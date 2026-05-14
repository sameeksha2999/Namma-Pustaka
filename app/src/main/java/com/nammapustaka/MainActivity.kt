package com.nammapustaka

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class MainActivity : Activity() {
    private lateinit var db: LibraryDatabase
    private lateinit var prefs: android.content.SharedPreferences
    private val green = Color.rgb(27, 94, 32)
    private val gold = Color.rgb(255, 213, 79)
    private val bg = Color.rgb(247, 249, 244)
    private val red = Color.rgb(211, 47, 47)
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = LibraryDatabase(this)
        prefs = getSharedPreferences("namma_pustaka_prefs", MODE_PRIVATE)
        db.markOverdue()
        if (prefs.getInt("user_id", 0) == 0) showLogin() else showHome()
    }

    private fun showLogin() {
        val name = edit("Full name")
        val grade = edit("Class / Staff ID")
        val role = spinner(listOf("STUDENT", "TEACHER"))

        screen("Namma-Pustaka", "Smart Library Assistant for Rural Schools") {
            card {
                title("Create or open profile")
                add(name)
                add(space(8))
                add(grade)
                add(space(8))
                add(label("Role"))
                add(role)
                add(space(14))
                add(primaryButton("Continue") {
                    val cleanName = name.text.toString().trim()
                    val cleanGrade = grade.text.toString().trim()
                    val selectedRole = role.selectedItem.toString()
                    if (cleanName.isBlank() || cleanGrade.isBlank()) {
                        toast("Enter name and class/staff ID")
                        return@primaryButton
                    }
                    val user = db.findOrCreateUser(cleanName, cleanGrade, selectedRole)
                    prefs.edit()
                        .putInt("user_id", user.id)
                        .putString("user_name", user.name)
                        .putString("user_role", user.role)
                        .apply()
                    showHome()
                })
            }
        }
    }

    private fun showHome() {
        db.markOverdue()
        val user = currentUser() ?: return showLogin()
        if (user.role == "TEACHER") showTeacherDashboard(user) else showStudentHome(user)
    }

    private fun showStudentHome(user: User) {
        val active = db.activeTransactionsForUser(user.id)
        val overdue = active.count { it.status == "OVERDUE" }
        screen("Hello, ${user.name}", "Borrow, return, and review books offline") {
            statsRow(
                "Borrowed" to active.size.toString(),
                "Overdue" to overdue.toString(),
                "Limit" to "3"
            )
            navRow(
                "Catalog" to { showCatalog(user) },
                "Scan" to { showScanner(user) },
                "History" to { showHistory(user) }
            )
            title("Currently borrowed")
            if (active.isEmpty()) {
                empty("No active borrowed books")
            } else {
                active.forEach { tx ->
                    val book = db.book(tx.bookId) ?: return@forEach
                    transactionCard(tx, book, showReturn = true, showReview = false, user = user)
                }
            }
        }
    }

    private fun showCatalog(user: User, category: String = "ALL", query: String = "") {
        val search = edit("Search title or author").apply { setText(query) }
        val categorySpinner = spinner(listOf("ALL", "STORY", "SCIENCE", "HISTORY", "KANNADA", "BIOGRAPHY")).apply {
            setSelection((0 until count).firstOrNull { getItemAtPosition(it) == category } ?: 0)
        }
        screen("Book Catalog", "Find books and borrow by QR") {
            toolbarBack { showStudentHome(user) }
            add(search)
            add(space(8))
            add(categorySpinner)
            add(space(8))
            add(secondaryButton("Apply filters") {
                showCatalog(user, categorySpinner.selectedItem.toString(), search.text.toString())
            })
            add(space(12))
            val books = db.books(categorySpinner.selectedItem.toString(), search.text.toString())
            if (books.isEmpty()) empty("No books match this filter")
            books.forEach { book -> bookCard(book, user) }
        }
    }

    private fun showScanner(user: User) {
        val qrInput = edit("QR code data, for example NP_BOOK_1_demo")
        screen("QR Scanner", "Offline scan simulator for borrow and return") {
            toolbarBack { showStudentHome(user) }
            card {
                title("Enter scanned QR")
                add(qrInput)
                add(space(10))
                add(primaryButton("Borrow / Return") {
                    handleQr(user, qrInput.text.toString().trim())
                })
            }
            title("Available QR codes")
            db.books("ALL", "").forEach { book ->
                add(secondaryButton("${book.title}  |  ${book.qr}") {
                    qrInput.setText(book.qr)
                    handleQr(user, book.qr)
                })
                add(space(8))
            }
        }
    }

    private fun handleQr(user: User, qr: String) {
        if (!qr.startsWith("NP_BOOK_")) {
            toast("Invalid Namma-Pustaka QR")
            return
        }
        val book = db.bookByQr(qr)
        if (book == null) {
            toast("Book not found")
            return
        }
        val active = db.activeTransaction(user.id, book.id)
        if (active != null) {
            db.returnBook(active.id, qr)
            showSuccess("Book returned", "${book.title} is back in the library.") { showReview(user, book) }
            return
        }
        val borrowedCount = db.activeTransactionsForUser(user.id).size
        when {
            borrowedCount >= 3 -> toast("Borrow limit reached")
            book.available <= 0 -> toast("No copies available")
            else -> {
                val due = db.borrowBook(user.id, book.id, qr)
                showSuccess("Book borrowed", "Return by ${dateFormat.format(Date(due))}.") { showStudentHome(user) }
            }
        }
    }

    private fun showHistory(user: User) {
        val history = db.transactionsForUser(user.id)
        screen("Reading History", "Returned books can be reviewed") {
            toolbarBack { showStudentHome(user) }
            if (history.isEmpty()) empty("No transactions yet")
            history.forEach { tx ->
                val book = db.book(tx.bookId) ?: return@forEach
                transactionCard(tx, book, showReturn = false, showReview = tx.status == "RETURNED", user = user)
            }
        }
    }

    private fun showReview(user: User, book: Book) {
        val rating = spinner(listOf("5", "4", "3", "2", "1"))
        val review = edit("One-line review")
        screen("Review ${book.title}", "Help classmates choose their next book") {
            toolbarBack { showHistory(user) }
            card {
                title(book.title)
                add(label("Rating"))
                add(rating)
                add(space(8))
                add(review)
                add(space(12))
                add(primaryButton("Save review") {
                    val text = review.text.toString().trim()
                    if (text.isBlank() || text.length > 100) {
                        toast("Review should be 1 to 100 characters")
                        return@primaryButton
                    }
                    db.saveReview(user.id, book.id, rating.selectedItem.toString().toInt(), text)
                    toast("Review saved")
                    showHistory(user)
                })
            }
        }
    }

    private fun showTeacherDashboard(user: User) {
        val books = db.books("ALL", "")
        val active = db.allActiveTransactions()
        val overdue = active.filter { it.status == "OVERDUE" }
        screen("Teacher Dashboard", "Manage books, QR codes, and overdue returns") {
            statsRow(
                "Books" to books.size.toString(),
                "Borrowed" to active.size.toString(),
                "Overdue" to overdue.size.toString()
            )
            navRow(
                "Add Book" to { showAddBook(user) },
                "All Books" to { showTeacherBooks(user) },
                "Logout" to { logout() }
            )
            title("Overdue list")
            if (overdue.isEmpty()) empty("No overdue books")
            overdue.forEach { tx ->
                val book = db.book(tx.bookId) ?: return@forEach
                val student = db.user(tx.userId)?.name ?: "Student"
                card(accent = red) {
                    title(book.title)
                    add(label("$student - due ${dateFormat.format(Date(tx.dueDate))}", red))
                }
            }
        }
    }

    private fun showTeacherBooks(user: User) {
        screen("All Books", "Tap a book to see its QR data") {
            toolbarBack { showTeacherDashboard(user) }
            db.books("ALL", "").forEach { book ->
                card {
                    title(book.title)
                    add(label("${book.author} - ${book.category}"))
                    add(label("Copies: ${book.available}/${book.total}"))
                    add(label("QR: ${book.qr}"))
                    add(label("Rating: ${book.ratingLabel()}"))
                }
            }
        }
    }

    private fun showAddBook(user: User) {
        val title = edit("Book title")
        val author = edit("Author")
        val category = spinner(listOf("STORY", "SCIENCE", "HISTORY", "KANNADA", "BIOGRAPHY"))
        val description = edit("Short description")
        val copies = edit("Total copies").apply { inputType = android.text.InputType.TYPE_CLASS_NUMBER }
        screen("Add Book", "Create a book and QR code") {
            toolbarBack { showTeacherDashboard(user) }
            card {
                add(title)
                add(space(8))
                add(author)
                add(space(8))
                add(category)
                add(space(8))
                add(description)
                add(space(8))
                add(copies)
                add(space(12))
                add(primaryButton("Save book") {
                    val count = copies.text.toString().toIntOrNull() ?: 0
                    if (title.text.isBlank() || author.text.isBlank() || count <= 0) {
                        toast("Enter title, author, and valid copies")
                        return@primaryButton
                    }
                    val book = db.addBook(
                        title.text.toString().trim(),
                        author.text.toString().trim(),
                        category.selectedItem.toString(),
                        description.text.toString().trim(),
                        count,
                        user.id
                    )
                    showQrDisplay(user, book)
                })
            }
        }
    }

    private fun showQrDisplay(user: User, book: Book) {
        screen("QR Ready", "Print or write this code on the book label") {
            toolbarBack { showTeacherDashboard(user) }
            card {
                title(book.title)
                add(bigCode(book.qr))
                add(label("Copies: ${book.total}"))
                add(space(12))
                add(primaryButton("Done") { showTeacherDashboard(user) })
            }
        }
    }

    private fun LinearLayout.bookCard(book: Book, user: User) {
        card(accent = if (book.available > 0) green else red) {
            title(book.title)
            add(label(book.author))
            add(label(book.category))
            add(label(book.description.ifBlank { "No description added" }))
            add(label("Available: ${book.available}/${book.total}"))
            add(label("Rating: ${book.ratingLabel()}"))
            add(space(8))
            add(primaryButton(if (book.available > 0) "Borrow with QR" else "Unavailable") {
                if (book.available > 0) handleQr(user, book.qr)
            }.apply { isEnabled = book.available > 0 })
        }
    }

    private fun LinearLayout.transactionCard(tx: Transaction, book: Book, showReturn: Boolean, showReview: Boolean, user: User) {
        val isOverdue = tx.status == "OVERDUE"
        card(accent = if (isOverdue) red else green) {
            title(book.title)
            add(label("${tx.status} - borrowed ${dateFormat.format(Date(tx.borrowDate))}", if (isOverdue) red else Color.DKGRAY))
            add(label("Due ${dateFormat.format(Date(tx.dueDate))}"))
            tx.returnDate?.let { add(label("Returned ${dateFormat.format(Date(it))}")) }
            if (showReturn) {
                add(space(8))
                add(primaryButton("Return") { handleQr(user, book.qr) })
            }
            if (showReview) {
                add(space(8))
                add(secondaryButton("Write review") { showReview(user, book) })
            }
        }
    }

    private fun currentUser(): User? = prefs.getInt("user_id", 0).takeIf { it > 0 }?.let { db.user(it) }

    private fun logout() {
        prefs.edit().clear().apply()
        showLogin()
    }

    private fun showSuccess(title: String, message: String, next: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { _, _ -> next() }
            .show()
    }

    private fun toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun screen(heading: String, subheading: String, content: LinearLayout.() -> Unit) {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(22), dp(20), dp(18))
            setBackgroundColor(green)
            addView(TextView(this@MainActivity).apply {
                text = heading
                textSize = 28f
                setTextColor(Color.WHITE)
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            })
            addView(TextView(this@MainActivity).apply {
                text = subheading
                textSize = 15f
                setTextColor(Color.rgb(235, 245, 232))
            })
        }
        val body = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(24))
            content()
        }
        root.addView(header)
        root.addView(ScrollView(this).apply { addView(body) }, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
    }

    private fun LinearLayout.card(accent: Int = green, content: LinearLayout.() -> Unit) {
        val box = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = android.graphics.drawable.GradientDrawable().apply {
                setColor(Color.WHITE)
                setStroke(dp(2), accent)
                cornerRadius = dp(8).toFloat()
            }
            content()
        }
        addView(box, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 0, 0, dp(12)) })
    }

    private fun LinearLayout.add(view: View) {
        addView(view, view.layoutParams ?: LinearLayout.LayoutParams(-1, -2))
    }

    private fun LinearLayout.title(text: String) {
        addView(TextView(this@MainActivity).apply {
            this.text = text
            textSize = 20f
            setTextColor(green)
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setPadding(0, dp(4), 0, dp(8))
        })
    }

    private fun LinearLayout.empty(text: String) {
        addView(label(text).apply {
            gravity = Gravity.CENTER
            setPadding(0, dp(24), 0, dp(24))
        })
    }

    private fun LinearLayout.toolbarBack(action: () -> Unit) {
        addView(secondaryButton("Back", action), LinearLayout.LayoutParams(-1, dp(44)).apply { setMargins(0, 0, 0, dp(12)) })
    }

    private fun LinearLayout.statsRow(vararg stats: Pair<String, String>) {
        val row = LinearLayout(this@MainActivity).apply {
            orientation = LinearLayout.HORIZONTAL
            weightSum = stats.size.toFloat()
        }
        stats.forEach { (name, value) ->
            row.addView(LinearLayout(this@MainActivity).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(dp(8), dp(12), dp(8), dp(12))
                background = android.graphics.drawable.GradientDrawable().apply {
                    setColor(Color.WHITE)
                    setStroke(dp(1), Color.rgb(220, 230, 218))
                    cornerRadius = dp(8).toFloat()
                }
                addView(TextView(this@MainActivity).apply {
                    text = value
                    textSize = 24f
                    setTextColor(green)
                    setTypeface(typeface, android.graphics.Typeface.BOLD)
                    gravity = Gravity.CENTER
                })
                addView(TextView(this@MainActivity).apply {
                    text = name
                    textSize = 13f
                    setTextColor(Color.DKGRAY)
                    gravity = Gravity.CENTER
                })
            }, LinearLayout.LayoutParams(0, -2, 1f).apply { setMargins(dp(3), 0, dp(3), dp(12)) })
        }
        addView(row)
    }

    private fun LinearLayout.navRow(vararg actions: Pair<String, () -> Unit>) {
        actions.forEach { (text, action) ->
            addView(primaryButton(text, action), LinearLayout.LayoutParams(-1, dp(48)).apply { setMargins(0, 0, 0, dp(8)) })
        }
    }

    private fun primaryButton(text: String, action: () -> Unit) = Button(this).apply {
        this.text = text
        setTextColor(Color.rgb(20, 30, 15))
        setBackgroundColor(gold)
        setOnClickListener { action() }
    }

    private fun secondaryButton(text: String, action: () -> Unit) = Button(this).apply {
        this.text = text
        setTextColor(Color.WHITE)
        setBackgroundColor(green)
        setOnClickListener { action() }
    }

    private fun label(text: String, color: Int = Color.DKGRAY) = TextView(this).apply {
        this.text = text
        textSize = 15f
        setTextColor(color)
        setPadding(0, dp(3), 0, dp(3))
    }

    private fun edit(hint: String) = EditText(this).apply {
        this.hint = hint
        minHeight = dp(48)
        setSingleLine(false)
        background = android.graphics.drawable.GradientDrawable().apply {
            setColor(Color.WHITE)
            setStroke(dp(1), Color.rgb(190, 205, 185))
            cornerRadius = dp(8).toFloat()
        }
        setPadding(dp(12), 0, dp(12), 0)
    }

    private fun spinner(items: List<String>) = Spinner(this).apply {
        adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, items)
        onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) = Unit
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    private fun bigCode(text: String) = TextView(this).apply {
        this.text = text
        textSize = 19f
        setTextColor(Color.BLACK)
        setTypeface(typeface, android.graphics.Typeface.BOLD)
        gravity = Gravity.CENTER
        setPadding(dp(12), dp(24), dp(12), dp(24))
        background = android.graphics.drawable.GradientDrawable().apply {
            setColor(Color.rgb(245, 245, 245))
            setStroke(dp(2), Color.BLACK)
            cornerRadius = dp(6).toFloat()
        }
        layoutParams = ViewGroup.LayoutParams(-1, -2)
    }

    private fun space(height: Int) = View(this).apply { layoutParams = LinearLayout.LayoutParams(1, dp(height)) }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).roundToInt()
}

