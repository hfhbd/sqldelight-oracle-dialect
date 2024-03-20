package app.softwork.sqldelight.oracledialect

import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.driver.jdbc.JdbcCursor
import java.math.BigDecimal
import java.sql.ResultSet

public class OracleJdbcCursor(public val resultSet: ResultSet) : SqlCursor by JdbcCursor(resultSet) {
    public fun getShort(index: Int): Short? = getAtIndex(index, resultSet::getShort)
    public fun getInt(index: Int): Int? = getAtIndex(index, resultSet::getInt)

    public fun getBigDecimal(index: Int): BigDecimal? = resultSet.getBigDecimal(index + 1)
    public inline fun <reified T : Any> getObject(index: Int): T? = resultSet.getObject(index + 1, T::class.java)

    private fun <T> getAtIndex(index: Int, converter: (Int) -> T): T? =
        converter(index + 1).takeUnless { resultSet.wasNull() }
}
