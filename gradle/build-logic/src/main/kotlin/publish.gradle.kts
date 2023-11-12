plugins {
    id("java")
    id("maven-publish")
    id("signing")
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications.register<MavenPublication>("maven") {
        from(components["java"])
    }
    publications.withType<MavenPublication>().configureEach {
        pom {
            name.set("app.softwork Oracle JDBC Driver and SqlDelight Dialect")
            description.set("A Oracle JDBC driver including support for SqlDelight")
            url.set("https://github.com/hfhbd/sqldelight-oracle-dialect")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("hfhbd")
                    name.set("Philip Wedemann")
                    email.set("mybztg+mavencentral@icloud.com")
                }
            }
            scm {
                connection.set("scm:git://github.com/hfhbd/sqldelight-oracle-dialect.git")
                developerConnection.set("scm:git://github.com/hfhbd/sqldelight-oracle-dialect.git")
                url.set("https://github.com/hfhbd/sqldelight-oracle-dialect")
            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    signingKey?.let {
        useInMemoryPgpKeys(it, signingPassword)
        sign(publishing.publications)
    }
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}
