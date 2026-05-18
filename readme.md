# NAMMA-PUSTAKA SMART LIBRARY ASSISTANT
## Standard Operating Procedure (SOP) & Technical Specification Document

---

# TABLE OF CONTENTS

1. Project Overview & Vision
2. Technical Architecture
3. Database Schema & Design
4. Screen-by-Screen UI/UX Specifications
5. Screen Connection & Navigation Flow
6. Backend Logic & Business Rules
7. QR Code System
8. Prompts for Each Screen
9. API & Data Layer Specifications
10. Error Handling & Edge Cases
11. Testing Guidelines
12. Deployment Instructions

---

# SECTION 1: PROJECT OVERVIEW & VISION

## 1.1 Project Name
**Namma-Pustaka** (Meaning: "Our Library" in Kannada)
**Tagline:** Smart Library Assistant for Rural Schools

## 1.2 Problem Statement
Rural schools in India lack a proper library management system. Physical registers are used to track borrowed books, which leads to:
- Lost records
- No overdue tracking
- No reading culture encouragement
- Teachers spending hours manually managing records
- Students having no motivation to read or review books

## 1.3 Solution
A lightweight Android mobile application that digitizes the entire library management process using:
- Simple name-based login (no passwords)
- QR code scanning for borrowing/returning
- Automatic overdue detection
- Peer review & rating system
- Local Room Database (works offline)
- Teacher/Librarian dashboard

## 1.4 Target Users
| User Type | Description |
|-----------|-------------|
| Students | Age 10-18, rural school students, basic smartphone usage |
| Teachers/Librarians | School staff managing library operations |

## 1.5 Core Principles
- **Simplicity First:** No complex login, no internet required
- **Offline First:** All data stored locally using Room Database
- **Visual First:** Images, colors, icons for easy understanding
- **Lightweight:** Minimal permissions, fast loading
- **Regional:** Supports Kannada + English language

## 1.6 Platform
- **Platform:** Android (Native)
- **Language:** Kotlin
- **Minimum SDK:** Android 6.0 (API 23)
- **Target SDK:** Android 14 (API 34)
- **Architecture:** MVVM (Model-View-ViewModel)

---

# SECTION 2: TECHNICAL ARCHITECTURE

## 2.1 Technology Stack

```
┌─────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                    │
│         Activities + Fragments + XML Layouts            │
│              ViewBinding + LiveData Observers            │
└─────────────────────────────────────────────────────────┘
                           │
┌─────────────────────────────────────────────────────────┐
│                    VIEWMODEL LAYER                       │
│         AndroidViewModel + LiveData + StateFlow          │
│              Business Logic + UI State Management        │
└─────────────────────────────────────────────────────────┘
                           │
┌─────────────────────────────────────────────────────────┐
│                    REPOSITORY LAYER                      │
│              Data Access Objects (DAOs)                  │
│         Coroutines for Async Operations                  │
└─────────────────────────────────────────────────────────┘
                           │
┌─────────────────────────────────────────────────────────┐
│                    DATABASE LAYER                        │
│                  Room Database v2.6                      │
│         SQLite Local Storage (No Internet Needed)        │
└─────────────────────────────────────────────────────────┘
```

## 2.2 Libraries & Dependencies

```gradle
// Core Android
implementation 'androidx.core:core-ktx:1.12.0'
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'

// Navigation Component
implementation 'androidx.navigation:navigation-fragment-ktx:2.7.6'
implementation 'androidx.navigation:navigation-ui-ktx:2.7.6'

// Room Database
implementation 'androidx.room:room-runtime:2.6.1'
implementation 'androidx.room:room-ktx:2.6.1'
kapt 'androidx.room:room-compiler:2.6.1'

// ViewModel & LiveData
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'
implementation 'androidx.lifecycle:lifecycle-livedata-ktx:2.7.0'

// QR Code Scanning
implementation 'com.google.mlkit:barcode-scanning:17.2.0'
implementation 'androidx.camera:camera-camera2:1.3.1'
implementation 'androidx.camera:camera-lifecycle:1.3.1'
implementation 'androidx.camera:camera-view:1.3.1'

// QR Code Generation
implementation 'com.google.zxing:core:3.5.2'

// Glide (Image Loading)
implementation 'com.github.bumptech.glide:glide:4.16.0'
kapt 'com.github.bumptech.glide:compiler:4.16.0'

// Coroutines
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3'

// PDF Generation
implementation 'com.itextpdf:itext7-core:7.2.5'

// RecyclerView
implementation 'androidx.recyclerview:recyclerview:1.3.2'

// CardView
implementation 'androidx.cardview:cardview:1.0.0'

// SharedPreferences (for session)
implementation 'androidx.preference:preference-ktx:1.2.1'
```

## 2.3 Project Folder Structure

```
app/
├── src/main/
│   ├── java/com/nammapustaka/
│   │   ├── MainActivity.kt
│   │   ├── data/
│   │   │   ├── database/
│   │   │   │   ├── NammaPustakaDatabase.kt
│   │   │   │   └── DatabaseMigrations.kt
│   │   │   ├── dao/
│   │   │   │   ├── UserDao.kt
│   │   │   │   ├── BookDao.kt
│   │   │   │   ├── TransactionDao.kt
│   │   │   │   └── ReviewDao.kt
│   │   │   ├── entities/
│   │   │   │   ├── User.kt
│   │   │   │   ├── Book.kt
│   │   │   │   ├── BorrowTransaction.kt
│   │   │   │   └── BookReview.kt
│   │   │   └── repository/
│   │   │       ├── UserRepository.kt
│   │   │       ├── BookRepository.kt
│   │   │       ├── TransactionRepository.kt
│   │   │       └── ReviewRepository.kt
│   │   ├── ui/
│   │   │   ├── splash/
│   │   │   │   └── SplashActivity.kt
│   │   │   ├── onboarding/
│   │   │   │   ├── OnboardingActivity.kt
│   │   │   │   └── OnboardingFragment.kt
│   │   │   ├── profile/
│   │   │   │   ├── ProfileSetupActivity.kt
│   │   │   │   └── ProfileSetupViewModel.kt
│   │   │   ├── home/
│   │   │   │   ├── HomeActivity.kt
│   │   │   │   ├── HomeFragment.kt
│   │   │   │   └── HomeViewModel.kt
│   │   │   ├── catalog/
│   │   │   │   ├── CatalogFragment.kt
│   │   │   │   ├── CatalogViewModel.kt
│   │   │   │   └── BookAdapter.kt
│   │   │   ├── bookdetail/
│   │   │   │   ├── BookDetailActivity.kt
│   │   │   │   └── BookDetailViewModel.kt
│   │   │   ├── scanner/
│   │   │   │   ├── QRScannerActivity.kt
│   │   │   │   └── QRScannerViewModel.kt
│   │   │   ├── history/
│   │   │   │   ├── HistoryFragment.kt
│   │   │   │   └── HistoryViewModel.kt
│   │   │   ├── review/
│   │   │   │   ├── ReviewFragment.kt
│   │   │   │   └── ReviewViewModel.kt
│   │   │   ├── teacher/
│   │   │   │   ├── TeacherDashboardActivity.kt
│   │   │   │   ├── TeacherDashboardViewModel.kt
│   │   │   │   ├── AddBookActivity.kt
│   │   │   │   └── AddBookViewModel.kt
│   │   │   └── common/
│   │   │       ├── BaseActivity.kt
│   │   │       └── BaseFragment.kt
│   │   └── utils/
│   │       ├── QRCodeGenerator.kt
│   │       ├── QRCodeScanner.kt
│   │       ├── DateUtils.kt
│   │       ├── ImageUtils.kt
│   │       └── Constants.kt
│   └── res/
│       ├── layout/
│       ├── drawable/
│       ├── values/
│       ├── navigation/
│       └── menu/
```

---

# SECTION 3: DATABASE SCHEMA & DESIGN

## 3.1 Entity: User

```kotlin
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    
    @ColumnInfo(name = "full_name")
    val fullName: String,           // Student/Teacher full name
    
    @ColumnInfo(name = "class_grade")
    val classGrade: String,         // "Class 6", "Class 7", "Teacher"
    
    @ColumnInfo(name = "roll_number")
    val rollNumber: String,         // Roll number or staff ID
    
    @ColumnInfo(name = "profile_image_path")
    val profileImagePath: String?,  // Local image path (optional)
    
    @ColumnInfo(name = "user_type")
    val userType: String,           // "STUDENT" or "TEACHER"
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long,            // Timestamp in milliseconds
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true    // Soft delete support
)
```

## 3.2 Entity: Book

```kotlin
@Entity(tableName = "books")
data class Book(
    @PrimaryKey(autoGenerate = true)
    val bookId: Int = 0,
    
    @ColumnInfo(name = "title")
    val title: String,              // Book title
    
    @ColumnInfo(name = "author")
    val author: String,             // Author name
    
    @ColumnInfo(name = "category")
    val category: String,           // "STORY", "SCIENCE", "HISTORY"
    
    @ColumnInfo(name = "description")
    val description: String,        // Short description
    
    @ColumnInfo(name = "cover_image_path")
    val coverImagePath: String?,    // Local image path
    
    @ColumnInfo(name = "qr_code_data")
    val qrCodeData: String,         // Unique QR data string
    
    @ColumnInfo(name = "total_copies")
    val totalCopies: Int,           // Total physical copies
    
    @ColumnInfo(name = "available_copies")
    val availableCopies: Int,       // Currently available
    
    @ColumnInfo(name = "added_by_user_id")
    val addedByUserId: Int,         // Teacher who added it
    
    @ColumnInfo(name = "added_at")
    val addedAt: Long,              // Timestamp
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,
    
    @ColumnInfo(name = "average_rating")
    val averageRating: Float = 0f,  // Calculated average
    
    @ColumnInfo(name = "total_reviews")
    val totalReviews: Int = 0       // Total review count
)
```

## 3.3 Entity: BorrowTransaction

```kotlin
@Entity(
    tableName = "borrow_transactions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Book::class,
            parentColumns = ["bookId"],
            childColumns = ["book_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["book_id"])
    ]
)
data class BorrowTransaction(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Int = 0,
    
    @ColumnInfo(name = "user_id")
    val userId: Int,                // Foreign key to User
    
    @ColumnInfo(name = "book_id")
    val bookId: Int,                // Foreign key to Book
    
    @ColumnInfo(name = "borrow_date")
    val borrowDate: Long,           // Timestamp when borrowed
    
    @ColumnInfo(name = "due_date")
    val dueDate: Long,              // borrowDate + 14 days (in ms)
    
    @ColumnInfo(name = "return_date")
    val returnDate: Long?,          // Null until returned
    
    @ColumnInfo(name = "status")
    val status: String,             // "ACTIVE", "RETURNED", "OVERDUE"
    
    @ColumnInfo(name = "qr_scan_borrow")
    val qrScanBorrow: String,       // QR data at borrow time
    
    @ColumnInfo(name = "qr_scan_return")
    val qrScanReturn: String?       // QR data at return time
)
```

## 3.4 Entity: BookReview

```kotlin
@Entity(
    tableName = "book_reviews",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["user_id"]
        ),
        ForeignKey(
            entity = Book::class,
            parentColumns = ["bookId"],
            childColumns = ["book_id"]
        )
    ],
    indices = [
        Index(value = ["user_id", "book_id"], unique = true)
    ]
)
data class BookReview(
    @PrimaryKey(autoGenerate = true)
    val reviewId: Int = 0,
    
    @ColumnInfo(name = "user_id")
    val userId: Int,
    
    @ColumnInfo(name = "book_id")
    val bookId: Int,
    
    @ColumnInfo(name = "rating")
    val rating: Int,                // 1 to 5
    
    @ColumnInfo(name = "review_text")
    val reviewText: String,         // One-line feedback
    
    @ColumnInfo(name = "reviewed_at")
    val reviewedAt: Long            // Timestamp
)
```

## 3.5 DAO Interfaces

### UserDao.kt
```kotlin
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long
    
    @Query("SELECT * FROM users WHERE full_name LIKE :name AND class_grade = :grade LIMIT 1")
    suspend fun findUser(name: String, grade: String): User?
    
    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUserById(userId: Int): User?
    
    @Query("SELECT * FROM users WHERE user_type = 'STUDENT'")
    fun getAllStudents(): LiveData<List<User>>
    
    @Query("SELECT * FROM users WHERE user_type = 'TEACHER'")
    fun getAllTeachers(): LiveData<List<User>>
    
    @Update
    suspend fun updateUser(user: User)
    
    @Query("UPDATE users SET is_active = 0 WHERE userId = :userId")
    suspend fun deactivateUser(userId: Int)
}
```

### BookDao.kt
```kotlin
@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book): Long
    
    @Query("SELECT * FROM books WHERE is_active = 1")
    fun getAllBooks(): LiveData<List<Book>>
    
    @Query("SELECT * FROM books WHERE category = :category AND is_active = 1")
    fun getBooksByCategory(category: String): LiveData<List<Book>>
    
    @Query("""
        SELECT * FROM books 
        WHERE (title LIKE '%' || :query || '%' 
        OR author LIKE '%' || :query || '%') 
        AND is_active = 1
    """)
    fun searchBooks(query: String): LiveData<List<Book>>
    
    @Query("SELECT * FROM books WHERE bookId = :bookId")
    suspend fun getBookById(bookId: Int): Book?
    
    @Query("SELECT * FROM books WHERE qr_code_data = :qrData")
    suspend fun getBookByQRCode(qrData: String): Book?
    
    @Update
    suspend fun updateBook(book: Book)
    
    @Query("UPDATE books SET available_copies = available_copies - 1 WHERE bookId = :bookId")
    suspend fun decrementAvailability(bookId: Int)
    
    @Query("UPDATE books SET available_copies = available_copies + 1 WHERE bookId = :bookId")
    suspend fun incrementAvailability(bookId: Int)
    
    @Query("UPDATE books SET average_rating = :rating, total_reviews = :count WHERE bookId = :bookId")
    suspend fun updateBookRating(bookId: Int, rating: Float, count: Int)
}
```

### TransactionDao.kt
```kotlin
@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transaction: BorrowTransaction): Long
    
    @Query("SELECT * FROM borrow_transactions WHERE user_id = :userId ORDER BY borrow_date DESC")
    fun getUserTransactions(userId: Int): LiveData<List<BorrowTransaction>>
    
    @Query("SELECT * FROM borrow_transactions WHERE status = 'ACTIVE' OR status = 'OVERDUE'")
    fun getAllActiveTransactions(): LiveData<List<BorrowTransaction>>
    
    @Query("""
        SELECT * FROM borrow_transactions 
        WHERE user_id = :userId AND book_id = :bookId AND status = 'ACTIVE'
        LIMIT 1
    """)
    suspend fun getActiveTransaction(userId: Int, bookId: Int): BorrowTransaction?
    
    @Query("UPDATE borrow_transactions SET status = 'OVERDUE' WHERE due_date < :currentTime AND status = 'ACTIVE'")
    suspend fun markOverdueTransactions(currentTime: Long)
    
    @Query("""
        UPDATE borrow_transactions 
        SET return_date = :returnTime, status = 'RETURNED', qr_scan_return = :qrData 
        WHERE transactionId = :transactionId
    """)
    suspend fun returnBook(transactionId: Int, returnTime: Long, qrData: String)
    
    @Query("SELECT COUNT(*) FROM borrow_transactions WHERE status = 'OVERDUE'")
    fun getOverdueCount(): LiveData<Int>
    
    @Query("SELECT * FROM borrow_transactions WHERE status = 'OVERDUE'")
    fun getAllOverdueTransactions(): LiveData<List<BorrowTransaction>>
}
```

