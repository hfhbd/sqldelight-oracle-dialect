package app.softwork.sqldelight.oracledialect

import app.cash.sqldelight.db.*
import app.cash.sqldelight.driver.jdbc.*
import oracle.jdbc.datasource.impl.*
import org.testcontainers.containers.*
import org.testcontainers.utility.*

fun runTest(action: SqlDriver.(SqlDriver) -> Unit) {
    val myImage =
        DockerImageName.parse("gvenzl/oracle-free:23-slim-faststart").asCompatibleSubstituteFor("gvenzl/oracle-xe")
    val container = OracleContainer(myImage)
    container.withDatabaseName("FREEPDB1")
    container.start()
    val systemUser = OracleDataSource().apply {
        driverType = "thin"
        serverName = "localhost"
        serviceName = "FREEPDB1"
        portNumber = container.oraclePort
        databaseName = "xepdb1"
        user = "SYSTEM"
        setPassword("test")
    }.asJdbcDriver()

    val newUser = OracleDataSource().apply {
        driverType = "thin"
        serverName = "localhost"
        serviceName = "FREEPDB1"
        portNumber = container.oraclePort
        databaseName = "xepdb1"
        user = "A-B"
        setPassword("myStrongPassword")
    }.asJdbcDriver()

    try {
        systemUser.action(newUser)
    } finally {
        systemUser.close()
        newUser.close()
    }
}
