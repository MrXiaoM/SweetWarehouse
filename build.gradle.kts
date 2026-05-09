plugins {
    java
    `maven-publish`
    id ("com.gradleup.shadow") version "9.3.0"
}

group = "top.mrxiaom"
version = "1.0.0"
val targetJavaVersion = 8
val shadowGroup = "top.mrxiaom.sweet.warehouse.libs"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://mvn.lumine.io/repository/maven/")
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains:annotations:24.0.0")
    
    compileOnly("net.milkbowl.vault:VaultAPI:1.7")
    compileOnly("me.clip:placeholderapi:2.11.6")

    compileOnly("io.lumine:Mythic-Dist:4.13.0")
    compileOnly("io.lumine:Mythic:5.6.2")
    compileOnly("io.lumine:LumineUtils:1.20-SNAPSHOT")

    implementation("com.zaxxer:HikariCP:4.0.3") { isTransitive = false }
    implementation("top.mrxiaom:PluginBase:1.4.5")
}
java {
    disableAutoTargetJvm()
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
}
tasks {
    shadowJar {
        configurations.add(project.configurations.runtimeClasspath.get())
        mapOf(
            "top.mrxiaom.pluginbase" to "base",
            "com.zaxxer.hikari" to "hikari",
        ).forEach { (original, target) ->
            relocate(original, "$shadowGroup.$target")
        }
    }
    val jarName = "${project.name}-$version.jar"
    val copyTask = register<Copy>("copyBuildArtifact") {
        dependsOn(shadowJar)
        from(shadowJar.get().outputs)
        rename { jarName }
        into(rootProject.file("out"))
    }
    build {
        dependsOn(copyTask)
    }
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(sourceSets.main.get().resources.srcDirs) {
            expand(mapOf("version" to version))
            include("plugin.yml")
        }
    }
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = rootProject.name
            version = project.version.toString()

            artifact(tasks["shadowJar"]).classifier = null
        }
    }
}