### ReviewDao.kt
```kotlin
@Dao
interface ReviewDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: BookReview): Long
    
    @Query("SELECT * FROM book_reviews WHERE book_id = :bookId ORDER BY reviewed_at DESC")
    fun getReviewsForBook(bookId: Int): LiveData<List<BookReview>>
    
    @Query("SELECT * FROM book_reviews WHERE user_id = :userId AND book_id = :bookId LIMIT 1")
    suspend fun getUserReviewForBook(userId: Int, bookId: Int): BookReview?
    
    @Query("SELECT AVG(rating) FROM book_reviews WHERE book_id = :bookId")
    suspend fun getAverageRating(bookId: Int): Float
    
    @Query("SELECT COUNT(*) FROM book_reviews WHERE book_id = :bookId")
    suspend fun getReviewCount(bookId: Int): Int
    
    @Update
    suspend fun updateReview(review: BookReview)
}
```

---

# SECTION 4: SCREEN-BY-SCREEN SPECIFICATIONS

## SCREEN 1: SPLASH SCREEN

### Screen ID: SCR-001
### Screen Name: SplashScreen
### File: SplashActivity.kt + activity_splash.xml

### Visual Design:
```
┌─────────────────────────────────┐
│                                 │
│                                 │
│                                 │
│         [APP LOGO/ICON]         │
│      📚 Large Book Icon         │
│                                 │
│      NAMMA-PUSTAKA              │
│   Smart Library Assistant       │
│                                 │
│      ──────────────             │
│                                 │
│    Loading... [Progress Bar]    │
│                                 │
│   "ಜ್ಞಾನವೇ ಶಕ್ತಿ"               │
│  "Knowledge is Power"           │
│                                 │
└─────────────────────────────────┘
```

