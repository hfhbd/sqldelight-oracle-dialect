package app.softwork.sqldelight.oracledialect

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import app.cash.sqldelight.driver.jdbc.asJdbcDriver
import org.intellij.lang.annotations.Language
import javax.sql.DataSource

public fun DataSource.asOracleJdbcDriver(): SqlDriver {
    val delegate = asJdbcDriver()
    return object : SqlDriver by delegate {
        override fun execute(
            identifier: Int?,
            @Language("SQL") sql: String,
            parameters: Int,
            binders: (SqlPreparedStatement.() -> Unit)?,
        ): QueryResult<Long> {
            val (connection, onClose) = delegate.connectionAndClose()
            try {
                return QueryResult.Value(
                    connection.prepareStatement(sql).use { jdbcStatement ->
                        OraclePreparedStatement(jdbcStatement)
                            .apply { if (binders != null) this.binders() }
                            .execute()
                    },
                )
            } finally {
                onClose()
            }
        }

        override fun <R> executeQuery(
            identifier: Int?,
            @Language("SQL") sql: String,
            mapper: (SqlCursor) -> QueryResult<R>,
            parameters: Int,
            binders: (SqlPreparedStatement.() -> Unit)?,
        ): QueryResult<R> {
            val (connection, onClose) = delegate.connectionAndClose()
            try {
                return OraclePreparedStatement(connection.prepareStatement(sql))
                    .apply { if (binders != null) this.binders() }
                    .executeQuery(mapper)
            } finally {
                onClose()
            }
        }
    }
}
