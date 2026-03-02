plugins {
    java
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    // 直接使用 Paper API
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    
    // 本地库
    implementation(fileTree("lib") {
        include("*.jar")
    })
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    jar {
        from("lib") {
            into("lib")
            include("**/*.jar")
        }
    }
}
