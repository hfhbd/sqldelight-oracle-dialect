package app.softwork.sqldelight.oracledialect

import app.cash.sqldelight.db.SqlPreparedStatement
import app.cash.sqldelight.driver.jdbc.JdbcPreparedStatement
import java.math.BigDecimal
import java.sql.PreparedStatement
import java.sql.Types

public class OraclePreparedStatement(
    private val preparedStatement: PreparedStatement,
) : SqlPreparedStatement by JdbcPreparedStatement(preparedStatement) {
    public fun bindShort(index: Int, short: Short?) {
        if (short == null) {
            preparedStatement.setNull(index + 1, Types.SMALLINT)
        } else {
            preparedStatement.setShort(index + 1, short)
        }
    }

    public fun bindInt(index: Int, int: Int?) {
        if (int == null) {
            preparedStatement.setNull(index + 1, Types.INTEGER)
        } else {
            preparedStatement.setInt(index + 1, int)
        }
    }

    public fun bindObject(index: Int, obj: Any?, type: Int) {
        if (obj == null) {
            preparedStatement.setNull(index + 1, type)
        } else {
            preparedStatement.setObject(index + 1, obj, type)
        }
    }

    public fun bindBigDecimal(index: Int, decimal: BigDecimal?) {
        preparedStatement.setBigDecimal(index + 1, decimal)
    }
}
