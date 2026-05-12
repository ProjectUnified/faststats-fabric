import com.vanniktech.maven.publish.DeploymentValidation
import com.vanniktech.maven.publish.JavaLibrary
import com.vanniktech.maven.publish.JavadocJar

plugins {
    // This plugin applies the correct loom variant based on the Minecraft version
    id("dev.kikugie.loom-back-compat")
    id("com.vanniktech.maven.publish")
}

// DO NOT set group = ...!
version = "${property("mod.version")}+${sc.current.version}"
base.archivesName = property("mod.id") as String

val requiredJava: JavaVersion = when {
    sc.current.parsed >= "26.1" -> JavaVersion.VERSION_25
    else -> JavaVersion.VERSION_21
}

// This can be used for publishing on Modrinth and Curseforge
val compatibleVersions: List<String> = sc.properties.rawOrNull("mod", "mc_releases")
    ?.asList().orEmpty().map { it.toString() }

repositories {
    /**
     * Restricts dependency search of the given [groups] to the [maven URL][url],
     * improving the setup speed.
     */
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
    maven {
        name = "faststatsReleases"
        url = uri("https://repo.faststats.dev/releases")
    }
}

dependencies {
    /**
     * Fetches only the required Fabric API modules to not waste time downloading all of them for each version.
     * @see <a href="https://github.com/FabricMC/fabric">List of Fabric API modules</a>
     */
    fun fapi(vararg modules: String) {
        for (it in modules) modImplementation(fabricApi.module(it, property("deps.fabric_api") as String))
    }

    minecraft("com.mojang:minecraft:${sc.current.version}")
    loomx.applyMojangMappings()
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    fapi("fabric-lifecycle-events-v1")

    api("dev.faststats.metrics:core:${property("deps.faststats")}")
}

java {
    withSourcesJar()
    targetCompatibility = requiredJava
    sourceCompatibility = requiredJava
}

mavenPublishing {
    publishToMavenCentral(automaticRelease = true, validateDeployment = DeploymentValidation.VALIDATED)
    if (gradle.startParameter.taskNames.contains("publishToMavenCentral")) {
        signAllPublications()
    }

    configure(
        JavaLibrary(
            javadocJar = JavadocJar.Javadoc(),
            sourcesJar = com.vanniktech.maven.publish.SourcesJar.Sources(),
        )
    )

    coordinates("${property("mod.group")}", "${property("mod.id")}", "${property("mod.version")}+${stonecutter.current.version}")

    pom {
        name = "FastStats Fabric"
        description = "A FabricMC project to implement Topper"
        url = "https://github.com/ProjectUnified/faststats-fabric"

        licenses {
            license {
                name = "MIT License"
                url = "https://github.com/ProjectUnified/faststats-fabric/blob/master/LICENSE"
            }
        }

        developers {
            developer {
                name = "HSGamer"
                email = "huynhqtienvtag@gmail.com"
                url = "https://github.com/HSGamer"
            }
        }

        issueManagement {
            system = "github"
            url = "https://github.com/ProjectUnified/faststats-fabric/issues"
        }

        scm {
            connection = "scm:git:https://github.com/ProjectUnified/faststats-fabric.git"
            developerConnection = "scm:git:git@github.com:ProjectUnified/faststats-fabric.git"
            url = "https://github.com/ProjectUnified/faststats-fabric"
        }

        withXml {
            val repositories = asNode().appendNode("repositories")
            val repository = repositories.appendNode("repository")
            repository.appendNode("id", "faststatsReleases")
            repository.appendNode("url", "https://repo.faststats.dev/releases")
        }
    }
}
