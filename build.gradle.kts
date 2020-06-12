buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.0.0")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
        classpath("com.squareup.sqldelight:gradle-plugin:1.3.0")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenLocal()
    }
}

apply(plugin = "org.jetbrains.kotlin.jvm")
apply(plugin = "com.squareup.sqldelight")
