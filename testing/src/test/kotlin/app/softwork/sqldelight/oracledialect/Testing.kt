package app.softwork.sqldelight.oracledialect

import app.softwork.sqldelight.oracledialect.Oracle.driver
import java.time.*
import kotlin.test.*

class Testing {
    @Test
    fun select() {
        TestingDB.Schema.create(driver)
        val db = TestingDB(driver)

        val epoch = LocalDate.ofEpochDay(0)

        assertEquals(emptyList(), db.fooQueries.getAll().executeAsList())
        db.fooQueries.new(Foo(42, "Foo", 1.toBigDecimal(), epoch))
        assertEquals(listOf(Foo(42, "Foo", 1.toBigDecimal(), epoch)), db.fooQueries.getAll().executeAsList())

        db.fooQueries.create(Foo(100, "Bar", 1.toBigDecimal(), epoch))
        assertEquals(listOf(Foo(42, "Foo", 1.toBigDecimal(), epoch), Foo(100, "Bar", 1.toBigDecimal(), LocalDate.EPOCH)), db.fooQueries.getAll().executeAsList())
    }
}
