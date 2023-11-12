# Module sqldelight-oracle-dialect

An Oracle dialect for SqlDelight using JDBC.

- [Source code](https://github.com/hfhbd/sqldelight-oracle-dialect)

## Install

This package is uploaded to MavenCentral and supports the JVM.


````kotlin
plugins {
    kotlin("jvm") version "1.9.20"
    id("app.cash.sqldelight") version "2.0.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("app.cash.sqldelight:jdbc-driver:2.0.0")

    testImplementation(kotlin("test-junit"))
    testImplementation("org.testcontainers:oracle:1.17.3")
    testImplementation("com.oracle.database.jdbc:ojdbc8:23.3.0.23.09")
    testRuntimeOnly("ch.qos.logback:logback-classic:1.2.11")
}

sqldelight {
    databases.register("OracleDatabase") {
        dialect("app.softwork:sqldelight-oracle-dialect:LATEST")
    }
}
````

## License

Apache 2
