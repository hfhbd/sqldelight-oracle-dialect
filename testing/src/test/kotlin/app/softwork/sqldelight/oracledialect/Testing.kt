package app.softwork.sqldelight.oracledialect

import java.math.BigDecimal
import java.time.*
import kotlin.test.*

class Testing {
    @Test
    fun select() = runTest {
        val db = TestingDB(this)

        val epoch = LocalDate.ofEpochDay(0).atTime(0, 0, 0)
        val now = ZonedDateTime.now()

        assertEquals(emptyList(), db.fooQueries.getAll().executeAsList())

        db.fooQueries.new(Foo(42, "Foo", "BAR", 1.toBigDecimal(), epoch, null))
        assertEquals(
            listOf(Foo(42, "Foo", "BAR", 1.toBigDecimal(), epoch, null)),
            db.fooQueries.getAll().executeAsList()
        )

        db.fooQueries.create(Foo(100, "Bar", "BAR", 1.toBigDecimal(), epoch, now))
        assertEquals(
            listOf(
                Foo(42, "Foo", "BAR", 1.toBigDecimal(), epoch, null),
                Foo(100, "Bar", null, 1.toBigDecimal(), epoch, now)
            ),
            db.fooQueries.getAll().executeAsList()
        )
    }

    @Test
    fun round() = runTest {
        val db = TestingDB(this)
        db.fooQueries.new(Foo(42, "Foo", "BAR", 1.toBigDecimal(), LocalDateTime.now(), ZonedDateTime.now()))

        val s = db.fooQueries.testRound().executeAsOne()
        val integer: Long = s.round
        assertEquals(42L, integer)
        val number: BigDecimal = s.round_
        assertEquals(BigDecimal.valueOf(42L), number)
    }

    @Test
    fun dates() = runTest {
        val db = TestingDB(this)
        db.fooQueries.new(Foo(42, "Foo", "BAR", 1.toBigDecimal(), LocalDateTime.now(), ZonedDateTime.now()))

        val s = db.fooQueries.testDates().executeAsOne()
        val currentDate: LocalDateTime = s.currentDate
        val currentTimestamp: ZonedDateTime = s.currentTimestamp
        val sysDate: LocalDateTime = s.sysDate
        val sysTimestamp: ZonedDateTime = s.sysTimestamp
        val localTimestamp: LocalDateTime = s.localTimestamp
    }

    @Test
    fun testMinusDate() = runTest {
        val db = TestingDB(this)
        db.fooQueries.new(Foo(42, "Foo", "BAR", 1.toBigDecimal(), LocalDateTime.now(), ZonedDateTime.now()))

        val i: Long? = null
        val l: Long? = 42
        val s = db.fooQueries.testMinusDate(i, l).executeAsOne()
        val a: LocalDateTime? = s.expr
        assertNull(a)
        val b: LocalDateTime = s.expr_
    }
}
