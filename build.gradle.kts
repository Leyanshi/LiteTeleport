plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.7.1" // Paper 开发插件
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
    // Paper API 依赖
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")
    
    // 添加 lib 文件夹内的所有 JAR 文件作为依赖
    implementation(fileTree("lib") {
        include("*.jar")
    })
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21)) // Paper 1.21.8 使用 Java 21
    }
}

tasks {
    // 配置编译任务
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
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
