buildscript {
    repositories {
        google()
        jcenter()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:3.6.3")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.2.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.72")
        classpath("com.apollographql.apollo:apollo-gradle-plugin:2.1.0")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

