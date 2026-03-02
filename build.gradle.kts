plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.7.1"
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
    // 指定具体的 Paper 版本 - 修复多文件问题
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")
    
    // 编译时依赖 Paper API
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    
    // 添加 lib 文件夹内的所有 JAR 文件作为依赖
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

    processResources {
        filteringCharset = "UTF-8"
        from(sourceSets.main.get().resources.srcDirs) {
            include("**/*.yml", "**/*.properties", "**/*.json")
        }
    }

    // 重新打包 - 将 lib 文件夹包含到 JAR 中
    jar {
        // 从 lib 文件夹复制所有 JAR 到输出 JAR 的 lib 目录
        from("lib") {
            into("lib")
            include("**/*.jar")
        }
        
        manifest {
            attributes(
                "paperweight-mappings-namespace" to "mojang"
            )
        }
    }

    // 如果需要创建包含所有依赖的胖 JAR，使用这个任务
    register<Jar>("fatJar") {
        group = "build"
        description = "创建一个包含所有依赖的胖 JAR"
        
        archiveClassifier.set("fat")
        
        from(sourceSets.main.get().output)
        
        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get()
                .filter { it.name.endsWith(".jar") }
                .map { zipTree(it) }
        }) {
            exclude("META-INF/**")
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
        
        // 包含 lib 文件夹
        from("lib") {
            into("lib")
            include("**/*.jar")
        }
        
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

// 确保 paperweight 配置正确
paperweight {
    // 如果需要重新映射，可以在这里配置
}        options.release.set(21)
    }

    // 创建可执行 JAR 的任务
    val jar by existing(Jar::class) {
        // 确保 lib 文件夹中的 JAR 被包含到 JAR 文件中
        from("lib") {
            into("lib") // 将 lib 文件夹复制到输出 JAR 的 lib 目录下
            include("**/*.jar")
        }
    }

    // 如果需要创建一个包含所有依赖的胖 JAR
    val fatJar = register<Jar>("fatJar") {
        group = "build"
        description = "创建一个包含所有依赖的胖 JAR"
        
        from(sourceSets.main.get().output)
        
        // 包含所有运行时依赖
        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
        })
        
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        
        manifest {
            attributes(
                "Main-Class" to "com.example.YourMainClass" // 如果有主类的话
            )
        }
        
        // 确保 lib 文件夹也被包含
        from("lib") {
            into("lib")
            include("**/*.jar")
        }
    }
}
