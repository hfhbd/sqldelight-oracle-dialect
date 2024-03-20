package app.softwork.sqldelight.oracledialect

import app.cash.sqldelight.db.*
import oracle.jdbc.datasource.impl.*
import org.testcontainers.containers.*
import org.testcontainers.utility.*

fun runTest(action: SqlDriver.() -> Unit) {
    val myImage =
        DockerImageName.parse("gvenzl/oracle-free:23-slim-faststart").asCompatibleSubstituteFor("gvenzl/oracle-xe")
    val container = OracleContainer(myImage)
    container.start()

    OracleDataSource().apply {
        driverType = "thin"
        serverName = "localhost"
        serviceName = "FREEPDB1"
        portNumber = container.oraclePort
        databaseName = "FREEPDB1"
        user = "SYSTEM"
        setPassword("test")
    }.asOracleJdbcDriver().use {
        TestingDB.Schema.create(it)
    }

    val newUser = OracleDataSource().apply {
        driverType = "thin"
        serverName = "localhost"
        serviceName = "FREEPDB1"
        portNumber = container.oraclePort
        databaseName = "FREEPDB1"
        user = "A-B"
        setPassword("myStrongPassword")
    }.asOracleJdbcDriver()

    try {
        newUser.action()
    } finally {
        newUser.close()
    }
}
