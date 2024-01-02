plugins {
    java
}

allprojects {
    group = "org.anvilpowered"
    version = "0.8.1-SNAPSHOT"
}

subprojects {
    apply(plugin = "java")
    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://repo.spongepowered.org/maven")
        maven("https://jetbrains.bintray.com/xodus")
    }
    dependencies {
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.0")
        compileOnly("org.anvilpowered:anvil-api:0.3") {
            exclude(group = "org.jetbrains.xodus")
        }
        implementation("org.jetbrains.xodus:xodus-openAPI:1.3.232")
    }
//    test {
//        useJUnitPlatform()
//    }
//    if (project.hasProperty("buildNumber") && version.toString().contains("-SNAPSHOT")) {
//        version = version.toString().replace("-SNAPSHOT", "-RC${buildNumber}")
//    }
}
