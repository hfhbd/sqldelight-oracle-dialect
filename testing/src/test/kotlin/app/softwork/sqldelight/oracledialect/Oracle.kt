package app.softwork.sqldelight.oracledialect

import app.cash.sqldelight.driver.jdbc.*
import oracle.jdbc.datasource.impl.*
import org.testcontainers.containers.*

object Oracle {
    val driver = run {
        val container = OracleContainer("gvenzl/oracle-xe:21-slim-faststart")
        container.start()
        OracleDataSource().apply {
            databaseName = "xepdb1"
            user = "test"
            portNumber = container.firstMappedPort
            setPassword("test")
        }.asJdbcDriver()
    }
}
