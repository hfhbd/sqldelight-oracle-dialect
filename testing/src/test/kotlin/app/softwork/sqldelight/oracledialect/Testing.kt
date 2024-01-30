package app.softwork.sqldelight.oracledialect

import java.math.BigDecimal
import java.time.*
import kotlin.test.*

class Testing {
    @Test
    fun select() = runTest {
        TestingDB.Schema.create(this)
        val db = TestingDB(this)

        val epoch = LocalDate.ofEpochDay(0)

        assertEquals(emptyList(), db.fooQueries.getAll().executeAsList())
        db.fooQueries.new(Foo(42, "Foo", "BAR", 1.toBigDecimal(), epoch))
        assertEquals(
            listOf(Foo(42, "Foo", "BAR", 1.toBigDecimal(), epoch)),
            db.fooQueries.getAll().executeAsList()
        )

        db.fooQueries.create(Foo(100, "Bar", "BAR", 1.toBigDecimal(), epoch))
        assertEquals(
            listOf(
                Foo(42, "Foo", "BAR", 1.toBigDecimal(), epoch),
                Foo(100, "Bar", null, 1.toBigDecimal(), epoch)
            ),
            db.fooQueries.getAll().executeAsList()
        )
    }

    @Test
    fun round() = runTest {
        TestingDB.Schema.create(this)
        val db = TestingDB(this)

        val s = db.fooQueries.testRound().executeAsOne()
        val integer: Long = s.round
        assertEquals(42L, integer)
        val number: BigDecimal = s.round_
        assertEquals(BigDecimal.valueOf(42L), number)
    }

    @Test
    fun dates() = runTest {
        TestingDB.Schema.create(this)
        val db = TestingDB(this)

        val s = db.fooQueries.testDates().executeAsOne()
        val currentDate: LocalDate = s.currentDate
        val currentTimestamp: Instant = s.currentTimestamp
        val sysDate: LocalDate = s.sysDate
        val sysTimestamp: Instant = s.sysTimestamp
        val localTimestamp: LocalDateTime = s.localTimestamp
    }
}