### Design Specifications:
- **Background Color:** #1B5E20 (Deep Forest Green)
- **App Name Font:** Bold, 28sp, White (#FFFFFF)
- **Tagline Font:** Regular, 14sp, Light Green (#A5D6A7)
- **Kannada Quote:** Italic, 12sp, White with 70% opacity
- **Logo Size:** 120dp x 120dp, centered
- **Progress Bar:** Linear, bottom area, accent color #FFD54F (Golden Yellow)
- **Duration:** 2.5 seconds total

### Logic:
```
1. App launches → Show Splash Screen
2. After 500ms → Start checking SharedPreferences
3. Check if "current_user_id" exists in SharedPreferences
   - YES → Navigate to HomeActivity (skip profile setup)
   - NO → Navigate to OnboardingActivity
4. Run overdue check in background while splash shows
5. Total splash duration: 2500ms minimum
```

### Navigation:
- **On Timer Complete + User Exists:** → HomeActivity (SCR-004)
- **On Timer Complete + No User:** → OnboardingActivity (SCR-002)

---

## SCREEN 2: ONBOARDING SCREEN

### Screen ID: SCR-002
### Screen Name: OnboardingScreen
### File: OnboardingActivity.kt + activity_onboarding.xml

### Visual Design (3 slides with ViewPager2):

**Slide 1:**
```
┌─────────────────────────────────┐
│                                 │
│    [Illustration: Books]        │
│    📚 Colorful book stack       │
│                                 │
│  Welcome to Namma-Pustaka!      │
│                                 │
│  Your school library is now     │
│  available on your phone.       │
│  Borrow, return & discover      │
│  amazing books!                 │
│                                 │
│    ● ○ ○                        │
│                                 │
│           [NEXT →]              │
└─────────────────────────────────┘
```

**Slide 2:**
```
┌─────────────────────────────────┐
│                                 │
│  [Illustration: QR Scan]        │
│  📱 Phone scanning a book       │
│                                 │
│  Scan & Go!                     │
│                                 │
│  Just scan the QR code on       │
│  any book to borrow or          │
│  return it instantly.           │
│  No paperwork needed!           │
│                                 │
│    ○ ● ○                        │
│                                 │
│  [← BACK]      [NEXT →]        │
└─────────────────────────────────┘
```

**Slide 3:**
```
┌─────────────────────────────────┐
│                                 │
│  [Illustration: Stars/Review]   │
│  ⭐ Student rating a book       │
│                                 │
│  Read & Review!                 │
│                                 │
│  Rate books and help your       │
│  friends discover great         │
│  reads. Build a reading         │
│  culture together!              │
│                                 │
│    ○ ○ ●                        │
│                                 │
│  [← BACK]    [GET STARTED]      │
└─────────────────────────────────┘
```

### Design Specifications:
- **Background:** White (#FFFFFF)
- **Illustration Area:** 60% of screen height
- **Title Font:** Bold, 24sp, #1B5E20
- **Description Font:** Regular, 16sp, #424242
- **Indicator Dots:** 10dp diameter, active = #1B5E20, inactive = #E0E0E0
- **Next Button:** Filled, #1B5E20, white text, rounded corners 24dp
- **Back Button:** Outlined, #1B5E20
- **Get Started Button:** Filled, #FFD54F (Golden), Black text, larger 56dp height

### Logic:
```
1. ViewPager2 with 3 fragments
2. Indicator dots update on page change
3. "SKIP" button always visible top-right (except last slide)
4. SKIP → directly to ProfileSetupActivity
5. GET STARTED → ProfileSetupActivity
6. Store "onboarding_complete = true" in SharedPreferences
```

### Navigation:
- **GET STARTED / SKIP Button:** → ProfileSetupActivity (SCR-003)

---

## SCREEN 3: PROFILE SETUP SCREEN

### Screen ID: SCR-003
### Screen Name: ProfileSetupScreen
### File: ProfileSetupActivity.kt + activity_profile_setup.xml

### Visual Design:
```
┌─────────────────────────────────┐
│  ← Back                         │
│  Set Up Your Profile            │
│  ─────────────────────          │
│                                 │
│       [Camera Icon Circle]      │
│       👤 Tap to add photo       │
│       (Optional)                │
│                                 │
│  I am a...                      │
│  ┌──────────┐  ┌──────────┐    │
│  │  Student │  │  Teacher │    │
│  │    🎒    │  │    👩‍🏫    │    │
│  └──────────┘  └──────────┘    │
│                                 │
│  Full Name *                    │
│  ┌───────────────────────────┐  │
│  │ Enter your full name      │  │
│  └───────────────────────────┘  │
│                                 │
│  Class / Grade *                │
│  ┌───────────────────────────┐  │
│  │ Select Class ▼            │  │
│  └───────────────────────────┘  │
│                                 │
│  Roll Number / Staff ID *       │
│  ┌───────────────────────────┐  │
│  │ Enter roll number         │  │
│  └───────────────────────────┘  │
│                                 │
│  ┌───────────────────────────┐  │
│  │   CREATE MY PROFILE  ✓    │  │
│  └───────────────────────────┘  │
└─────────────────────────────────┘
```

### Design Specifications:
- **Header:** "Set Up Your Profile" — Bold, 22sp, #1B5E20
- **Sub text:** "No password needed! Just your name." — 14sp, #757575
- **Profile Image Circle:** 100dp diameter, dashed border, #1B5E20
- **User Type Selector:** Two cards side by side
  - Selected state: #1B5E20 background, white text, elevation 4dp
  - Unselected state: White background, #1B5E20 border
- **Input Fields:** OutlinedTextInputLayout, accent #1B5E20
- **Class Dropdown (Student only):** Class 1 through Class 12 + options
- **Class Dropdown (Teacher):** Replaced with "Subject" field
- **CREATE PROFILE Button:** Full width, #1B5E20, white text, 56dp height
- **Validation:** All * fields required, show red error message if empty

### Class Options Dropdown:
```
Class 1, Class 2, Class 3, Class 4, Class 5,
Class 6, Class 7, Class 8, Class 9, Class 10,
Class 11 (Science), Class 11 (Arts), 
Class 12 (Science), Class 12 (Arts)
```

### Teacher Subject Options:
```
Mathematics, Science, Kannada, English, 
Social Studies, Physical Education, Librarian, Principal
```

### Logic:
```
1. User selects Student or Teacher (default: Student)
2. If Student selected:
   - Show Class/Grade dropdown
   - Show Roll Number field
3. If Teacher selected:
   - Show Subject dropdown
   - Show Staff ID field
4. Profile photo: Optional, camera/gallery intent
5. On CREATE PROFILE:
   a. Validate all required fields
   b. Check if user already exists (same name + class)
   c. If exists → show dialog "Welcome back! Continue as [Name]?"
   d. If new → Insert into Room DB
   e. Save userId in SharedPreferences as "current_user_id"
   f. Save userType as "current_user_type"
   g. Navigate based on userType:
      - STUDENT → HomeActivity
      - TEACHER → TeacherDashboardActivity
```

### Validation Rules:
- Full Name: Min 2 characters, Max 50 characters, letters only
- Class: Must be selected from dropdown
- Roll Number: 1-20 characters, alphanumeric

### Navigation:
- **CREATE PROFILE (Student):** → HomeActivity (SCR-004)
- **CREATE PROFILE (Teacher):** → TeacherDashboardActivity (SCR-011)
- **Back Arrow:** → OnboardingActivity (SCR-002)

---

## SCREEN 4: HOME SCREEN (STUDENT)

### Screen ID: SCR-004
### Screen Name: StudentHomeScreen
### File: HomeFragment.kt + fragment_home.xml (inside HomeActivity)

### Visual Design:
```
┌─────────────────────────────────┐
│ Namma-Pustaka    🔔  👤         │
│ ─────────────────────────────   │
│                                 │
│ Good Morning, Ravi! 🌅          │
│ What would you like to read?    │
│                                 │
│ ┌─────────────────────────────┐ │
│ │ 🔍 Search books, authors... │ │
│ └─────────────────────────────┘ │
│                                 │
│ Browse by Category              │
│ ┌──────┐ ┌─────────┐ ┌───────┐ │
│ │Story │ │ Science │ │History│ │
│ │  📖  │ │   🔬    │ │  🏛️   │ │
│ └──────┘ └─────────┘ └───────┘ │
│                                 │
│ Currently Borrowed (2)          │
│ ┌─────────────────────────────┐ │
│ │ [Cover] The Jungle Book     │ │
│ │         Due: Jan 20, 2025   │ │
│ │         [RETURN NOW] button │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ [Cover] Light of Stars  ⚠️  │ │
│ │         OVERDUE - 3 days    │ │
│ │         [RETURN NOW] button │ │
│ └─────────────────────────────┘ │
│                                 │
│ Popular Books This Week         │
│ [Grid of books - 2 columns]     │
│                                 │
└─────────────────────────────────┘

[Bottom Nav: 🏠 Home | 📚 Library | 📷 Scan | 📖 History | ⭐ Reviews]
```

### Design Specifications:
- **Top App Bar:** White background, app name in #1B5E20, notification bell, profile avatar
- **Greeting:** Bold, 20sp, #212121 — Time-based (Good Morning/Afternoon/Evening)
- **Search Bar:** Rounded, light gray background (#F5F5F5), magnifying glass icon
- **Category Chips:** Horizontal scrollable, rounded rectangles
  - Default: White background, #1B5E20 border
  - Selected: #1B5E20 background, white text
- **Borrowed Books Section:**
  - Normal status: White card, green "Due: date" text
  - OVERDUE status: **Light RED background (#FFEBEE)**, **RED border**, **RED "OVERDUE" label**, warning icon ⚠️
- **Return Now Button:** Outlined #1B5E20 button inside borrowed card
- **Popular Books Grid:** 2-column RecyclerView

### Overdue Display Rules:
```kotlin
// Color Logic for Borrowed Books
when (transaction.status) {
    "ACTIVE" -> {
        cardBackground = Color.WHITE
        statusText = "Due: ${formatDate(transaction.dueDate)}"
        statusColor = Color.parseColor("#1B5E20")
    }
    "OVERDUE" -> {
        cardBackground = Color.parseColor("#FFEBEE")  // Light RED
        cardStrokeColor = Color.parseColor("#D32F2F")  // Dark RED
        statusText = "OVERDUE - ${getDaysOverdue()} days"
        statusColor = Color.parseColor("#D32F2F")  // RED
        showWarningIcon = true
    }
}
```

### Logic:
```
1. Load current user from SharedPreferences
2. Greet by name + time of day
3. Fetch active/overdue transactions for current user
4. Show overdue books in RED at top of borrowed list
5. Run background check: mark any past-due-date books as OVERDUE
6. Search bar: real-time filtering on keystroke (300ms debounce)
7. Category chips: filter book grid
8. Notification bell: shows badge count of overdue books
```

### Bottom Navigation Items:
| Icon | Label | Fragment/Activity |
|------|-------|-------------------|
| 🏠 | Home | HomeFragment |
| 📚 | Library | CatalogFragment |
| 📷 | Scan | QRScannerActivity |
| 📖 | History | HistoryFragment |
| ⭐ | Reviews | ReviewsFragment |

### Navigation:
- **Search Bar Tap:** → Expand inline search results
- **Category Chip:** → CatalogFragment filtered by category
- **Return Now button:** → QRScannerActivity (return mode)
- **Book Card Tap:** → BookDetailActivity
- **Bell Icon:** → OverdueAlertFragment
- **Profile Icon:** → ProfileViewActivity
- **Bottom Nav - Library:** → CatalogFragment (SCR-005)
- **Bottom Nav - Scan:** → QRScannerActivity (SCR-007)
- **Bottom Nav - History:** → HistoryFragment (SCR-009)
- **Bottom Nav - Reviews:** → ReviewsFragment (SCR-010)

---

## SCREEN 5: BOOK CATALOG SCREEN

### Screen ID: SCR-005
### Screen Name: BookCatalogScreen
### File: CatalogFragment.kt + fragment_catalog.xml

### Visual Design:
```
┌─────────────────────────────────┐
│ ← Library Catalog    [Grid/List]│
│ ─────────────────────────────   │
│                                 │
│ ┌─────────────────────────────┐ │
│ │ 🔍 Search by name, author...│ │
│ └─────────────────────────────┘ │
│                                 │
│ [All] [Story] [Science] [History│
│                                 │
│ 47 Books Found                  │
│                                 │
│ ┌───────────┐  ┌───────────┐   │
│ │ [Cover]   │  │ [Cover]   │   │
│ │           │  │           │   │
│ │ Book Title│  │ Book Title│   │
│ │ Author    │  │ Author    │   │
│ │ ⭐⭐⭐⭐  │  │ ⭐⭐⭐    │   │
│ │ Available │  │ 🔴 Taken  │   │
│ └───────────┘  └───────────┘   │
│                                 │
│ ┌───────────┐  ┌───────────┐   │
│ │ [Cover]   │  │ [Cover]   │   │
│ │           │  │           │   │
│ │ Book Title│  │ Book Title│   │
│ │ Author    │  │ Author    │   │
│ │ ⭐⭐⭐⭐⭐│  │ ⭐⭐     │   │
│ │ Available │  │ Available │   │
│ └───────────┘  └───────────┘   │
│                                 │
│        [Load More...]           │
└─────────────────────────────────┘
```

### Design Specifications:
- **Search Bar:** Persistent at top, real-time search
- **Filter Chips (Horizontal Scroll):**
  - "All" chip
  - "Story" chip — 📖 icon, Orange (#FF6F00)
  - "Science" chip — 🔬 icon, Blue (#1565C0)
  - "History" chip — 🏛️ icon, Brown (#4E342E)
- **Book Grid:** 2-column grid, 8dp spacing between items
- **Book Card Design:**
  - Cover image: Full width of card, aspect ratio 3:4
  - Title: Bold, 13sp, max 2 lines, ellipsis
  - Author: Regular, 11sp, #757575, 1 line
  - Star Rating: Small golden stars (⭐)
  - Availability Badge:
    - Available: Green badge "Available"
    - Not Available: Red badge "Borrowed"
- **Toggle Button:** Grid/List view switch (top right)
- **Sort Option:** By title, by rating, by newest (dropdown)

### Availability Badge Colors:
```
Available: Background #E8F5E9, Text #1B5E20, "✓ Available"
Borrowed:  Background #FFEBEE, Text #D32F2F, "✗ Borrowed"
```

### Logic:
```
1. Load all active books from Room DB via LiveData
2. Observe LiveData → Update RecyclerView via DiffUtil
3. Category chip click → filter list by category
4. Search text change → searchBooks() with query
5. Grid/List toggle → change RecyclerView layout manager
6. Availability: check available_copies > 0
7. Pagination: load 20 books, "Load More" button
8. Empty state: Show "No books found" illustration
```

### Navigation:
- **Book Card Tap:** → BookDetailActivity (SCR-006)
- **Back Arrow:** → HomeFragment (SCR-004)

---

## SCREEN 6: BOOK DETAIL SCREEN

### Screen ID: SCR-006
### Screen Name: BookDetailScreen
### File: BookDetailActivity.kt + activity_book_detail.xml

### Visual Design:
```
┌─────────────────────────────────┐
│ ← Back                    Share │
│                                 │
│ ┌─────────────────────────────┐ │
│ │                             │ │
│ │     [BOOK COVER IMAGE]      │ │
│ │     (Full width, 220dp)     │ │
│ │                             │ │
│ └─────────────────────────────┘ │
│                                 │
│ The Jungle Book                 │
│ by Rudyard Kipling              │
│                                 │
│ ⭐⭐⭐⭐☆  4.2  (18 reviews) │
│                                 │
│ ┌────────┐ ┌────────┐ ┌──────┐ │
│ │ Story  │ │English │ │  2   │ │
│ │Category│ │Language│ │Copies│ │
│ └────────┘ └────────┘ └──────┘ │
│                                 │
│ About this book:                │
│ A young boy raised by wolves    │
│ in the Indian jungle discovers  │
│ his place in the world...       │
│                                 │
│ ┌─────────────────────────────┐ │
│ │  ✅ AVAILABLE - Borrow Now  │ │
│ └─────────────────────────────┘ │
│         OR                      │
│ ┌─────────────────────────────┐ │
│ │  ❌ NOT AVAILABLE           │ │
│ │  Due back on: Jan 25, 2025  │ │
│ └─────────────────────────────┘ │
│                                 │
│ Student Reviews (18)            │
│ ┌─────────────────────────────┐ │
│ │ 👤 Priya, Class 7           │ │
│ │ ⭐⭐⭐⭐⭐                  │ │
│ │ "Best book I ever read!"    │ │
│ └─────────────────────────────┘ │
│                                 │
│ [Write a Review]                │
└─────────────────────────────────┘
```

### Design Specifications:
- **Book Cover:** Full-width image, height 220dp, placeholder for missing covers
- **Title:** Bold, 22sp, #212121
- **Author:** "by [Name]" — Regular, 14sp, #757575
- **Rating Row:** Golden stars (filled/empty), numeric rating, review count
- **Info Chips (3 columns):** Rounded chips with labels
- **Description:** Regular, 14sp, #424242, expandable (Show More/Less)
- **Borrow Button:**
  - Available: Full-width, #1B5E20, white text, "Borrow Now →"
  - Unavailable: Gray background, "Not Available - Due Jan 25"
- **Reviews Section:** Vertical list, most recent first
- **Write Review Button:** Only shown if user has borrowed this book previously

### Borrow Button Logic:
```kotlin
when {
    book.availableCopies > 0 && !userCurrentlyHasBook -> {
        showBorrowButton()
    }
    book.availableCopies == 0 -> {
        showUnavailableState()
        // Show expected return date from latest active transaction
    }
    userCurrentlyHasBook -> {
        showReturnButton()
        // User has this book — show return option
    }
}
```

### Navigation:
- **Borrow Now Button:** → QRScannerActivity (borrow mode, bookId passed as extra)
- **Return Now Button:** → QRScannerActivity (return mode)
- **Write a Review:** → ReviewWriteFragment (bottom sheet)
- **Back:** → CatalogFragment or previous screen

---

## SCREEN 7: QR SCANNER SCREEN

### Screen ID: SCR-007
### Screen Name: QRScannerScreen
### File: QRScannerActivity.kt + activity_qr_scanner.xml

### Visual Design:
```
┌─────────────────────────────────┐
│ ← Back                          │
│                                 │
│  [MODE INDICATOR]               │
│  ┌─────────────────────────┐   │
│  │  📷 BORROWING A BOOK    │   │  ← Green header
│  └─────────────────────────┘   │
│          OR                     │
│  ┌─────────────────────────┐   │
│  │  📷 RETURNING A BOOK    │   │  ← Orange header
│  └─────────────────────────┘   │
│                                 │
│  ┌─────────────────────────────┐│
│  │                             ││
│  │    [CAMERA PREVIEW]         ││
│  │                             ││
│  │    ┌─────────────┐          ││
│  │    │             │          ││
│  │    │  SCAN AREA  │          ││
│  │    │  [corners]  │          ││
│  │    │             │          ││
│  │    └─────────────┘          ││
│  │                             ││
│  │                             ││
│  └─────────────────────────────┘│
│                                 │
│  Point your camera at the QR    │
│  code printed on the book cover │
│                                 │
│  [🔦 Toggle Flashlight]         │
│                                 │
│  ─────── OR ENTER MANUALLY ─── │
│  ┌─────────────────────────────┐│
│  │ Enter Book ID manually      ││
│  └─────────────────────────────┘│
│  [SUBMIT MANUALLY]              │
│                                 │
└─────────────────────────────────┘
```

### Design Specifications:
- **Mode Header:**
  - Borrow mode: #1B5E20 background, "📷 BORROWING A BOOK", white text
  - Return mode: #E65100 background, "📷 RETURNING A BOOK", white text
- **Camera Preview:** Full available width, proper aspect ratio
- **Scan Frame:** Animated corners (green scanning animation), 250dp x 250dp
- **Scanning Line:** Animated red/green line moving inside frame
- **Instruction Text:** 14sp, #424242, centered
- **Flashlight Button:** Outlined circular button, torch icon
- **Manual Entry:** TextField + Submit button for cases where QR is damaged

### QR Scanner Logic:
```
1. Activity receives extras:
   - "SCAN_MODE": "BORROW" or "RETURN"
   - "BOOK_ID": Optional (if coming from BookDetail directly)

2. Camera initializes with ML Kit Barcode Scanner

3. On QR Code Detected:
   a. Vibrate phone (50ms haptic feedback)
   b. Play success sound
   c. Stop camera preview
   d. Extract book QR data
   e. Query Room DB: getBookByQRCode(qrData)

4. BORROW MODE:
   a. Book found? Check available_copies > 0
   b. YES available:
      - Create BorrowTransaction record
      - Set dueDate = currentDate + 14 days
      - Decrement book available_copies
      - Show SUCCESS dialog with due date
   c. NOT available:
      - Show ERROR dialog "Book not available"

5. RETURN MODE:
   a. Find active transaction for user + book
   b. Update transaction: status = RETURNED, return_date = now
   c. Increment book available_copies
   d. Calculate if overdue: compare returnDate vs dueDate
   e. Show SUCCESS dialog with "Returned on time!" or "Returned late - X days overdue"

6. SUCCESS DIALOG → Navigate back or to HomeFragment
7. ERROR → Show error, allow retry
```

### QR Code Data Format:
```
NP_BOOK_{bookId}_{randomUUID_first8chars}
Example: NP_BOOK_42_a1b2c3d4
```

### Navigation:
- **On Successful Borrow:** → Show Success Dialog → HomeFragment
- **On Successful Return:** → Show Success Dialog → HomeFragment
- **Back:** → Previous screen

---

## SCREEN 8: BORROW/RETURN SUCCESS DIALOG

### Screen ID: SCR-008
### Screen Name: TransactionSuccessDialog
### File: TransactionSuccessDialogFragment.kt

### Visual Design - BORROW SUCCESS:
```
┌─────────────────────────────────┐
│         ✅                      │
│    Book Borrowed!               │
│                                 │
│  The Jungle Book                │
│  by Rudyard Kipling             │
│                                 │
│  ┌─────────────────────────┐    │
│  │ Borrowed On: Jan 6, 2025│    │
│  │ Due Date:   Jan 20, 2025│    │
│  │ Days Left:  14 days     │    │
│  └─────────────────────────┘    │
│                                 │
│  Happy Reading! 📖              │
│                                 │
│  [VIEW MY BOOKS]  [GO HOME]    │
└─────────────────────────────────┘
```

### Visual Design - RETURN SUCCESS:
```
┌─────────────────────────────────┐
│         ✅                      │
│    Book Returned!               │
│                                 │
│  The Jungle Book                │
│                                 │
│  ┌─────────────────────────┐    │
│  │ Returned: Jan 15, 2025  │    │
│  │ Returned On Time! ✓     │    │
│  └─────────────────────────┘    │
│                                 │
│  How was the book?              │
│  ⭐ ⭐ ⭐ ⭐ ⭐              │
│  [Tap to rate quickly]          │
│                                 │
│  [RATE LATER]  [RATE NOW]      │
└─────────────────────────────────┘
```

### Visual Design - OVERDUE RETURN:
```
┌─────────────────────────────────┐
│         ⚠️                      │
│    Book Returned Late           │
│                                 │
│  The Jungle Book                │
│                                 │
│  ┌─────────────────────────┐    │
│  │ Was Due:   Jan 20, 2025 │    │
│  │ Returned:  Jan 25, 2025 │    │
│  │ Days Late: 5 days  🔴   │    │
│  └─────────────────────────┘    │
│                                 │
│  Please try to return books     │
│  on time. Others are waiting!   │
│                                 │
│         [OK, NOTED]             │
└─────────────────────────────────┘
```

---

## SCREEN 9: BORROWING HISTORY SCREEN

### Screen ID: SCR-009
### Screen Name: HistoryScreen
### File: HistoryFragment.kt + fragment_history.xml

### Visual Design:
```
┌─────────────────────────────────┐
│ ← My Reading History            │
│ ─────────────────────────────   │
│                                 │
│ ┌──────────┬──────────┬───────┐ │
│ │   All    │  Active  │Overdue│ │
│ └──────────┴──────────┴───────┘ │
│                                 │
│ Total Books Read: 12 📚         │
│                                 │
│ ┌─────────────────────────────┐ │
│ │[Cover] The Jungle Book      │ │
│ │        Borrowed: Jan 6      │ │
│ │        Returned: Jan 15 ✅  │ │
│ │        Duration: 9 days     │ │
│ │        ⭐⭐⭐⭐⭐ (My rating)│ │
│ └─────────────────────────────┘ │
│                                 │
│ ┌─────────────────────────────┐ │
│ │[Cover] Light of Stars    🔴 │ │
│ │        Borrowed: Dec 20     │ │
│ │        OVERDUE - 3 days     │ │
│ │        Due: Jan 3, 2025     │ │
│ │        [RETURN NOW]         │ │
│ └─────────────────────────────┘ │
│                                 │
│ ┌─────────────────────────────┐ │
│ │[Cover] Wonder               │ │
│ │        Borrowed: Nov 10     │ │
│ │        Returned: Nov 20 ✅  │ │
│ │        Duration: 10 days    │ │
│ │        [Write Review]       │ │
│ └─────────────────────────────┘ │
└─────────────────────────────────┘
```

### Design Specifications:
- **Tab Bar:** 3 tabs — All, Active, Overdue
  - Active tab: underline in #1B5E20
  - Overdue tab: shows red badge with count
- **Summary Card:** Total books read counter
- **History Item - Returned:**
  - White card
  - Green ✅ checkmark
  - "Returned: [date]" in green
  - Duration shown
  - Star rating if reviewed, "Write Review" if not
- **History Item - Active:**
  - Light green card (#E8F5E9)
  - "Due: [date]" in green
  - "X days remaining" countdown
- **History Item - Overdue:**
  - RED card (#FFEBEE)
  - ⚠️ warning icon
  - "OVERDUE - X days" in red bold
  - "RETURN NOW" button in red
- **Sort:** Most recent first (default)

### Logic:
```
1. Load all transactions for current user
2. Tab "All" → all transactions
3. Tab "Active" → status = ACTIVE
4. Tab "Overdue" → status = OVERDUE
5. Each item shows: book cover, title, dates, status
6. RETURN NOW button → QRScannerActivity (return mode)
7. Write Review → ReviewWriteBottomSheet
```

### Navigation:
- **RETURN NOW Button:** → QRScannerActivity (return mode)
- **Write Review:** → ReviewWriteFragment (bottom sheet)
- **Book Cover Tap:** → BookDetailActivity

---

## SCREEN 10: BOOK REVIEWS SCREEN

### Screen ID: SCR-010
### Screen Name: BookReviewsScreen
### File: ReviewsFragment.kt + fragment_reviews.xml

### Visual Design - WRITE REVIEW (Bottom Sheet):
```
┌─────────────────────────────────┐
│  ──── (drag handle)             │
│                                 │
│  Rate "The Jungle Book"         │
│                                 │
│  How would you rate this book?  │
│                                 │
│      ★  ★  ★  ★  ★            │
│     (Tap stars to rate)         │
│                                 │
│  Write a short review:          │
│  ┌─────────────────────────────┐│
│  │ What did you love about    ││
│  │ this book? (One line)      ││
│  └─────────────────────────────┘│
│                                 │
│  Character limit: 100 chars     │
│                                 │
│  ┌─────────────────────────────┐│
│  │      SUBMIT REVIEW ✓        ││
│  └─────────────────────────────┘│
└─────────────────────────────────┘
```

### Visual Design - VIEW ALL REVIEWS (In BookDetail):
```
Reviews for "The Jungle Book"

Overall Rating: ⭐ 4.2 / 5
━━━━━━━━━━━━━━━━━━━━
5 ★  ████████░░  8
4 ★  █████░░░░░  5
3 ★  ██░░░░░░░░  2
2 ★  ░░░░░░░░░░  0
1 ★  █░░░░░░░░░  1

18 Reviews Total

┌─────────────────────────────────┐
│ 👤 Priya K.  •  Class 7        │
│ ⭐⭐⭐⭐⭐                      │
│ "The best book I've ever read!  │
│  Mowgli is my hero!"            │
│ Jan 15, 2025                    │
└─────────────────────────────────┘

┌─────────────────────────────────┐
│ 👤 Ravi M.  •  Class 8         │
│ ⭐⭐⭐⭐                        │
│ "Very exciting story, loved the │
│  animal characters!"            │
│ Jan 12, 2025                    │
└─────────────────────────────────┘
```

### Star Rating Interaction:
```kotlin
// Star rating touch handler
starRating.setOnRatingBarChangeListener { _, rating, fromUser ->
    if (fromUser) {
        selectedRating = rating.toInt()
        updateStarColors(selectedRating)
        // Stars animate with scale 1.0 → 1.3 → 1.0 on selection
    }
}

// Validation
fun submitReview() {
    if (selectedRating == 0) {
        showError("Please select a star rating")
        return
    }
    if (reviewText.isEmpty()) {
        showError("Please write a short review")
        return
    }
    // Check: user can only review a book they've borrowed
    // Check: only one review per user per book (update if exists)
}
```

### Logic:
```
1. Review only allowed if user has a RETURNED transaction for this book
2. One review per user per book (can update existing)
3. Rating: 1-5 integer
4. Review text: 1-100 characters, non-empty
5. On submit:
   a. Insert/update BookReview in Room
   b. Recalculate book average rating
   c. Update Book entity averageRating field
   d. Refresh reviews list
6. Rating chart: show distribution bar chart
```

---

## SCREEN 11: TEACHER DASHBOARD

### Screen ID: SCR-011
### Screen Name: TeacherDashboardScreen
### File: TeacherDashboardActivity.kt + activity_teacher_dashboard.xml

### Visual Design:
```
┌─────────────────────────────────┐
│ Teacher Dashboard    👩‍🏫 Profile │
│ Namma-Pustaka Library           │
│ ─────────────────────────────   │
│                                 │
│ Library Overview                │
│ ┌──────┐ ┌──────┐ ┌──────────┐ │
│ │  47  │ │  12  │ │    3     │ │
│ │Books │ │Borr'd│ │ Overdue  │ │
│ │Total │ │Today │ │  🔴      │ │
│ └──────┘ └──────┘ └──────────┘ │
│                                 │
│ ┌──────────────────────────────┐│
│ │  ➕ ADD NEW BOOK             ││
│ └──────────────────────────────┘│
│                                 │
│ 🔴 OVERDUE ALERTS (3)           │
│ ┌─────────────────────────────┐ │
│ │ 👤 Ravi M. - Class 7        │ │
│ │    "Light of Stars"         │ │
│ │    Overdue by 3 days 🔴     │ │
│ └─────────────────────────────┘ │
│ ┌─────────────────────────────┐ │
│ │ 👤 Priya K. - Class 6       │ │
│ │    "Malgudi Days"           │ │
│ │    Overdue by 1 day 🔴      │ │
│ └─────────────────────────────┘ │
│                                 │
│ Recent Transactions             │
│ ┌─────────────────────────────┐ │
│ │ [Book] Borrowed by Anita    │ │
│ │        Class 8 • Today 10am │ │
│ └─────────────────────────────┘ │
│                                 │
│ [VIEW ALL TRANSACTIONS]         │
│ [VIEW ALL STUDENTS]             │
│                                 │
└─────────────────────────────────┘

[Bottom Nav: 🏠 Dashboard | 📚 Catalog | ➕ Add Book | 📊 Reports | ⚙️ Settings]
```

### Design Specifications:
- **Header:** School green (#1B5E20), white text, teacher name
- **Stat Cards (3 horizontal):**
  - Books Total: Blue (#1565C0)
  - Borrowed Today: Green (#1B5E20)
  - Overdue: RED (#D32F2F) with pulsing animation
- **ADD BOOK Button:** Prominent, full-width, #1B5E20
- **Overdue Section:** Red section header, overdue cards with red styling
- **Recent Transactions:** Chronological list
- **Bottom Nav:** 5 items for teacher workflow

### Teacher Bottom Navigation:
| Icon | Label | Screen |
|------|-------|--------|
| 🏠 | Dashboard | TeacherDashboard |
| 📚 | Catalog | TeacherCatalogView |
| ➕ | Add Book | AddBookActivity |
| 📊 | Reports | ReportsFragment |
| ⚙️ | Settings | SettingsFragment |

### Logic:
```
1. Load stats from Room DB:
   - Total books: COUNT from books table
   - Borrowed today: COUNT transactions where borrow_date = today
   - Overdue: COUNT transactions where status = OVERDUE
2. Load overdue transactions with user info (JOIN query)
3. Load recent 10 transactions
4. Overdue count shows pulsing red animation if > 0
5. Refresh stats every time fragment resumes
```

### Navigation:
- **ADD NEW BOOK:** → AddBookActivity (SCR-012)
- **VIEW ALL TRANSACTIONS:** → AllTransactionsActivity (SCR-013)
- **VIEW ALL STUDENTS:** → AllStudentsActivity (SCR-014)
- **Overdue Student Card Tap:** → Student detail/contact info

---

## SCREEN 12: ADD NEW BOOK SCREEN

### Screen ID: SCR-012
### Screen Name: AddBookScreen
### File: AddBookActivity.kt + activity_add_book.xml

### Visual Design:
```
┌─────────────────────────────────┐
│ ← Add New Book                  │
│ ─────────────────────────────   │
│                                 │
│ Book Cover Photo                │
│ ┌─────────────────────────────┐ │
│ │                             │ │
│ │   📸 Take Photo             │ │
│ │   or Choose from Gallery    │ │
│ │                             │ │
│ └─────────────────────────────┘ │
│                                 │
│ Book Title *                    │
│ ┌─────────────────────────────┐ │
│ │ Enter book title            │ │
│ └─────────────────────────────┘ │
│                                 │
│ Author Name *                   │
│ ┌─────────────────────────────┐ │
│ │ Enter author name           │ │
│ └─────────────────────────────┘ │
│                                 │
│ Category *                      │
│ ┌──────────┐┌─────────┐┌──────┐│
│ │📖 Story  ││🔬Science││🏛️Hist││
│ └──────────┘└─────────┘└──────┘│
│                                 │
│ Description                     │
│ ┌─────────────────────────────┐ │
│ │ Brief description of book   │ │
│ │ (optional)                  │ │
│ └─────────────────────────────┘ │
│                                 │
│ Number of Copies *              │
│ ┌─────┐                         │
│ │  1  │ [−] [+]                │
│ └─────┘                         │
│                                 │
│ ┌─────────────────────────────┐ │
│ │   ADD BOOK & GENERATE QR ✓  │ │
│ └─────────────────────────────┘ │
└─────────────────────────────────┘
```

### Design Specifications:
- **Cover Photo Area:** Dashed border card, 160dp height, placeholder book icon
- **Photo Options:** Camera icon + Gallery icon, tap to open chooser dialog
- **Input Fields:** OutlinedTextInputLayout
- **Category:** 3 toggle buttons, only one selectable
- **Copies Counter:** Number input with +/- buttons, min 1, max 99
- **ADD BOOK Button:** #1B5E20, full width, 56dp height

### Logic:
```
1. Teacher fills in all required fields
2. Cover photo: Optional — camera or gallery
   - Compress image before saving (max 500KB)
   - Save to app internal storage
3. Category: Must select one of 3
4. Copies: Default 1, min 1, max 99
5. On ADD BOOK:
   a. Validate all required fields
   b. Generate unique QR code data:
      format = "NP_BOOK_{tempId}_{UUID.randomUUID().toString().take(8)}"
   c. Insert Book into Room DB
   d. Get generated bookId
   e. Update QR data with actual bookId:
      "NP_BOOK_{actualBookId}_{uuid8}"
   f. Generate QR code bitmap from data
   g. Navigate to QRCodeDisplayScreen
```

### Navigation:
- **ADD BOOK Success:** → QRCodeDisplayActivity (SCR-013)
- **Back:** → TeacherDashboard

---

## SCREEN 13: QR CODE DISPLAY SCREEN

### Screen ID: SCR-013
### Screen Name: QRCodeDisplayScreen
### File: QRCodeDisplayActivity.kt + activity_qr_display.xml

### Visual Design:
```
┌─────────────────────────────────┐
│ ← QR Code Generated!            │
│ ─────────────────────────────   │
│                                 │
│       Book Added Successfully!  │
│       ✅                        │
│                                 │
│  Book: The Jungle Book          │
│  Author: Rudyard Kipling        │
│  Category: Story                │
│  Copies: 2                      │
│                                 │
│  ┌─────────────────────────────┐│
│  │                             ││
│  │    [QR CODE IMAGE]          ││
│  │    256dp x 256dp            ││
│  │                             ││
│  └─────────────────────────────┘│
│                                 │
│  NP_BOOK_42_a1b2c3d4            │
│  (QR Code ID)                   │
│                                 │
│  📋 Instructions:               │
│  Print this QR code and         │
│  paste it on the book cover.    │
│  Students will scan this to     │
│  borrow or return the book.     │
│                                 │
│  ┌─────────────────────────────┐│
│  │  📤 SHARE QR CODE           ││
│  └─────────────────────────────┘│
│  ┌─────────────────────────────┐│
│  │  💾 SAVE TO GALLERY         ││
│  └─────────────────────────────┘│
│  ┌─────────────────────────────┐│
│  │  ✅ DONE - Go to Dashboard  ││
│  └─────────────────────────────┘│
└─────────────────────────────────┘
```

### Logic:
```
1. Receive book details + QR data from AddBookActivity
2. Generate QR code bitmap using ZXing library:
   QRCodeWriter().encode(
       qrData,
       BarcodeFormat.QR_CODE,
       512, 512
   )
3. Convert BitMatrix to Bitmap
4. Display QR code on screen
5. SHARE: Use Android ShareSheet to share QR image
6. SAVE: Save bitmap to gallery using MediaStore
7. DONE: Navigate to TeacherDashboard
```

### QR Code Generation Code:
```kotlin
fun generateQRCode(data: String, size: Int = 512): Bitmap {
    val hints = mapOf(
        EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
        EncodeHintType.MARGIN to 2
    )
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size, hints)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
        }
    }
    return bitmap
}
```

---

## SCREEN 14: ALL TRANSACTIONS SCREEN (TEACHER)

### Screen ID: SCR-014
### Screen Name: AllTransactionsScreen
### File: AllTransactionsActivity.kt + activity_all_transactions.xml

### Visual Design:
```
┌─────────────────────────────────┐
│ ← All Transactions    [Filter ▼]│
│ ─────────────────────────────   │
│                                 │
│ [All] [Active] [Overdue] [Done] │
│                                 │
│ ┌─────────────────────────────┐ │
│ │🔍 Search by student name... │ │
│ └─────────────────────────────┘ │
│                                 │
│ 47 Total Transactions           │
│                                 │
│ ┌─────────────────────────────┐ │
│ │ 👤 Ravi M. • Class 7    🔴  │ │
│ │    📕 Light of Stars        │ │
│ │    Borrowed: Jan 1, 2025    │ │
│ │    OVERDUE: 5 days          │ │
│ └─────────────────────────────┘ │
│                                 │
│ ┌─────────────────────────────┐ │
│ │ 👤 Priya K. • Class 6   ✅  │ │
│ │    📗 Wonder                │ │
│ │    Borrowed: Jan 3, 2025    │ │
│ │    Returned: Jan 12, 2025   │ │
│ └─────────────────────────────┘ │
│                                 │
│ ┌─────────────────────────────┐ │
│ │ 👤 Anita R. • Class 8   📖  │ │
│ │    📘 The Alchemist         │ │
│ │    Borrowed: Jan 5, 2025    │ │
│ │    Due: Jan 19, 2025        │ │
│ └─────────────────────────────┘ │
└─────────────────────────────────┘
```

### Transaction Status Icons:
- 📖 Active — currently borrowed, not overdue
- 🔴 Overdue — RED card, past due date
- ✅ Returned — White card, returned successfully

---

## SCREEN 15: STUDENT PROFILE VIEW

### Screen ID: SCR-015
### Screen Name: ProfileViewScreen
### File: ProfileActivity.kt + activity_profile.xml

### Visual Design:
```
┌─────────────────────────────────┐
│ ← My Profile          [Edit ✏️] │
│ ─────────────────────────────   │
│                                 │
│          [Profile Photo]        │
│          👤 Circle Avatar       │
│                                 │
│          Ravi Kumar             │
│          Class 7 • Roll No. 15  │
│          Student                │
│                                 │
│          Member since Jan 2025  │
│                                 │
│  ┌──────────┬──────────┬──────┐ │
│  │    12    │    2     │  4.3 │ │
│  │  Books   │Currently │ Avg  │ │
│  │  Read    │Borrowed  │Rating│ │
│  └──────────┴──────────┴──────┘ │
│                                 │
│  Reading Achievements           │
│  🏆 First Book Borrowed!        │
│  🏆 5 Books Read!               │
│  🏆 Top Reviewer!               │
│                                 │
│  Favourite Category             │
│  📖 Story (8 books)             │
│                                 │
│  ┌─────────────────────────────┐│
│  │  🔄 SWITCH USER / LOGOUT   ││
│  └─────────────────────────────┘│
└─────────────────────────────────┘
```

### Achievements System:
| Achievement | Condition |
|-------------|-----------|
| 🏆 First Book | Borrowed first book |
| 🏆 5 Books Read | 5 books returned |
| 🏆 10 Books Read | 10 books returned |
| 🏆 Top Reviewer | Submitted 5+ reviews |
| 🏆 On Time Reader | 5 consecutive on-time returns |
| 🏆 Speed Reader | Returned in under 5 days |

### Switch User / Logout Logic:
```
1. "Switch User" button pressed
2. Show dialog: "Are you sure? You'll need to enter your name again."
3. Confirm → Clear "current_user_id" from SharedPreferences
4. Navigate to ProfileSetupActivity
5. (Data remains in Room DB — they can log back in by entering same name)
```

---

# SECTION 5: SCREEN CONNECTION & NAVIGATION FLOW

## 5.1 Complete Navigation Map

```
┌─────────────────────────────────────────────────────────────────┐
│                        NAVIGATION FLOW                          │
└─────────────────────────────────────────────────────────────────┘

[APP LAUNCH]
     │
     ▼
[SplashActivity - SCR-001]
     │
     ├─── User Found in SharedPrefs ──────────────────────────────┐
     │                                                            │
     └─── No User ───────────────────────────────────────────────┐│
                                                                 ││
                                                                 ▼▼
[OnboardingActivity - SCR-002] ◄──────────────────── [Back from Profile]
     │
     │ GET STARTED / SKIP
     ▼
[ProfileSetupActivity - SCR-003]
     │
     ├─── STUDENT ────────────────────────────────────────────────┐
     │                                                            │
     └─── TEACHER ────────────────────────────────────────────────┼──► [TeacherDashboard - SCR-011]
                                                                  │              │
                                                                  │    ┌─────────┼─────────┐
                                                                  │    ▼         ▼         ▼
                                                                  │ [AddBook] [AllTxn] [Reports]
                                                                  │ SCR-012   SCR-014  SCR-016
                                                                  │    │
                                                                  │    ▼
                                                                  │ [QRDisplay - SCR-013]
                                                                  │
                                                                  ▼
[HomeActivity - SCR-004] (contains bottom nav)
     │
     ├─── Bottom Nav: Home ──────────────────────► [HomeFragment - SCR-004]
     │                                                    │
     │                                          ┌─────────┼──────────┐
     │                                          ▼         ▼          ▼
     │                                     [BookDetail] [Search] [Category]
     │                                     SCR-006       inline  filtered
     │
     ├─── Bottom Nav: Library ───────────────► [CatalogFragment - SCR-005]
     │                                                    │
     │                                                    ▼
     │                                           [BookDetailActivity - SCR-006]
     │                                                    │
     │                                          ┌─────────┼──────────┐
     │                                          ▼         ▼          ▼
     │                                      [Borrow]  [Return]  [Reviews]
     │                                      → SCR-007  → SCR-007  inline
     │
     ├─── Bottom Nav: Scan ──────────────────► [QRScannerActivity - SCR-007]
     │                                                    │
     │                                          ┌─────────┴──────────┐
     │                                          ▼                    ▼
     │                                    [BorrowSuccess]     [ReturnSuccess]
     │                                    SCR-008             SCR-008
     │                                          │
     │                                          ▼
     │                                    [HomeFragment]
     │
     ├─── Bottom Nav: History ───────────────► [HistoryFragment - SCR-009]
     │                                                    │
     │                                          ┌─────────┴──────────┐
     │                                          ▼                    ▼
     │                                    [QRScanner]         [WriteReview]
     │                                    (Return mode)       (BottomSheet)
     │
     └─── Bottom Nav: Reviews ───────────────► [ReviewsFragment - SCR-010]
                                                          │
                                                          ▼
                                                   [WriteReview]
                                                   (BottomSheet)
```

## 5.2 Intent/Bundle Data Passing

### SplashActivity → HomeActivity:
```kotlin
// No extras needed - userId stored in SharedPreferences
val intent = Intent(this, HomeActivity::class.java)
startActivity(intent)
finish()
```

### HomeFragment → QRScannerActivity (Borrow):
```kotlin
val intent = Intent(this, QRScannerActivity::class.java).apply {
    putExtra("SCAN_MODE", "BORROW")
    putExtra("BOOK_ID", bookId) // Optional
}
startActivityForResult(intent, REQUEST_BORROW)
```

### HomeFragment → QRScannerActivity (Return):
```kotlin
val intent = Intent(this, QRScannerActivity::class.java).apply {
    putExtra("SCAN_MODE", "RETURN")
    putExtra("TRANSACTION_ID", transactionId)
}
startActivityForResult(intent, REQUEST_RETURN)
```

### CatalogFragment → BookDetailActivity:
```kotlin
val intent = Intent(this, BookDetailActivity::class.java).apply {
    putExtra("BOOK_ID", book.bookId)
}
startActivity(intent)
```

### AddBookActivity → QRCodeDisplayActivity:
```kotlin
val intent = Intent(this, QRCodeDisplayActivity::class.java).apply {
    putExtra("BOOK_ID", savedBookId)
    putExtra("QR_DATA", generatedQRData)
    putExtra("BOOK_TITLE", bookTitle)
    putExtra("BOOK_AUTHOR", bookAuthor)
}
startActivity(intent)
```

## 5.3 SharedPreferences Keys

```kotlin
object PreferenceKeys {
    const val PREF_FILE = "namma_pustaka_prefs"
    const val CURRENT_USER_ID = "current_user_id"
    const val CURRENT_USER_TYPE = "current_user_type"  // "STUDENT" or "TEACHER"
    const val CURRENT_USER_NAME = "current_user_name"
    const val ONBOARDING_COMPLETE = "onboarding_complete"
    const val LAST_OVERDUE_CHECK = "last_overdue_check"
}
```

---

# SECTION 6: BACKEND LOGIC & BUSINESS RULES

## 6.1 Overdue Detection System

```kotlin
class OverdueCheckWorker(context: Context, params: WorkerParameters) 
    : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val db = NammaPustakaDatabase.getInstance(applicationContext)
        val currentTime = System.currentTimeMillis()
        
        // Mark all past-due active transactions as OVERDUE
        db.transactionDao().markOverdueTransactions(currentTime)
        
        return Result.success()
    }
}

// Schedule periodic overdue check
val overdueCheckRequest = PeriodicWorkRequestBuilder<OverdueCheckWorker>(
    1, TimeUnit.HOURS
).build()

WorkManager.getInstance(context).enqueueUniquePeriodicWork(
    "overdue_check",
    ExistingPeriodicWorkPolicy.KEEP,
    overdueCheckRequest
)
```

## 6.2 Due Date Calculation

```kotlin
object DateUtils {
    const val BORROW_PERIOD_DAYS = 14
    
    fun calculateDueDate(borrowDate: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = borrowDate
        calendar.add(Calendar.DAY_OF_YEAR, BORROW_PERIOD_DAYS)
        return calendar.timeInMillis
    }
    
    fun getDaysOverdue(dueDate: Long): Int {
        val currentTime = System.currentTimeMillis()
        return if (currentTime > dueDate) {
            val diffMs = currentTime - dueDate
            (diffMs / (1000 * 60 * 60 * 24)).toInt()
        } else 0
    }
    
    fun getDaysRemaining(dueDate: Long): Int {
        val currentTime = System.currentTimeMillis()
        return if (dueDate > currentTime) {
            val diffMs = dueDate - currentTime
            (diffMs / (1000 * 60 * 60 * 24)).toInt()
        } else 0
    }
    
    fun formatDisplayDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
    
    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Hello"
        }
    }
}
```

## 6.3 Book Availability Rules

```kotlin
// Business Rules:
// 1. A book is available if available_copies > 0
// 2. A student cannot borrow the same book twice simultaneously
// 3. A student can have maximum 3 books borrowed at once
// 4. Overdue students can still borrow other books (just flagged)

suspend fun canStudentBorrowBook(userId: Int, bookId: Int): BorrowEligibility {
    // Check: book availability
    val book = bookDao.getBookById(bookId) 
        ?: return BorrowEligibility.BOOK_NOT_FOUND
    
    if (book.availableCopies <= 0) 
        return BorrowEligibility.NOT_AVAILABLE
    
    // Check: student doesn't already have this book
    val existingTransaction = transactionDao.getActiveTransaction(userId, bookId)
    if (existingTransaction != null) 
        return BorrowEligibility.ALREADY_BORROWED
    
    // Check: student hasn't exceeded max borrow limit (3 books)
    val activeCount = transactionDao.getActiveTransactionCount(userId)
    if (activeCount >= 3) 
        return BorrowEligibility.MAX_LIMIT_REACHED
    
    return BorrowEligibility.ELIGIBLE
}

enum class BorrowEligibility {
    ELIGIBLE,
    NOT_AVAILABLE,
    ALREADY_BORROWED,
    MAX_LIMIT_REACHED,
    BOOK_NOT_FOUND
}
```

## 6.4 Rating Calculation

```kotlin
suspend fun recalculateBookRating(bookId: Int) {
    val avgRating = reviewDao.getAverageRating(bookId)
    val reviewCount = reviewDao.getReviewCount(bookId)
    
    // Round to 1 decimal place
    val roundedRating = (avgRating * 10).toInt() / 10f
    
    bookDao.updateBookRating(bookId, roundedRating, reviewCount)
}
```

## 6.5 Search Logic

```kotlin
// Real-time search with debounce
private var searchJob: Job? = null

fun onSearchTextChanged(query: String) {
    searchJob?.cancel()
    searchJob = viewModelScope.launch {
        delay(300) // Debounce 300ms
        if (query.isEmpty()) {
            _books.value = allBooks.value
        } else {
            bookRepository.searchBooks(query).collect { results ->
                _books.value = results
            }
        }
    }
}
```

---

# SECTION 7: QR CODE SYSTEM

## 7.1 QR Data Format Specification

```
Format: NP_BOOK_{bookId}_{8-char-UUID}
Example: NP_BOOK_42_a1b2c3d4

Components:
- "NP_BOOK_" = App identifier prefix
- {bookId}   = Integer ID from Room DB
- "_"        = Separator
- {8-char}   = First 8 chars of UUID for uniqueness

Why this format:
- Unique per book
- Contains bookId for fast lookup
- Has random component to prevent guessing
- Short enough for fast QR scanning
- Readable for manual entry if QR is damaged
```

## 7.2 QR Code Scanner Implementation

```kotlin
class QRScannerActivity : AppCompatActivity() {
    
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var barcodeScanner: BarcodeScanner
    
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }
    
    private fun bindCameraUseCases() {
        val preview = Preview.Builder().build()
        
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        
        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
            processImageForQR(imageProxy)
        }
        
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        
        cameraProvider.bindToLifecycle(
            this, cameraSelector, preview, imageAnalysis
        )
    }
    
    private fun processImageForQR(imageProxy: ImageProxy) {
        val inputImage = InputImage.fromMediaImage(
            imageProxy.image!!, 
            imageProxy.imageInfo.rotationDegrees
        )
        
        barcodeScanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                barcodes.firstOrNull()?.rawValue?.let { qrData ->
                    if (qrData.startsWith("NP_BOOK_")) {
                        onValidQRCodeDetected(qrData)
                    }
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    }
    
    private fun onValidQRCodeDetected(qrData: String) {
        vibrate()
        cameraProvider.unbindAll() // Stop scanning
        viewModel.processQRCode(qrData, scanMode)
    }
    
    private fun vibrate() {
        val vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}
```

## 7.3 QR Code Generator Implementation

```kotlin
object QRCodeGenerator {
    
    fun generateBookQRCode(bookId: Int): Pair<String, Bitmap> {
        val uuid8 = UUID.randomUUID().toString().replace("-", "").take(8)
        val qrData = "NP_BOOK_${bookId}_${uuid8}"
        val bitmap = generateBitmap(qrData)
        return Pair(qrData, bitmap)
    }
    
    private fun generateBitmap(data: String, size: Int = 512): Bitmap {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H)
            put(EncodeHintType.MARGIN, 2)
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
        }
        
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size, hints)
        
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap.setPixel(x, y, 
                    if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                )
            }
        }
        return bitmap
    }
    
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, bookTitle: String): Uri? {
        val filename = "NammaPustaka_QR_${bookTitle}_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/NammaPustaka")
        }
        
        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        
        uri?.let {
            resolver.openOutputStream(it)?.use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
        }
        return uri
    }
}
```

---

# SECTION 8: INDIVIDUAL SCREEN PROMPTS FOR ANDROID STUDIO / AI DEVELOPMENT

## PROMPT 1: SPLASH SCREEN

```
Create an Android SplashActivity in Kotlin with the following specifications:

File: SplashActivity.kt and activity_splash.xml

LAYOUT (activity_splash.xml):
- ConstraintLayout as root
- Background color: #1B5E20 (deep forest green)
- Center: ImageView with app logo/book icon, 120dp x 120dp, tint white
- Below logo: TextView "NAMMA-PUSTAKA" — Bold, 28sp, white (#FFFFFF), font: Roboto Bold
- Below title: TextView "Smart Library Assistant" — Regular, 14sp, #A5D6A7
- Bottom area: LinearProgressIndicator (Material), width match_parent, 
  margin 48dp sides, color #FFD54F
- Very bottom: TextView "ಜ್ಞಾನವೇ ಶಕ್ತಿ • Knowledge is Power" — Italic, 12sp, white 70% alpha

LOGIC (SplashActivity.kt):
- Extend AppCompatActivity
- Override onCreate, call setContentView
- In onCreate, launch coroutine with delay(2500)
- Check SharedPreferences key "current_user_id" (default -1)
- If userId != -1 → startActivity(HomeActivity) with finish()
- If userId == -1 → startActivity(OnboardingActivity) with finish()
- Also run background overdue check using Room DB transactionDao
- Make status bar transparent, extend to full screen
- Add enter animation: fade in from alpha 0 to 1 over 800ms
- Theme: No title bar, full screen

DEPENDENCIES NEEDED:
- MaterialProgressIndicator from Material Design
- SharedPreferences for user session check
- Coroutines for delay
```

---

## PROMPT 2: ONBOARDING SCREEN

```
Create an Android OnboardingActivity with ViewPager2 and 3 slides in Kotlin.

Files: OnboardingActivity.kt, activity_onboarding.xml, 
       OnboardingFragment.kt, fragment_onboarding.xml

LAYOUT:
- Root: ConstraintLayout, white background
- Top: TextView "SKIP" aligned top-right, #1B5E20, 14sp, visible on slides 1&2 only
- Center: ViewPager2 taking 80% of screen height
- Bottom: 
  - TabLayout for indicator dots (custom dot style, not tabs)
  - Row with BACK button (left) and NEXT/GET STARTED button (right)

THREE SLIDES (pass as arguments to fragments):
Slide 1:
  - Image: Drawable of stacked books or book illustration
  - Title: "Welcome to Namma-Pustaka!" — Bold, 24sp, #1B5E20
  - Description: "Your school library is now available on your phone. 
    Borrow, return & discover amazing books!"
  - Regular, 16sp, #424242, centered, padding 24dp sides

Slide 2:
  - Image: Phone scanning QR code illustration
  - Title: "Scan & Go!" — Bold, 24sp, #1B5E20
  - Description: "Just scan the QR code on any book to borrow or 
    return it instantly. No paperwork needed!"

Slide 3:
  - Image: Stars/rating illustration
  - Title: "Read & Review!" — Bold, 24sp, #1B5E20
  - Description: "Rate books and help your friends discover great reads. 
    Build a reading culture together!"

BUTTON BEHAVIOR:
- Slide 1: Show only NEXT button (right-aligned)
- Slide 2: Show BACK and NEXT buttons
- Slide 3: Show BACK and GET STARTED button (yellow #FFD54F, black text)
- NEXT: viewPager.currentItem++
- BACK: viewPager.currentItem--
- SKIP: intent to ProfileSetupActivity
- GET STARTED: intent to ProfileSetupActivity
- Save "onboarding_complete = true" in SharedPreferences before navigating

INDICATOR DOTS:
- Use TabLayout with custom selector drawable
- Active dot: 10dp filled circle #1B5E20
- Inactive dot: 10dp filled circle #E0E0E0
- Connect TabLayout.TabLayoutMediator to ViewPager2

ANIMATIONS:
- Page transitions: horizontal slide (default ViewPager2)
- Button appearance: fade in on slide change
```

---

## PROMPT 3: PROFILE SETUP SCREEN

```
Create ProfileSetupActivity in Kotlin with Room DB integration.

Files: ProfileSetupActivity.kt, activity_profile_setup.xml, 
       ProfileSetupViewModel.kt

LAYOUT:
- ScrollView root with ConstraintLayout inside
- Top: Toolbar with back arrow and title "Set Up Your Profile"
- Sub-title: "No password needed! Just your name." — 14sp, #757575
- Profile photo:
  - CircleImageView 100dp x 100dp, centered
  - Default: person icon with dashed border
  - Tap: show dialog with Camera / Gallery options
  - Badge: small camera icon overlay bottom-right of circle

- User type selector:
  - TextView label "I am a..."
  - Two MaterialCardViews side by side (equal width)
  - Card 1: "🎒 Student" 
  - Card 2: "👩‍🏫 Teacher"
  - Selected card: background #1B5E20, text white, elevation 4dp
  - Unselected card: background white, stroke #1B5E20, text #1B5E20

- Full Name field: TextInputLayout + TextInputEditText
  - Hint: "Enter your full name"
  - Input type: textPersonName
  - Counter: maxLength 50
  - Error: show below if invalid

- Class/Grade field (shown only when Student selected):
  - TextInputLayout with ExposedDropdownMenu style
  - Items: Class 1 through Class 12 (Science/Arts for 11,12)
  - Use AutoCompleteTextView inside

- Subject field (shown only when Teacher selected):
  - Same dropdown style
  - Items: Mathematics, Science, Kannada, English, Social Studies, 
    Physical Education, Librarian, Principal

- Roll Number / Staff ID field:
  - TextInputLayout, hint changes based on user type
  - Student: "Roll Number", Teacher: "Staff ID"

- CREATE PROFILE button:
  - Full width, height 56dp
  - Background: #1B5E20
  - Text: "CREATE MY PROFILE ✓" — white, Bold, 16sp
  - Margin: 16dp sides and bottom

VIEWMODEL LOGIC:
- insertUser(user: User) coroutine function
- findExistingUser(name: String, grade: String) function
- Expose LiveData<ProfileSetupState> with states:
  - Loading, Success(userId), Error(message), UserExists(user)

ACTIVITY LOGIC:
- Validate: name not empty, class selected, roll number not empty
- On CREATE PROFILE clicked:
  a. Show loading on button
  b. Call viewModel.findExistingUser()
  c. If found → show AlertDialog "Welcome back! Continue as [name]?"
     - YES → save userId to SharedPrefs → navigate
     - NO → allow creating new profile
  d. If not found → viewModel.insertUser() → save to SharedPrefs → navigate
- Profile photo: ActivityResultLauncher for camera and gallery
- Save image to app internal storage, save path in User entity
- Navigate: Student → HomeActivity, Teacher → TeacherDashboardActivity

ROOM DB INTERACTION:
- Inject UserRepository via ViewModel
- Use viewModelScope.launch for DB operations
- Return generated userId from insert operation
```

---

## PROMPT 4: HOME SCREEN (STUDENT)

```
Create HomeFragment inside HomeActivity with bottom navigation in Kotlin.

Files: HomeActivity.kt, activity_home.xml, 
       HomeFragment.kt, fragment_home.xml, HomeViewModel.kt

HOMEACTIVITY:
- Contains BottomNavigationView with 5 items
- NavHostFragment for navigation
- Bottom nav items: Home (home_icon), Library (book_icon), 
  Scan (qr_scan_icon), History (history_icon), Reviews (star_icon)
- Active item color: #1B5E20
- When Scan is tapped: directly startActivity(QRScannerActivity) 
  with SCAN_MODE = "BORROW"

HOMEFRAGMENT LAYOUT:
- CoordinatorLayout root
- AppBarLayout with Toolbar:
  - Title: "Namma-Pustaka" — #1B5E20
  - Right icons: notification bell (BadgeDrawable for overdue count), profile avatar
- NestedScrollView containing:
  - Greeting Card:
    - "Good Morning, [Name]! 🌅" — Bold, 20sp
    - "What would you like to read today?" — 14sp, #757575
    - Background: light green gradient #E8F5E9 to #C8E6C9
    - Rounded corners 12dp, padding 16dp
  
  - Search Bar:
    - MaterialSearchBar or TextInputLayout styled as search
    - Hint: "Search books, authors..."
    - Start icon: search icon
    - Click: expand to show search results RecyclerView
    - Real-time search with 300ms debounce
  
  - Category Section:
    - Title: "Browse by Category" — Bold, 16sp
    - HorizontalScrollView with 3 Chips:
      Chip 1: "📖 Story" — background #FFF3E0, text #E65100
      Chip 2: "🔬 Science" — background #E3F2FD, text #1565C0
      Chip 3: "🏛️ History" — background #EFEBE9, text #4E342E
    - Chip tap: navigate to CatalogFragment with category filter
  
  - Currently Borrowed Section:
    - Title: "Currently Borrowed" with count badge
    - RecyclerView (vertical, max 3 items visible)
    - Each item is a MaterialCardView showing:
      - Book cover ImageView (60dp x 80dp)
      - Book title (Bold, 14sp)
      - Due date or OVERDUE label
      - "RETURN NOW" button (outlined)
    - OVERDUE STYLING: card background #FFEBEE, stroke #D32F2F, 
      status text #D32F2F Bold "OVERDUE - X days ⚠️"
    - Show "No borrowed books" if empty
  
  - Popular Books Section:
    - Title: "Popular Books This Week"
    - RecyclerView (horizontal scroll) or Grid (2 columns)
    - Book cards with cover, title, star rating

HOMEVIEWMODEL:
- currentUserId from SharedPreferences
- greetingText: LiveData<String> based on time
- activeBorrows: LiveData<List<BorrowTransactionWithBook>> 
  (use Room @Relation or JOIN)
- popularBooks: LiveData<List<Book>> (by rating, limit 10)
- overdueCount: LiveData<Int>
- searchResults: LiveData<List<Book>>
- fun searchBooks(query: String) with debounce
- fun checkAndMarkOverdue() run on startup

DATA CLASS FOR BORROWED DISPLAY:
data class BorrowWithBook(
    val transaction: BorrowTransaction,
    val book: Book
)

NAVIGATION FROM HOME:
- Notification bell → AlertsBottomSheetFragment showing overdue list
- Profile avatar → ProfileActivity
- RETURN NOW button → QRScannerActivity (RETURN mode, pass transactionId)
- Book card tap → BookDetailActivity (pass bookId)
- Category chip → CatalogFragment with category argument
```

---

## PROMPT 5: BOOK CATALOG SCREEN

```
Create CatalogFragment with RecyclerView grid and search/filter in Kotlin.

Files: CatalogFragment.kt, fragment_catalog.xml, 
       BookAdapter.kt, item_book_grid.xml, item_book_list.xml,
       CatalogViewModel.kt

LAYOUT (fragment_catalog.xml):
- ConstraintLayout root
- Top: SearchView or TextInputLayout (always visible, pinned)
- Below search: HorizontalScrollView with ChipGroup:
  - Chips: "All", "📖 Story", "🔬 Science", "🏛️ History"
  - Single selection, "All" default selected
  - Use Chip with app:chipBackgroundColor selector
- Results count: "47 Books Found" — 12sp, #757575
- Top-right toggle: ImageButton switching between grid/list icons
- RecyclerView:
  - Default: GridLayoutManager 2 columns
  - Toggle: LinearLayoutManager
  - ItemDecoration: 8dp spacing between items

BOOK GRID ITEM (item_book_grid.xml):
- MaterialCardView: corner radius 8dp, elevation 2dp
- ImageView: match card width, height 160dp, scaleType centerCrop
  - Placeholder: book_placeholder drawable
  - Load with Glide from local file path
- TextView: Book title — Bold, 13sp, maxLines 2, ellipsize END
- TextView: Author — 11sp, #757575, maxLines 1
- RatingBar (small style): max 5, stepSize 0.5, isIndicator true
- TextView: availability badge
  - Available: green text "#1B5E20", "✓ Available"
  - Borrowed: red text "#D32F2F", "✗ Borrowed"

BOOK LIST ITEM (item_book_list.xml):
- Horizontal layout
- ImageView: 60dp x 80dp
- Column: title (Bold 14sp), author (12sp gray), category chip, rating, availability

BOOKADAPTER:
- ListAdapter with DiffUtil.ItemCallback
- Toggle between GRID and LIST view type
- onBookClick: lambda callback
- ViewHolder pattern for both types

CATALOGVIEWMODEL:
- _allBooks: LiveData<List<Book>> from bookRepository.getAllBooks()
- _filteredBooks: MediatorLiveData combining search + category
- selectedCategory: MutableLiveData<String> ("ALL" default)
- searchQuery: MutableLiveData<String>
- displayMode: MutableLiveData<DisplayMode> (GRID/LIST)
- fun setCategory(category: String)
- fun setSearchQuery(query: String) — with 300ms debounce via Flow
- fun toggleDisplayMode()

FILTER LOGIC:
fun applyFilters(books: List<Book>, category: String, query: String): List<Book> {
    return books
        .filter { if (category == "ALL") true else it.category == category }
        .filter { 
            if (query.isEmpty()) true 
            else it.title.contains(query, ignoreCase = true) || 
                 it.author.contains(query, ignoreCase = true) 
        }
        .sortedByDescending { it.averageRating }
}

EMPTY STATE:
- When no books found: show centered ImageView (empty_books illustration) 
  + TextView "No books found. Try a different search!"

NAVIGATION:
- Book card click → BookDetailActivity with bookId as Intent extra
```

---

## PROMPT 6: BOOK DETAIL SCREEN

```
Create BookDetailActivity with full book information, borrow/return action, 
and reviews display in Kotlin.

Files: BookDetailActivity.kt, activity_book_detail.xml,
       BookDetailViewModel.kt, ReviewAdapter.kt, item_review.xml

LAYOUT (activity_book_detail.xml):
- CoordinatorLayout root
- AppBarLayout with CollapsingToolbarLayout:
  - AppBarLayout height: 260dp
  - ImageView: book cover, full width, height 220dp, scaleType centerCrop
    (use Glide to load from local path)
  - CollapsingToolbar with back button
  - Content scrim: semi-transparent dark gradient over image bottom
- NestedScrollView for scrollable content:
  - Book title: Bold, 22sp, #212121, padding 16dp top
  - Author: "by [name]" — 14sp, #757575
  - Rating row: RatingBar (indicator) + "4.2" text + "(18 reviews)" text
    all horizontal in same row
  - Info chips row (3 chips): Category | Language | X Copies
    - MaterialChip, not clickable, outlined style
  - Divider
  - "About this book:" header Bold 16sp
  - Description text: 14sp, #424242
  - "Show More / Show Less" link if text > 3 lines
  - Divider
  - Availability section:
    - If available_copies > 0:
      Show "✅ AVAILABLE" chip (green) + "Borrow Now →" MaterialButton
      Full width, #1B5E20, white text, 56dp height
    - If available_copies == 0:
      Show "❌ NOT AVAILABLE" chip (red)
      Show "Due back around: [date]" text
      Button: grayed out "Not Available"
    - If user currently has this book:
      Show "Return This Book" button (#E65100, full width)
  - Divider
  - "Student Reviews (N)" header
  - RatingBar distribution:
    - 5 rows showing star count bars (horizontal ProgressBar + count)
  - RecyclerView: reviews list (vertical, non-scrollable inside NestedScroll)
    nestedScrollingEnabled = false
  - "Write a Review" button (outlined, #1B5E20)
    Only visible if: user has returned this book AND hasn't reviewed yet
    OR user has an existing review (then show "Edit Your Review")

REVIEW ITEM (item_review.xml):
- MaterialCardView, outlined style
- Reviewer name: Bold 14sp + Class info 12sp gray (same row)
- RatingBar: small, indicator, golden
- Review text: 14sp, italic, #424242
- Date: 12sp, #9E9E9E, right-aligned

BOOKDETAILVIEWMODEL:
- book: LiveData<Book>
- reviews: LiveData<List<ReviewWithUser>> (join review + user name)
- userHasBorrowedBook: LiveData<Boolean>
- userCurrentTransaction: LiveData<BorrowTransaction?>
- userExistingReview: LiveData<BookReview?>
- ratingDistribution: LiveData<Map<Int, Int>> (star → count)
- fun loadBook(bookId: Int)
- fun loadReviews(bookId: Int)
- fun checkUserBorrowStatus(userId: Int, bookId: Int)

DATA CLASS:
data class ReviewWithUser(
    val review: BookReview,
    val reviewerName: String,
    val reviewerClass: String
)

NAVIGATION:
- "Borrow Now" → QRScannerActivity(BORROW mode, bookId extra)
- "Return This Book" → QRScannerActivity(RETURN mode, transactionId extra)
- "Write a Review" → ReviewBottomSheetFragment(bookId, existingReview)
- Back arrow → finish() / back to catalog
```

---

## PROMPT 7: QR SCANNER SCREEN

```
Create QRScannerActivity using CameraX + ML Kit Barcode Scanner in Kotlin.

Files: QRScannerActivity.kt, activity_qr_scanner.xml,
       QRScannerViewModel.kt

PERMISSIONS:
- Request CAMERA permission on launch using ActivityResultLauncher
- If denied → show explanation dialog → Settings

LAYOUT (activity_qr_scanner.xml):
- ConstraintLayout, black background
- Top: Mode indicator bar (match_parent width, 56dp height)
  - BORROW mode: background #1B5E20, text "📷 BORROWING A BOOK", white
  - RETURN mode: background #E65100, text "📷 RETURNING A BOOK", white
  - Dynamic: set based on received Intent extra "SCAN_MODE"
- PreviewView: takes center portion, aspect ratio 4:3
- Overlay View (custom ScanOverlayView):
  - Dark semi-transparent overlay everywhere except center square
  - Center square: 250dp x 250dp
  - Animated corner brackets (green lines, animated with ObjectAnimator)
  - Animated horizontal scan line (moves up-down in center square)
- Below camera:
  - Instruction text: "Point camera at the QR code on the book cover"
    14sp, white, centered
  - FlashlightButton: rounded outlined button with flash icon
    Toggle camera torch on/off
- Bottom section (white background):
  - Divider text: "─── OR ENTER MANUALLY ───"
  - TextInputLayout: "Enter Book QR ID (e.g. NP_BOOK_42_a1b2c3)"
  - "SUBMIT" outlined button
- Back button: top-left, white icon

SCANNER LOGIC (QRScannerActivity.kt):
1. Get SCAN_MODE from intent extra ("BORROW" or "RETURN")
2. Get optional BOOK_ID or TRANSACTION_ID from extras
3. Initialize CameraX:
   - Preview use case bound to PreviewView
   - ImageAnalysis use case for QR processing
4. ImageAnalysis analyzer:
   - Convert ImageProxy to InputImage
   - Pass to ML Kit BarcodeScanner
   - Filter: only process barcodes starting with "NP_BOOK_"
   - On valid QR: vibrate 50ms, stop camera, call viewModel.processQRCode()
5. Torch toggle: camera.cameraControl.enableTorch(true/false)
6. Manual submit: call viewModel.processQRCode() with typed text

SCANNERVIEWMODEL LOGIC:
suspend fun processQRCode(qrData: String, mode: String, userId: Int) {
    // Extract bookId from QR: "NP_BOOK_42_a1b2c3d4".split("_")[2].toInt()
    val bookId = extractBookIdFromQR(qrData)
    val book = bookRepository.getBookByQRCode(qrData)
    
    if (mode == "BORROW") {
        val eligibility = checkBorrowEligibility(userId, book)
        when (eligibility) {
            ELIGIBLE -> {
                val borrowDate = System.currentTimeMillis()
                val dueDate = DateUtils.calculateDueDate(borrowDate)
                val transaction = BorrowTransaction(
                    userId = userId,
                    bookId = book.bookId,
                    borrowDate = borrowDate,
                    dueDate = dueDate,
                    status = "ACTIVE",
                    qrScanBorrow = qrData
                )
                transactionRepository.insert(transaction)
                bookRepository.decrementAvailability(book.bookId)
                _scanResult.value = ScanResult.BorrowSuccess(book, dueDate)
            }
            NOT_AVAILABLE -> _scanResult.value = ScanResult.Error("Book not available")
            ALREADY_BORROWED -> _scanResult.value = ScanResult.Error("You already have this book")
            MAX_LIMIT_REACHED -> _scanResult.value = ScanResult.Error("Maximum 3 books allowed")
        }
    } else { // RETURN
        val activeTransaction = transactionRepository.getActiveTransaction(userId, book.bookId)
        if (activeTransaction != null) {
            val returnDate = System.currentTimeMillis()
            transactionRepository.returnBook(activeTransaction.transactionId, returnDate, qrData)
            bookRepository.incrementAvailability(book.bookId)
            val isOverdue = returnDate > activeTransaction.dueDate
            _scanResult.value = ScanResult.ReturnSuccess(book, activeTransaction, isOverdue)
        } else {
            _scanResult.value = ScanResult.Error("No active borrow found for this book")
        }
    }
}

SCAN RESULT STATES:
sealed class ScanResult {
    data class BorrowSuccess(val book: Book, val dueDate: Long): ScanResult()
    data class ReturnSuccess(val book: Book, val transaction: BorrowTransaction, val isOverdue: Boolean): ScanResult()
    data class Error(val message: String): ScanResult()
}

ON SUCCESS: Show TransactionSuccessDialogFragment
ON ERROR: Show Snackbar with error, re-enable camera scanning
```

---

## PROMPT 8: BORROWING HISTORY SCREEN

```
Create HistoryFragment with tab layout showing All/Active/Overdue transactions in Kotlin.

Files: HistoryFragment.kt, fragment_history.xml,
       HistoryAdapter.kt, item_history.xml, HistoryViewModel.kt

LAYOUT (fragment_history.xml):
- ConstraintLayout root, background #F5F5F5
- Top: TabLayout with 3 tabs: "All" | "Active" | "Overdue"
  - Style: colored underline tabs, #1B5E20 selected
  - Overdue tab: show red badge chip "3" if overdue > 0
- Summary Card (below tabs):
  - "📚 Total Books Read: 12" — white card, green icon
- RecyclerView (takes remaining space)
  - nestedScrollingEnabled = false
  - Each item: HistoryItemView

HISTORY ITEM LAYOUT (item_history.xml):
- MaterialCardView, corner 8dp, margin 8dp sides and 4dp top/bottom
- Left: ImageView 64dp x 84dp (book cover, Glide load)
- Right column:
  - Book title: Bold, 14sp, maxLines 1
  - Author: 12sp, gray
  - Status row:
    - ACTIVE: "📖 Borrowed on [date]" green, "Due: [date]" gray
              "X days remaining" — small green chip
    - OVERDUE: Background card #FFEBEE, stroke #D32F2F
               "⚠️ OVERDUE - X days" — Bold, RED, large
               "Was due: [date]" — 12sp gray
               "RETURN NOW" — RED outlined button, below
    - RETURNED: "✅ Returned on [date]" green
                "Duration: X days" gray
                Star rating OR "Write Review →" link

HISTORYVIEWMODEL:
- allTransactions: LiveData<List<HistoryItem>>
- activeTransactions: LiveData<List<HistoryItem>>
- overdueTransactions: LiveData<List<HistoryItem>>
- totalBooksRead: LiveData<Int>
- overdueCount: LiveData<Int>

DATA CLASS:
data class HistoryItem(
    val transaction: BorrowTransaction,
    val book: Book,
    val userReview: BookReview?  // null if not reviewed
)

TAB SWITCHING:
- Use TabLayout.OnTabSelectedListener
- Switch which LiveData the adapter observes
- OR use ViewPager2 with 3 HistoryTabFragments sharing ViewModel

RETURN NOW BUTTON:
Intent to QRScannerActivity with:
- SCAN_MODE = "RETURN"
- TRANSACTION_ID = transaction.transactionId
- BOOK_ID = transaction.bookId
```

---

## PROMPT 9: TEACHER DASHBOARD

```
Create TeacherDashboardActivity with stats, overdue alerts, and 
recent transactions in Kotlin.

Files: TeacherDashboardActivity.kt, activity_teacher_dashboard.xml,
       TeacherDashboardViewModel.kt, OverdueAlertAdapter.kt,
       RecentTransactionAdapter.kt

LAYOUT (activity_teacher_dashboard.xml):
- CoordinatorLayout root
- AppBarLayout: "#1B5E20" background
  - Toolbar: "Namma-Pustaka Library" white text
  - Below toolbar: teacher name greeting "Welcome, [Name]" 14sp, light green
- NestedScrollView content:
  
  A. STATS ROW (3 cards horizontal, equal width):
     - Card 1 (Blue #E3F2FD): "47" large bold, "Total Books" small gray
     - Card 2 (Green #E8F5E9): "12" large bold, "Borrowed Today" small gray
     - Card 3 (Red #FFEBEE): "3" large bold RED, "Overdue ⚠️" small red
       Add pulse animation if count > 0 (ObjectAnimator alpha 1→0.5→1, repeat)
  
  B. ADD BOOK BUTTON (prominent, full width):
     - MaterialButton: height 56dp, #1B5E20, "➕ ADD NEW BOOK" white Bold 16sp
     - Margin: 16dp all sides
  
  C. OVERDUE ALERTS SECTION (only if overdue > 0):
     - Header: Red banner "🔴 OVERDUE ALERTS (N)" — white text on red
     - RecyclerView (vertical, non-scrollable): overdue items
     - Each item: student avatar, student name + class, book title, 
       "Overdue: X days" red text
     - Tap: show student detail dialog
  
  D. RECENT TRANSACTIONS:
     - Header: "Recent Transactions" — Bold 16sp
     - RecyclerView: last 10 transactions
     - Item: book cover thumbnail, student name, "Borrowed/Returned", time ago
     - "VIEW ALL TRANSACTIONS →" text button
  
  E. STUDENT LIST:
     - "VIEW ALL STUDENTS →" text button

- BottomNavigationView: Dashboard | Catalog | Add Book | Reports | Settings

TEACHERDASHBOARDVIEWMODEL:
- totalBooks: LiveData<Int>
- borrowedToday: LiveData<Int>
- overdueCount: LiveData<Int>
- overdueItems: LiveData<List<OverdueItem>>
- recentTransactions: LiveData<List<RecentTransaction>>

data class OverdueItem(
    val studentName: String,
    val studentClass: String,
    val bookTitle: String,
    val daysOverdue: Int
)

OVERDUE QUERY (TransactionDao):
@Query("""
    SELECT t.*, u.full_name as studentName, u.class_grade as studentClass,
           b.title as bookTitle
    FROM borrow_transactions t
    JOIN users u ON t.user_id = u.userId
    JOIN books b ON t.book_id = b.bookId
    WHERE t.status = 'OVERDUE'
    ORDER BY t.due_date ASC
""")
fun getOverdueWithDetails(): LiveData<List<OverdueDetail>>

NAVIGATION:
- ADD BOOK → AddBookActivity
- VIEW ALL TRANSACTIONS → AllTransactionsActivity
- VIEW ALL STUDENTS → AllStudentsActivity
- Bottom nav: handle each tab
```

---

## PROMPT 10: ADD NEW BOOK SCREEN

```
Create AddBookActivity for teachers to add books and auto-generate QR codes in Kotlin.

Files: AddBookActivity.kt, activity_add_book.xml, AddBookViewModel.kt

LAYOUT (activity_add_book.xml):
- ScrollView root
- Toolbar: "Add New Book" with back arrow

- COVER PHOTO SECTION:
  MaterialCardView (dashed border drawable):
    - Height 180dp, width match_parent, margin 16dp
    - Center: vertical LinearLayout
      ImageView: camera icon 48dp, #9E9E9E
      TextView: "📸 Take Photo" — 16sp, #1B5E20
      TextView: "or Choose from Gallery" — 12sp, #9E9E9E
  - After photo selected: show full image in card
  - Add "Change Photo" text overlay at bottom

- BOOK TITLE field:
  TextInputLayout (OutlinedBox): hint "Book Title *"
  TextInputEditText: inputType text, maxLength 100

- AUTHOR NAME field:
  TextInputLayout (OutlinedBox): hint "Author Name *"
  TextInputEditText: inputType text, maxLength 60

- CATEGORY SELECTOR:
  TextView label: "Category *" Bold
  Three MaterialButton toggle (ToggleGroup or manual):
    Button 1: "📖 Story" — when selected: #1B5E20 bg, white text
    Button 2: "🔬 Science" — when selected: #1565C0 bg, white text
    Button 3: "🏛️ History" — when selected: #4E342E bg, white text
  Use MaterialButtonToggleGroup with singleSelection=true

- DESCRIPTION field:
  TextInputLayout: hint "Brief Description (optional)"
  TextInputEditText: multiLine, maxLines 4, maxLength 300

- COPIES COUNTER:
  TextView label: "Number of Copies *"
  Horizontal row: [−] MaterialButton | "1" TextView 40sp | [+] MaterialButton
  Min value: 1, Max value: 99

- ADD BOOK BUTTON:
  MaterialButton: full width, 56dp, #1B5E20
  Text: "ADD BOOK & GENERATE QR ✓"

VIEWMODEL (AddBookViewModel.kt):
- selectedCategory: MutableLiveData<String>
- copiesCount: MutableLiveData<Int> (default 1)
- coverImagePath: MutableLiveData<String?>
- saveResult: LiveData<SaveResult>

fun decrementCopies() { if (count > 1) count-- }
fun incrementCopies() { if (count < 99) count++ }

fun saveBook(title, author, category, description, copies, imagePath, addedByUserId) {
    viewModelScope.launch {
        // 1. Create temporary book to get ID
        val tempBook = Book(
            title = title, author = author, category = category,
            description = description, coverImagePath = imagePath,
            qrCodeData = "TEMP", // placeholder
            totalCopies = copies, availableCopies = copies,
            addedByUserId = addedByUserId, addedAt = System.currentTimeMillis()
        )
        val bookId = bookRepository.insertBook(tempBook).toInt()
        
        // 2. Generate proper QR data with actual bookId
        val uuid8 = UUID.randomUUID().toString().replace("-","").take(8)
        val qrData = "NP_BOOK_${bookId}_${uuid8}"
        
        // 3. Update book with proper QR data
        bookRepository.updateQRData(bookId, qrData)
        
        // 4. Generate QR bitmap
        val qrBitmap = QRCodeGenerator.generateBookQRCode(qrData)
        
        _saveResult.value = SaveResult.Success(bookId, qrData, qrBitmap)
    }
}

PHOTO CAPTURE:
- Camera: ActivityResultLauncher for MediaStore image capture
- Gallery: ActivityResultLauncher for image pick
- After selection: compress to max 500KB, save to app internal storage
- Display in cover photo card using Glide

VALIDATION:
- Title: required, min 2 chars
- Author: required, min 2 chars
- Category: required, must select one
- Copies: min 1 (enforced by +/- buttons)
- Show red error messages on TextInputLayouts

NAVIGATION ON SUCCESS:
startActivity(QRCodeDisplayActivity.newIntent(
    context = this,
    bookId = bookId,
    qrData = qrData,
    bookTitle = title,
    bookAuthor = author
))
finish()
```

---

## PROMPT 11: REVIEW WRITE BOTTOM SHEET

```
Create ReviewBottomSheetFragment for students to rate and review books.

Files: ReviewBottomSheetFragment.kt, fragment_review_write.xml,
       ReviewViewModel.kt

LAYOUT (fragment_review_write.xml):
- LinearLayout (vertical) inside BottomSheetDialog
- Drag handle: View 40dp x 4dp, centered, #BDBDBD, corner 2dp
- Title: "Rate [Book Title]" — Bold 18sp, #212121, margin 16dp
- Sub-title: "How would you rate this book?" — 14sp, #757575
- Star Rating:
  5 ImageViews in horizontal row, centered, each 48dp x 48dp
  Default: star_outline icon, #E0E0E0
  On tap: fill stars 1 through N with golden color #FFD54F
  Animate: scale 1.0 → 1.4 → 1.0 with spring interpolation on selection
  Stars are: interactive (tappable), show rating visually
- Selected rating text: "You rated: ★★★★☆" appears after selection
- Divider
- Review text label: "Write a short review:" — Bold 14sp
- TextInputLayout (OutlinedBox):
  TextInputEditText, hint "What did you love about this book?",
  maxLength 100, single line, counter enabled showing "XX/100"
- Character counter bar (LinearProgressIndicator, max 100)
- SUBMIT REVIEW button:
  Full width, 56dp, #1B5E20, "SUBMIT REVIEW ✓" white text
  Disabled (gray) if rating == 0 or text empty

LOGIC:
- Accept args: bookId (Int), existingReview (BookReview? - Parcelable)
- If existingReview != null → pre-fill stars and text (editing mode)
- Title changes: "Edit Your Review" if editing
- Button text: "SUBMIT REVIEW" or "UPDATE REVIEW"
- Validation: rating > 0 AND text not empty
- On submit:
  If new: reviewRepository.insertReview(review)
  If edit: reviewRepository.updateReview(review)
  After insert/update: recalculate book average rating
  Dismiss bottom sheet
  Show Snackbar "Review submitted! Thank you 📖"

REVIEWVIEWMODEL:
- fun submitReview(userId, bookId, rating, text)
- fun updateReview(reviewId, rating, text)  
- reviewResult: LiveData<ReviewResult>
- Calls reviewRepository, then recalculates book rating:
  val avg = reviewDao.getAverageRating(bookId)
  val count = reviewDao.getReviewCount(bookId)
  bookDao.updateBookRating(bookId, avg, count)

USAGE (how to show it):
val sheet = ReviewBottomSheetFragment.newInstance(bookId, existingReview)
sheet.show(supportFragmentManager, "review")
```

---

# SECTION 9: COLOR SYSTEM & DESIGN TOKENS

## 9.1 Complete Color Palette

```xml
<!-- colors.xml -->
<resources>
    <!-- Primary Brand Colors -->
    <color name="primary_green">#1B5E20</color>         <!-- Deep Forest Green - main brand -->
    <color name="primary_green_light">#4CAF50</color>   <!-- Medium Green -->
    <color name="primary_green_pale">#E8F5E9</color>    <!-- Very Light Green - backgrounds -->
    <color name="accent_gold">#FFD54F</color>            <!-- Golden Yellow - accents, CTA -->
    
    <!-- Status Colors -->
    <color name="available_green">#1B5E20</color>        <!-- Available status text -->
    <color name="available_bg">#E8F5E9</color>           <!-- Available status background -->
    <color name="overdue_red">#D32F2F</color>            <!-- OVERDUE text, borders -->
    <color name="overdue_red_bg">#FFEBEE</color>         <!-- OVERDUE card background -->
    <color name="active_orange">#E65100</color>          <!-- Active/return actions -->
    
    <!-- Category Colors -->
    <color name="story_orange">#E65100</color>           <!-- Story category -->
    <color name="story_orange_bg">#FFF3E0</color>
    <color name="science_blue">#1565C0</color>           <!-- Science category -->
    <color name="science_blue_bg">#E3F2FD</color>
    <color name="history_brown">#4E342E</color>          <!-- History category -->
    <color name="history_brown_bg">#EFEBE9</color>
    
    <!-- Text Colors -->
    <color name="text_primary">#212121</color>           <!-- Main text -->
    <color name="text_secondary">#757575</color>         <!-- Sub-text -->
    <color name="text_hint">#9E9E9E</color>              <!-- Hints -->
    <color name="text_white">#FFFFFF</color>
    
    <!-- UI Colors -->
    <color name="background">#F5F5F5</color>             <!-- Screen backgrounds -->
    <color name="surface_white">#FFFFFF</color>          <!-- Cards, dialogs -->
    <color name="divider">#E0E0E0</color>
    <color name="star_gold">#FFD54F</color>              <!-- Star rating color -->
    <color name="star_empty">#E0E0E0</color>             <!-- Empty star -->
</resources>
```

## 9.2 Typography Scale

```xml
<!-- styles.xml - Typography -->
<style name="TextStyle.Title.Large" parent="TextAppearance.Material3.TitleLarge">
    <item name="android:textSize">22sp</item>
    <item name="android:textColor">@color/text_primary</item>
    <item name="android:fontFamily">sans-serif-medium</item>
</style>

<style name="TextStyle.Body.Normal" parent="TextAppearance.Material3.BodyMedium">
    <item name="android:textSize">14sp</item>
    <item name="android:textColor">@color/text_primary</item>
</style>

<style name="TextStyle.Status.Overdue">
    <item name="android:textSize">14sp</item>
    <item name="android:textColor">@color/overdue_red</item>
    <item name="android:fontFamily">sans-serif-bold</item>
</style>

<style name="TextStyle.Status.Available">
    <item name="android:textSize">12sp</item>
    <item name="android:textColor">@color/available_green</item>
</style>
```

## 9.3 Theme Configuration

```xml
<!-- themes.xml -->
<style name="Theme.NammaPustaka" parent="Theme.Material3.Light.NoActionBar">
    <item name="colorPrimary">@color/primary_green</item>
    <item name="colorPrimaryVariant">#2E7D32</item>
    <item name="colorOnPrimary">@color/text_white</item>
    <item name="colorSecondary">@color/accent_gold</item>
    <item name="colorSurface">@color/surface_white</item>
    <item name="android:colorBackground">@color/background</item>
    <item name="android:statusBarColor">@color/primary_green</item>
    <item name="android:navigationBarColor">@color/surface_white</item>
    <item name="textInputStyle">@style/Widget.MaterialComponents.TextInputLayout.OutlinedBox</item>
    <item name="materialButtonStyle">@style/Widget.NammaPustaka.Button</item>
</style>

<style name="Widget.NammaPustaka.Button" parent="Widget.Material3.Button">
    <item name="android:textSize">16sp</item>
    <item name="android:minHeight">56dp</item>
    <item name="cornerRadius">8dp</item>
</style>
```

---

# SECTION 10: ERROR HANDLING & EDGE CASES

## 10.1 Error Scenarios & Handling

| Scenario | Error Message | Action |
|----------|--------------|--------|
| QR code not recognized | "Invalid QR code. Try again." | Retry camera |
| Book not in database | "Book not found in library system." | Contact teacher |
| Book already borrowed by user | "You already have this book!" | Show current transaction |
| Book unavailable (0 copies) | "All copies are borrowed. Check back later!" | Show expected return |
| User at max borrow limit | "You can only borrow 3 books at a time. Return one first!" | Show history |
| Camera permission denied | "Camera access needed for QR scanning." | Settings button |
| Empty book catalog | "Library is empty. Teacher needs to add books!" | Contact teacher UI |
| DB insert failure | "Something went wrong. Please try again." | Retry button |
| Name too short | "Name must be at least 2 characters." | Inline error |
| Review for not-borrowed book | Not allowed — hide review button | - |

## 10.2 Empty State Designs

```
EMPTY CATALOG:
┌─────────────────────────────────┐
│                                 │
│        📚                       │
│   (Large book illustration)     │
│                                 │
│   "No books yet!"               │
│                                 │
│   "Your teacher hasn't added    │
│    any books to the library.    │
│    Ask them to add books!"      │
│                                 │
└─────────────────────────────────┘

EMPTY HISTORY:
┌─────────────────────────────────┐
│        📖                       │
│   "No books borrowed yet"       │
│   "Visit the Library tab to     │
│    find your first book!"       │
│   [BROWSE BOOKS] button         │
└─────────────────────────────────┘

NO SEARCH RESULTS:
┌─────────────────────────────────┐
│        🔍                       │
│   "No books found"              │
│   "Try searching with a         │
│    different keyword"           │
└─────────────────────────────────┘
```

## 10.3 Loading States

```kotlin
// Standard loading state pattern
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    object Empty : UiState<Nothing>()
}

// Usage in Fragment
viewModel.books.observe(viewLifecycleOwner) { state ->
    when (state) {
        is UiState.Loading -> showShimmerLoading()
        is UiState.Success -> showBooks(state.data)
        is UiState.Error -> showError(state.message)
        is UiState.Empty -> showEmptyState()
    }
}
```

---

# SECTION 11: PERMISSIONS MANIFEST

```xml
<!-- AndroidManifest.xml -->
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- Camera for QR Scanning and Book Photo -->
    <uses-permission android:name="android.permission.CAMERA" />
    
    <!-- Storage for saving QR codes and book covers -->
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="29" />
    
    <!-- Vibration for QR scan feedback -->
    <uses-permission android:name="android.permission.VIBRATE" />
    
    <!-- Camera feature declaration -->
    <uses-feature android:name="android.hardware.camera" android:required="true" />
    <uses-feature android:name="android.hardware.camera.autofocus" android:required="false" />
    
    <application
        android:name=".NammaPustakaApp"
        android:label="Namma-Pustaka"
        android:icon="@mipmap/ic_launcher"
        android:theme="@style/Theme.NammaPustaka"
        android:allowBackup="true"
        android:supportsRtl="false">
        
        <!-- File Provider for image sharing -->
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>
        
        <!-- WorkManager for background overdue check -->
        <provider
            android:name="androidx.startup.InitializationProvider"
            android:authorities="${applicationId}.androidx-startup"
            android:exported="false">
            <meta-data
                android:name="androidx.work.WorkManagerInitializer"
                android:value="androidx.startup" />
        </provider>
        
        <activity android:name=".ui.splash.SplashActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <activity android:name=".ui.onboarding.OnboardingActivity" />
        <activity android:name=".ui.profile.ProfileSetupActivity" />
        <activity android:name=".ui.home.HomeActivity" />
        <activity android:name=".ui.bookdetail.BookDetailActivity" />
        <activity android:name=".ui.scanner.QRScannerActivity" />
        <activity android:name=".ui.teacher.TeacherDashboardActivity" />
        <activity android:name=".ui.teacher.AddBookActivity" />
        <activity android:name=".ui.teacher.QRCodeDisplayActivity" />
        
    </application>
</manifest>
```

---

# SECTION 12: ROOM DATABASE SETUP

```kotlin
// NammaPustakaDatabase.kt
@Database(
    entities = [
        User::class,
        Book::class,
        BorrowTransaction::class,
        BookReview::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class NammaPustakaDatabase : RoomDatabase() {
    
    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao
    abstract fun transactionDao(): TransactionDao
    abstract fun reviewDao(): ReviewDao
    
    companion object {
        @Volatile
        private var INSTANCE: NammaPustakaDatabase? = null
        
        fun getInstance(context: Context): NammaPustakaDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    NammaPustakaDatabase::class.java,
                    "namma_pustaka_db"
                )
                .addCallback(DatabaseCallback())
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
            }
        }
    }
    
    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Pre-populate with sample books for demo
            // Run in background thread
        }
    }
}

// Application class
class NammaPustakaApp : Application() {
    val database by lazy { NammaPustakaDatabase.getInstance(this) }
    
    override fun onCreate() {
        super.onCreate()
        // Schedule periodic overdue check
        scheduleOverdueCheck()
    }
    
    private fun scheduleOverdueCheck() {
        val overdueWorkRequest = PeriodicWorkRequestBuilder<OverdueCheckWorker>(
            1, TimeUnit.HOURS
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
        ).build()
        
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "overdue_check",
            ExistingPeriodicWorkPolicy.KEEP,
            overdueWorkRequest
        )
    }
}
```

---

# SECTION 13: SAMPLE DATA FOR TESTING

```kotlin
// DatabaseCallback pre-population
val sampleBooks = listOf(
    Book(
        title = "The Jungle Book",
        author = "Rudyard Kipling",
        category = "STORY",
        description = "A young boy raised by wolves discovers his place in the world through adventures in the Indian jungle.",
        qrCodeData = "NP_BOOK_1_a1b2c3d4",
        totalCopies = 3,
        availableCopies = 2,
        addedByUserId = 1,
        addedAt = System.currentTimeMillis()
    ),
    Book(
        title = "Wings of Fire",
        author = "A.P.J. Abdul Kalam",
        category = "HISTORY",
        description = "Autobiography of India's missile man and former President, Dr. A.P.J. Abdul Kalam.",
        qrCodeData = "NP_BOOK_2_e5f6g7h8",
        totalCopies = 2,
        availableCopies = 2,
        addedByUserId = 1,
        addedAt = System.currentTimeMillis()
    ),
    Book(
        title = "The Wonderful World of Science",
        author = "Various Authors",
        category = "SCIENCE",
        description = "Fun and exciting science experiments and discoveries for young minds.",
        qrCodeData = "NP_BOOK_3_i9j0k1l2",
        totalCopies = 4,
        availableCopies = 4,
        addedByUserId = 1,
        addedAt = System.currentTimeMillis()
    )
)
```

---

# SECTION 14: CONSTANTS FILE

```kotlin
// Constants.kt
object Constants {
    // SharedPreferences
    const val PREF_FILE = "namma_pustaka_prefs"
    const val PREF_USER_ID = "current_user_id"
    const val PREF_USER_TYPE = "current_user_type"
    const val PREF_USER_NAME = "current_user_name"
    const val PREF_ONBOARDING_DONE = "onboarding_complete"
    
    // Intent Extras
    const val EXTRA_SCAN_MODE = "SCAN_MODE"
    const val EXTRA_BOOK_ID = "BOOK_ID"
    const val EXTRA_TRANSACTION_ID = "TRANSACTION_ID"
    const val EXTRA_QR_DATA = "QR_DATA"
    const val EXTRA_BOOK_TITLE = "BOOK_TITLE"
    const val EXTRA_BOOK_AUTHOR = "BOOK_AUTHOR"
    
    // Scan Modes
    const val SCAN_MODE_BORROW = "BORROW"
    const val SCAN_MODE_RETURN = "RETURN"
    
    // User Types
    const val USER_TYPE_STUDENT = "STUDENT"
    const val USER_TYPE_TEACHER = "TEACHER"
    
    // Book Categories
    const val CATEGORY_ALL = "ALL"
    const val CATEGORY_STORY = "STORY"
    const val CATEGORY_SCIENCE = "SCIENCE"
    const val CATEGORY_HISTORY = "HISTORY"
    
    // Transaction Status
    const val STATUS_ACTIVE = "ACTIVE"
    const val STATUS_RETURNED = "RETURNED"
    const val STATUS_OVERDUE = "OVERDUE"
    
    // Business Rules
    const val BORROW_PERIOD_DAYS = 14
    const val MAX_BOOKS_PER_STUDENT = 3
    const val MAX_REVIEW_LENGTH = 100
    const val QR_PREFIX = "NP_BOOK_"
    
    // Image
    const val MAX_IMAGE_SIZE_KB = 500
    const val BOOK_COVER_DIR = "book_covers"
    const val PROFILE_PHOTO_DIR = "profile_photos"
    
    // Request Codes
    const val REQUEST_CAMERA_PERMISSION = 1001
    const val REQUEST_BORROW = 2001
    const val REQUEST_RETURN = 2002
}
```

---

# SECTION 15: NAVIGATION GRAPH

```xml
<!-- res/navigation/nav_graph.xml -->
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/nav_graph"
    app:startDestination="@id/homeFragment">

    <fragment
        android:id="@+id/homeFragment"
        android:name=".ui.home.HomeFragment"
        android:label="Home">
        <action
            android:id="@+id/action_home_to_catalog"
            app:destination="@id/catalogFragment" />
        <action
            android:id="@+id/action_home_to_history"
            app:destination="@id/historyFragment" />
    </fragment>

    <fragment
        android:id="@+id/catalogFragment"
        android:name=".ui.catalog.CatalogFragment"
        android:label="Library">
        <argument
            android:name="category"
            app:argType="string"
            android:defaultValue="ALL" />
    </fragment>

    <fragment
        android:id="@+id/historyFragment"
        android:name=".ui.history.HistoryFragment"
        android:label="History" />

    <fragment
        android:id="@+id/reviewsFragment"
        android:name=".ui.review.ReviewsFragment"
        android:label="Reviews" />

    <!-- Activities handled via Intent, not NavGraph -->
</navigation>
```

---

# SECTION 16: COMPLETE SCREEN SUMMARY TABLE

| # | Screen ID | Screen Name | File Name | Accessed By |
|---|-----------|-------------|-----------|-------------|
| 1 | SCR-001 | Splash Screen | SplashActivity.kt | App Launch |
| 2 | SCR-002 | Onboarding | OnboardingActivity.kt | First time users |
| 3 | SCR-003 | Profile Setup | ProfileSetupActivity.kt | After onboarding |
| 4 | SCR-004 | Student Home | HomeFragment.kt | After profile setup |
| 5 | SCR-005 | Book Catalog | CatalogFragment.kt | Bottom nav / Home |
| 6 | SCR-006 | Book Detail | BookDetailActivity.kt | Catalog book tap |
| 7 | SCR-007 | QR Scanner | QRScannerActivity.kt | Scan nav / Borrow button |
| 8 | SCR-008 | Success Dialog | TransactionSuccessDialog.kt | After QR scan |
| 9 | SCR-009 | History | HistoryFragment.kt | Bottom nav |
| 10 | SCR-010 | Write Review | ReviewBottomSheet.kt | After return / History |
| 11 | SCR-011 | Teacher Dashboard | TeacherDashboardActivity.kt | Teacher login |
| 12 | SCR-012 | Add New Book | AddBookActivity.kt | Teacher dashboard |
| 13 | SCR-013 | QR Display | QRCodeDisplayActivity.kt | After adding book |
| 14 | SCR-014 | All Transactions | AllTransactionsActivity.kt | Teacher dashboard |
| 15 | SCR-015 | Student Profile | ProfileActivity.kt | Top bar profile icon |

---

# SECTION 17: TESTING CHECKLIST

## 17.1 Functional Tests

```
✅ SPLASH SCREEN
□ App launches and shows splash
□ Redirects to Onboarding if first time
□ Redirects to Home if returning user
□ Overdue check runs in background

✅ PROFILE SETUP
□ Student profile creation works
□ Teacher profile creation works
□ Validation shows errors for empty fields
□ "Welcome back" detection works
□ Profile photo upload works (optional)

✅ BOOK CATALOG
□ All books display in grid
□ Category filter works correctly
□ Search returns correct results
□ Availability badge shows correctly
□ Empty state shows when no books

✅ QR SCANNER - BORROW
□ Camera opens correctly
□ QR code detected and vibrates
□ Book found in DB
□ Transaction created with correct due date
□ Book availability decremented
□ Success dialog shows with due date

✅ QR SCANNER - RETURN
□ Active transaction found
□ Transaction updated to RETURNED
□ Book availability incremented
□ Overdue detection works (late return)
□ Success dialog shows correct message

✅ OVERDUE SYSTEM
□ Books past due date marked OVERDUE
□ OVERDUE shown in RED color
□ Teacher dashboard shows overdue count
□ Overdue badge on history tab

✅ REVIEWS
□ Review button only shows after book returned
□ Star rating works (1-5)
□ Text input validates length
□ Average rating recalculated after review
□ One review per user per book (update existing)

✅ TEACHER DASHBOARD
□ Stats show correct numbers
□ Overdue list shows with student info
□ ADD BOOK flow completes
□ QR code generated and displayable
□ QR code saveable to gallery
```

## 17.2 Device Compatibility Tests

```
□ Android 6.0 (API 23) - Minimum
□ Android 10.0 (API 29)
□ Android 13.0 (API 33)
□ Android 14.0 (API 34)
□ Small screen (5 inch)
□ Medium screen (6 inch)
□ Large screen (6.7 inch)
□ Low-end device (2GB RAM)
```

---

# SECTION 18: FINAL IMPLEMENTATION NOTES FOR DEVELOPER

## 18.1 Priority Order for Development

```
PHASE 1 — Core Foundation (Week 1-2):
1. Room Database setup (all entities, DAOs)
2. Repository layer
3. SplashActivity
4. ProfileSetupActivity
5. HomeActivity + HomeFragment skeleton

PHASE 2 — Main Features (Week 3-4):
6. CatalogFragment + BookAdapter
7. BookDetailActivity
8. QRScannerActivity (borrow flow)
9. Transaction success dialogs
10. HistoryFragment

PHASE 3 — Teacher + Advanced (Week 5-6):
11. QR return flow
12. TeacherDashboardActivity
13. AddBookActivity
14. QRCodeDisplayActivity
15. Reviews system

PHASE 4 — Polish (Week 7):
16. Onboarding screens
17. Profile view/edit
18. Animations and transitions
19. Error handling refinement
20. Testing and bug fixes
```

## 18.2 Key Implementation Notes

```
1. ALL database operations MUST run in coroutines 
   (viewModelScope.launch or IO dispatcher)

2. ALL UI updates MUST happen on Main thread
   (observe LiveData in fragments/activities)

3. Image loading ALWAYS use Glide:
   Glide.with(context).load(imagePath).placeholder(R.drawable.book_placeholder)
   .into(imageView)

4. QR Scanner MUST stop analyzing after first valid scan
   (prevent multiple triggers)

5. Overdue check runs BOTH:
   - On app start (in SplashActivity)
   - Via WorkManager every 1 hour in background

6. User session stored in SharedPreferences (NOT Room DB)
   - userId saved → auto-login on next app open

7. Book cover images saved to:
   context.filesDir + "/book_covers/" + bookId + ".jpg"

8. Profile photos saved to:
   context.filesDir + "/profiles/" + userId + ".jpg"

9. Never delete data from Room DB — use soft delete 
   (is_active = false) to preserve history

10. OVERDUE color RED (#D32F2F) must be applied to:
    - Transaction card background (#FFEBEE)
    - Card stroke color (#D32F2F)
    - Status text color (#D32F2F)
    - Warning icon tint (#D32F2F)
    These 4 places ALWAYS together for OVERDUE items
```

---

# APPENDIX A: QUICK REFERENCE CARD

```
┌─────────────────────────────────────────────────────────┐
│              NAMMA-PUSTAKA QUICK REFERENCE              │
├─────────────────────────────────────────────────────────┤
│ DATABASE: Room (SQLite, offline)                        │
│ ARCH: MVVM + Repository + LiveData                      │
│ MIN SDK: 23 (Android 6.0)                               │
│ LANGUAGE: Kotlin + Coroutines                           │
├─────────────────────────────────────────────────────────┤
│ TABLES: users | books | borrow_transactions | reviews   │
├─────────────────────────────────────────────────────────┤
│ QR FORMAT: NP_BOOK_{bookId}_{8charUUID}                 │
│ DUE PERIOD: 14 days from borrow date                    │
│ MAX BORROW: 3 books per student at once                 │
│ OVERDUE COLOR: #D32F2F (RED) + #FFEBEE (background)    │
├─────────────────────────────────────────────────────────┤
│ USER TYPES: STUDENT | TEACHER                           │
│ CATEGORIES: STORY | SCIENCE | HISTORY                   │
│ TX STATUS: ACTIVE | RETURNED | OVERDUE                  │
├─────────────────────────────────────────────────────────┤
│ PRIMARY COLOR: #1B5E20 (Deep Forest Green)              │
│ ACCENT COLOR:  #FFD54F (Golden Yellow)                  │
│ OVERDUE RED:   #D32F2F                                  │
└─────────────────────────────────────────────────────────┘
```

---

**END OF SOP DOCUMENT**

**Document Version:** 1.0  
**Project:** Namma-Pustaka Smart Library Assistant  
**Total Screens:** 15  
**Total Database Tables:** 4  
**Platform:** Android (Kotlin)  
**Architecture:** MVVM + Room + LiveData + Coroutines  

*This document contains everything needed to build the complete Namma-Pustaka application from scratch. All screens, logic, database schema, navigation flows, color codes, and implementation prompts are included.*