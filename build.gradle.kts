import net.fabricmc.loom.LoomGradleExtension
import net.raphimc.classtokenreplacer.extension.ClassTokenReplacerExtension

plugins {
	id("fabric-loom") version "1.9-SNAPSHOT"
	`maven-publish`
	id("net.raphimc.class-token-replacer") version "1.0.0"
}

val minecraftVersion = providers.gradleProperty("minecraft_version").get()
val modVersion = providers.gradleProperty("mod_version").get()
val mavenGroup = providers.gradleProperty("maven_group").get()
val archiveBaseName = providers.gradleProperty("archives_base_name").get()

version = modVersion
group = mavenGroup

base {
	archivesName = archiveBaseName
}

val rusherhackApi by configurations.creating {
	isCanBeResolved = true
}

val productionRuntime by configurations.creating {
	extendsFrom(
		configurations.getByName("minecraftLibraries"),
		configurations.getByName("loaderLibraries"),
		configurations.getByName("minecraftRuntimeLibraries"),
	)
}

configurations.compileOnly {
	extendsFrom(rusherhackApi)
}

repositories {
	mavenCentral()
	maven {
		name = "rusherhack"
		// releases repository will have the latest api version for last stable rusherhack release
		// snapshots will always be the latest api version
		// url = uri("https://maven.rusherhack.org/releases")
		url = uri("https://maven.rusherhack.org/snapshots")
	}

	maven {
		name = "ParchmentMC"
		url = uri("https://maven.parchmentmc.org")
	}
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraftVersion")
	add(productionRuntime.name, modImplementation("net.fabricmc:fabric-loader:0.16.7")!!)
	add(productionRuntime.name, "net.fabricmc:intermediary:1.20.4")

	// mojmap + parchment mappings
	mappings(
		loom.layered {
			officialMojangMappings()
			parchment("org.parchmentmc.data:parchment-1.20.4:2024.04.14@zip")
		}
	)
	rusherhackApi("org.rusherhack:rusherhack-api:1.20.4-SNAPSHOT")
}

val copyPluginToRunDir by tasks.registering(Copy::class) {
	group = "build"
	dependsOn(tasks.remapJar)
	from(tasks.remapJar.map { it.outputs })
	into(file("run/rusherhack/plugins"))
}

tasks.register<JavaExec>("runPlugin") {
	group = "build"
	dependsOn(tasks.remapJar, tasks.named("downloadAssets"), copyPluginToRunDir)
	classpath(productionRuntime)
	mainClass = "net.fabricmc.loader.impl.launch.knot.KnotClient"
	workingDir = file("run")

	doFirst {
		val loomExtension = extensions.getByType<LoomGradleExtension>()
		classpath(loomExtension.minecraftProvider.minecraftClientJar)
		workingDir.mkdirs()

		args(
			"--assetIndex",
			loomExtension.minecraftProvider.versionInfo.assetIndex().fabricId(loomExtension.minecraftProvider.minecraftVersion()),
			"--assetsDir",
			file(loomExtension.files.userCache).resolve("assets").absolutePath,
			"--gameDir",
			workingDir.absolutePath,
		)

		val rusherLoaderJarFile = layout.projectDirectory.file("lib/rusherhack-loader.jar").asFile
		if (!rusherLoaderJarFile.exists()) {
			throw GradleException("rusherhack-loader.jar must be copied to the lib directory!")
		}
		val rusherLoaderJarPath = rusherLoaderJarFile.absolutePath

		jvmArgs(
			"-Drusherhack.enablePlugins=true",
			"-Dfabric.addMods=$rusherLoaderJarPath",
		)
	}
}

loom {
	// apply accesswidener from rusherhack-api
	for (file in zipTree(rusherhackApi.singleFile)) {
		if (file.name == "rusherhack.accesswidener") {
			accessWidenerPath = file
		}
	}

	// disable run configs
	runConfigs.configureEach {
		setIdeConfigGenerated(false)
	}
}

val targetJavaVersion = 17
tasks.withType<JavaCompile>().configureEach {
	// ensure that the encoding is set to UTF-8, no matter what the system default is
	// this fixes some edge cases with special characters not displaying correctly
	// see http://yodaconditions.net/blog/fix-for-java-file-encoding-problems-with-gradle.html
	// If Javadoc is generated, this must be specified in that task too.
	options.encoding = "UTF-8"
	options.release = targetJavaVersion
}

java {
	val javaVersion = JavaVersion.toVersion(targetJavaVersion)
	if (JavaVersion.current() < javaVersion) {
		toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
	}
}

tasks.processResources {
	inputs.property("mod_version", modVersion)

	filesMatching("rusherhack-plugin.json") {
		expand("mod_version" to modVersion)
	}
}

sourceSets {
	main {
		extensions.configure<ClassTokenReplacerExtension>("classTokenReplacer") {
			property("\${version}", modVersion)
		}
	}
}
