plugins {
    kotlin("multiplatform").version(libs.versions.kotlin) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.maven.publish) apply false
}
