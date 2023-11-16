package app.softwork.sqldelight.oracledialect

import app.cash.sqldelight.driver.jdbc.*
import oracle.jdbc.datasource.impl.*
import org.testcontainers.containers.*
import org.testcontainers.utility.*

object Oracle {
    val driver = run {
        val myImage = DockerImageName.parse("gvenzl/oracle-free:23-slim-faststart").asCompatibleSubstituteFor("gvenzl/oracle-xe")
        val container = OracleContainer(myImage)
        container.start()
        OracleDataSource().apply {
            driverType = "thin"
            serverName = "localhost"
            portNumber = container.firstMappedPort
            serviceName = "xepdb1"
            user = "test"
            setPassword("test")
        }.asJdbcDriver()
    }
}
