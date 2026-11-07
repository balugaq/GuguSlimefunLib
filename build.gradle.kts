plugins {
    java
    alias(libs.plugins.shadow)
}

group = "me.ddggdd135"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.slimefun4)

    implementation(libs.nbt.api)
    implementation(libs.matlib)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks {
    compileJava {
        options.release = 21
    }

    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version.toString())
        }
    }

    shadowJar {
        archiveBaseName.set("GuguSlimefunLib")
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("")

        // 不用 minimize()：本库是依赖库，matlib/nbtapi 可能被反射访问，裁剪有风险

        // Relocations
        relocate("de.tr7zw.changeme.nbtapi", "me.ddggdd135.guguslimefunlib.libraries.nbtapi")
        relocate("me.matl114.matlib", "me.ddggdd135.guguslimefunlib.libraries.matlib")

        exclude("META-INF/*")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }
}

defaultTasks("clean", "build")
