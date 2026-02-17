
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Declare plugins for submodules, don't apply them at the root project.
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.navigation.safe.args) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    //alias(libs.plugins.gms.google.services) apply false
}
