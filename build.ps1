$ErrorActionPreference = "Stop"

$studioJbr = "C:\Program Files\Android\Android Studio\jbr"
$gradle = "$env:USERPROFILE\.gradle\wrapper\dists\gradle-9.2.1-bin\2t0n5ozlw9xmuyvbp7dnzaxug\gradle-9.2.1\bin\gradle.bat"

if (!(Test-Path $studioJbr)) {
    throw "Android Studio JBR not found at $studioJbr"
}

if (!(Test-Path $gradle)) {
    throw "Gradle 9.2.1 was not found in the local Gradle cache."
}

$env:JAVA_HOME = $studioJbr
& $gradle assembleDebug
