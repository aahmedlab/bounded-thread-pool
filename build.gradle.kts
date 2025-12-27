plugins {
    id("java")
    id("maven-publish")
    id("com.palantir.git-version") version "3.0.0"
}

group = "io.github.abdol-ahmed.btp"

// Use git-version plugin for semantic versioning
val gitVersion: groovy.lang.Closure<String> by extra
version = gitVersion()

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.5.23")
    
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

// Configure Java source/target compatibility
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

// JitPack publishing configuration
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            
            groupId = "io.github.abdol-ahmed.btp"
            artifactId = "bounded-thread-pool"
            version = project.version.toString()
            
            pom {
                name.set("Bounded Thread Pool")
                description.set("A thread pool implementation with bounded queue and configurable rejection policies")
                url.set("https://github.com/abdol-ahmed/bounded-thread-pool")
                
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                
                developers {
                    developer {
                        id.set("abdol-ahmed")
                        name.set("Abdol Ahmed")
                        email.set("your-email@example.com")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/abdol-ahmed/bounded-thread-pool.git")
                    developerConnection.set("scm:git:ssh://github.com:abdol-ahmed/bounded-thread-pool.git")
                    url.set("https://github.com/abdol-ahmed/bounded-thread-pool/tree/main")
                }
            }
        }
    }
}