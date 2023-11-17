package app.softwork.sqldelight.oracledialect

import app.softwork.sqldelight.oracledialect.Oracle.driver
import java.time.*
import kotlin.test.*

class Testing {
    @Test
    fun select() {
        TestingDB.Schema.create(driver)
        val db = TestingDB(driver)

        assertEquals(emptyList(), db.fooQueries.getAll().executeAsList())
        db.fooQueries.new(Foo(42, "Foo", 1.toBigDecimal(), LocalDate.EPOCH))
        assertEquals(listOf(Foo(42, "Foo", 1.toBigDecimal(), LocalDate.EPOCH)), db.fooQueries.getAll().executeAsList())

        db.fooQueries.create(Foo(100, "Bar", 1.toBigDecimal(), LocalDate.EPOCH))
        assertEquals(listOf(Foo(42, "Foo", 1.toBigDecimal(), LocalDate.EPOCH), Foo(100, "Bar", 1.toBigDecimal(), LocalDate.EPOCH)), db.fooQueries.getAll().executeAsList())
    }
}