data class User(val id: Int, val name: String, val grade: String, val role: String)

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val category: String,
    val description: String,
    val qr: String,
    val total: Int,
    val available: Int,
    val rating: Float,
    val reviews: Int
) {
    fun ratingLabel(): String = if (reviews == 0) "No reviews" else "${"%.1f".format(rating)} ($reviews)"
}

data class Transaction(
    val id: Int,
    val userId: Int,
    val bookId: Int,
    val borrowDate: Long,
    val dueDate: Long,
    val returnDate: Long?,
    val status: String
)

class LibraryDatabase(context: Context) : SQLiteOpenHelper(context, "namma_pustaka.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE users(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                grade TEXT NOT NULL,
                role TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                active INTEGER NOT NULL DEFAULT 1
            )"""
        )
        db.execSQL(
            """CREATE TABLE books(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                category TEXT NOT NULL,
                description TEXT NOT NULL,
                qr TEXT NOT NULL UNIQUE,
                total INTEGER NOT NULL,
                available INTEGER NOT NULL,
                added_by INTEGER NOT NULL,
                added_at INTEGER NOT NULL,
                active INTEGER NOT NULL DEFAULT 1,
                rating REAL NOT NULL DEFAULT 0,
                reviews INTEGER NOT NULL DEFAULT 0
            )"""
        )
        db.execSQL(
            """CREATE TABLE transactions(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                book_id INTEGER NOT NULL,
                borrow_date INTEGER NOT NULL,
                due_date INTEGER NOT NULL,
                return_date INTEGER,
                status TEXT NOT NULL,
                qr_borrow TEXT NOT NULL,
                qr_return TEXT
            )"""
        )
        db.execSQL(
            """CREATE TABLE reviews(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                book_id INTEGER NOT NULL,
                rating INTEGER NOT NULL,
                text TEXT NOT NULL,
                reviewed_at INTEGER NOT NULL,
                UNIQUE(user_id, book_id)
            )"""
        )
        seed(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = Unit

    fun findOrCreateUser(name: String, grade: String, role: String): User {
        readableDatabase.rawQuery(
            "SELECT * FROM users WHERE lower(name)=lower(?) AND grade=? AND role=? AND active=1 LIMIT 1",
            arrayOf(name, grade, role)
        ).use { if (it.moveToFirst()) return userFrom(it) }
        val id = writableDatabase.insert("users", null, ContentValues().apply {
            put("name", name)
            put("grade", grade)
            put("role", role)
            put("created_at", System.currentTimeMillis())
            put("active", 1)
        }).toInt()
        return User(id, name, grade, role)
    }

    fun user(id: Int): User? = readableDatabase.rawQuery("SELECT * FROM users WHERE id=?", arrayOf(id.toString()))
        .use { if (it.moveToFirst()) userFrom(it) else null }

    fun books(category: String, query: String): List<Book> {
        val args = mutableListOf<String>()
        val where = StringBuilder("active=1")
        if (category != "ALL") {
            where.append(" AND category=?")
            args.add(category)
        }
        if (query.isNotBlank()) {
            where.append(" AND (lower(title) LIKE lower(?) OR lower(author) LIKE lower(?))")
            args.add("%$query%")
            args.add("%$query%")
        }
        return readableDatabase.rawQuery(
            "SELECT * FROM books WHERE $where ORDER BY title",
            args.toTypedArray()
        ).use { cursor -> buildList { while (cursor.moveToNext()) add(bookFrom(cursor)) } }
    }

    fun book(id: Int): Book? = readableDatabase.rawQuery("SELECT * FROM books WHERE id=?", arrayOf(id.toString()))
        .use { if (it.moveToFirst()) bookFrom(it) else null }

    fun bookByQr(qr: String): Book? = readableDatabase.rawQuery("SELECT * FROM books WHERE qr=? AND active=1", arrayOf(qr))
        .use { if (it.moveToFirst()) bookFrom(it) else null }

    fun addBook(title: String, author: String, category: String, description: String, copies: Int, teacherId: Int): Book {
        val values = ContentValues().apply {
            put("title", title)
            put("author", author)
            put("category", category)
            put("description", description)
            put("qr", "NP_BOOK_${UUID.randomUUID().toString().take(8)}")
            put("total", copies)
            put("available", copies)
            put("added_by", teacherId)
            put("added_at", System.currentTimeMillis())
            put("active", 1)
            put("rating", 0)
            put("reviews", 0)
        }
        val id = writableDatabase.insert("books", null, values).toInt()
        return book(id)!!
    }

    fun activeTransactionsForUser(userId: Int): List<Transaction> {
        markOverdue()
        return readableDatabase.rawQuery(
            "SELECT * FROM transactions WHERE user_id=? AND status IN ('ACTIVE','OVERDUE') ORDER BY due_date",
            arrayOf(userId.toString())
        ).use { cursor -> buildList { while (cursor.moveToNext()) add(txFrom(cursor)) } }
    }

    fun transactionsForUser(userId: Int): List<Transaction> {
        markOverdue()
        return readableDatabase.rawQuery(
            "SELECT * FROM transactions WHERE user_id=? ORDER BY borrow_date DESC",
            arrayOf(userId.toString())
        ).use { cursor -> buildList { while (cursor.moveToNext()) add(txFrom(cursor)) } }
    }

    fun allActiveTransactions(): List<Transaction> {
        markOverdue()
        return readableDatabase.rawQuery(
            "SELECT * FROM transactions WHERE status IN ('ACTIVE','OVERDUE') ORDER BY due_date",
            emptyArray()
        ).use { cursor -> buildList { while (cursor.moveToNext()) add(txFrom(cursor)) } }
    }

    fun activeTransaction(userId: Int, bookId: Int): Transaction? {
        markOverdue()
        return readableDatabase.rawQuery(
            "SELECT * FROM transactions WHERE user_id=? AND book_id=? AND status IN ('ACTIVE','OVERDUE') LIMIT 1",
            arrayOf(userId.toString(), bookId.toString())
        ).use { if (it.moveToFirst()) txFrom(it) else null }
    }

    fun borrowBook(userId: Int, bookId: Int, qr: String): Long {
        val now = System.currentTimeMillis()
        val due = now + TimeUnit.DAYS.toMillis(14)
        writableDatabase.beginTransaction()
        try {
            writableDatabase.insert("transactions", null, ContentValues().apply {
                put("user_id", userId)
                put("book_id", bookId)
                put("borrow_date", now)
                put("due_date", due)
                putNull("return_date")
                put("status", "ACTIVE")
                put("qr_borrow", qr)
            })
            writableDatabase.execSQL("UPDATE books SET available=available-1 WHERE id=? AND available>0", arrayOf(bookId))
            writableDatabase.setTransactionSuccessful()
        } finally {
            writableDatabase.endTransaction()
        }
        return due
    }

    fun returnBook(transactionId: Int, qr: String) {
        val tx = readableDatabase.rawQuery("SELECT * FROM transactions WHERE id=?", arrayOf(transactionId.toString()))
            .use { if (it.moveToFirst()) txFrom(it) else return }
        writableDatabase.beginTransaction()
        try {
            writableDatabase.update("transactions", ContentValues().apply {
                put("return_date", System.currentTimeMillis())
                put("status", "RETURNED")
                put("qr_return", qr)
            }, "id=?", arrayOf(transactionId.toString()))
            writableDatabase.execSQL("UPDATE books SET available=available+1 WHERE id=? AND available<total", arrayOf(tx.bookId))
            writableDatabase.setTransactionSuccessful()
        } finally {
            writableDatabase.endTransaction()
        }
    }

    fun saveReview(userId: Int, bookId: Int, rating: Int, text: String) {
        writableDatabase.insertWithOnConflict("reviews", null, ContentValues().apply {
            put("user_id", userId)
            put("book_id", bookId)
            put("rating", rating)
            put("text", text)
            put("reviewed_at", System.currentTimeMillis())
        }, SQLiteDatabase.CONFLICT_REPLACE)
        readableDatabase.rawQuery("SELECT AVG(rating), COUNT(*) FROM reviews WHERE book_id=?", arrayOf(bookId.toString())).use {
            if (it.moveToFirst()) {
                writableDatabase.update("books", ContentValues().apply {
                    put("rating", it.getFloat(0))
                    put("reviews", it.getInt(1))
                }, "id=?", arrayOf(bookId.toString()))
            }
        }
    }

    fun markOverdue() {
        writableDatabase.execSQL(
            "UPDATE transactions SET status='OVERDUE' WHERE status='ACTIVE' AND due_date<?",
            arrayOf(System.currentTimeMillis())
        )
    }

    private fun seed(db: SQLiteDatabase) {
        db.insert("users", null, ContentValues().apply {
            put("name", "Library Teacher")
            put("grade", "STAFF")
            put("role", "TEACHER")
            put("created_at", System.currentTimeMillis())
            put("active", 1)
        })
        sample("The Jungle Book", "Rudyard Kipling", "STORY", "A boy raised by wolves discovers courage and friendship.", 3, db)
        sample("Wings of Fire", "A.P.J. Abdul Kalam", "BIOGRAPHY", "The inspiring life story of India's missile man.", 2, db)
        sample("Wonderful World of Science", "Various Authors", "SCIENCE", "Simple science ideas and experiments for curious students.", 4, db)
        sample("Karnataka Charitre", "School Library", "HISTORY", "A friendly introduction to Karnataka history.", 2, db)
        sample("Kannada Kathegalu", "Various Authors", "KANNADA", "Short Kannada stories for daily reading practice.", 5, db)
    }

    private fun sample(title: String, author: String, category: String, description: String, copies: Int, db: SQLiteDatabase) {
        db.insert("books", null, ContentValues().apply {
            put("title", title)
            put("author", author)
            put("category", category)
            put("description", description)
            put("qr", "NP_BOOK_${title.filter { it.isLetterOrDigit() }.take(6).uppercase(Locale.ENGLISH)}")
            put("total", copies)
            put("available", copies)
            put("added_by", 1)
            put("added_at", System.currentTimeMillis())
            put("active", 1)
            put("rating", 0)
            put("reviews", 0)
        })
    }

    private fun userFrom(c: Cursor) = User(
        id = c.getInt(c.getColumnIndexOrThrow("id")),
        name = c.getString(c.getColumnIndexOrThrow("name")),
        grade = c.getString(c.getColumnIndexOrThrow("grade")),
        role = c.getString(c.getColumnIndexOrThrow("role"))
    )

    private fun bookFrom(c: Cursor) = Book(
        id = c.getInt(c.getColumnIndexOrThrow("id")),
        title = c.getString(c.getColumnIndexOrThrow("title")),
        author = c.getString(c.getColumnIndexOrThrow("author")),
        category = c.getString(c.getColumnIndexOrThrow("category")),
        description = c.getString(c.getColumnIndexOrThrow("description")),
        qr = c.getString(c.getColumnIndexOrThrow("qr")),
        total = c.getInt(c.getColumnIndexOrThrow("total")),
        available = c.getInt(c.getColumnIndexOrThrow("available")),
        rating = c.getFloat(c.getColumnIndexOrThrow("rating")),
        reviews = c.getInt(c.getColumnIndexOrThrow("reviews"))
    )

    private fun txFrom(c: Cursor) = Transaction(
        id = c.getInt(c.getColumnIndexOrThrow("id")),
        userId = c.getInt(c.getColumnIndexOrThrow("user_id")),
        bookId = c.getInt(c.getColumnIndexOrThrow("book_id")),
        borrowDate = c.getLong(c.getColumnIndexOrThrow("borrow_date")),
        dueDate = c.getLong(c.getColumnIndexOrThrow("due_date")),
        returnDate = if (c.isNull(c.getColumnIndexOrThrow("return_date"))) null else c.getLong(c.getColumnIndexOrThrow("return_date")),
        status = c.getString(c.getColumnIndexOrThrow("status"))
    )
}
