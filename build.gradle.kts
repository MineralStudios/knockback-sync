import java.io.ByteArrayOutputStream

plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.5" apply false
    id("maven-publish")
}

val fullVersion = "1.3.5"
val snapshot = true
val githubRepo = System.getenv("GITHUB_REPOSITORY") ?: project.findProperty("githubRepo").toString()

allprojects {
    fun getVersionMeta(includeHash: Boolean): String {
        if (!snapshot) {
            return ""
        }
        var commitHash = ""
        if (includeHash && file(".git").isDirectory) {
            val stdout = ByteArrayOutputStream()
            exec {
                commandLine("git", "rev-parse", "--short", "HEAD")
                standardOutput = stdout
            }
            commitHash = "+${stdout.toString().trim()}"
        }
        return "$commitHash-SNAPSHOT"
    }

    group = "me.caseload.knockbacksync"
    version = "$fullVersion${getVersionMeta(true)}"
    ext["versionNoHash"] = "$fullVersion${getVersionMeta(false)}"
    ext["githubRepo"] = githubRepo

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.opencollab.dev/maven-snapshots/")
        maven("https://repo.papermc.io/repository/maven-public/")
        maven(url = "https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        maven("https://libraries.minecraft.net/")
        maven("https://maven.neoforged.net/releases")
        maven("https://repo.codemc.io/repository/maven-snapshots/")
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                artifactId = project.name
                version = SimpleDateFormat("yyyyMMdd-HH.mm.ss").format(Date())
            }
        }
    
        repositories {
            maven {
                name = "mineralDevPrivate"
                url = uri("https://repo.mineral.gg/private")
                credentials {
                    // cast findProperty(...) to String? since it can be null
                    username = findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                    password = findProperty("gpr.token") as String? ?: System.getenv("TOKEN")
                }
            }
        }
    }
}

subprojects {
    apply(plugin = "java")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
