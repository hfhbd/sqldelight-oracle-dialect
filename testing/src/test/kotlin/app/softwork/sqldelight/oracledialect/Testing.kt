package app.softwork.sqldelight.oracledialect

import app.cash.sqldelight.driver.jdbc.JdbcDriver
import oracle.sql.DATE
import oracle.sql.NUMBER
import java.math.BigDecimal
import java.sql.Types
import java.time.*
import kotlin.test.*

class Testing {
    @Test
    fun select() = runTest {
        val db = TestingDB(this)

        val epoch = LocalDate.ofEpochDay(0).atTime(0, 0, 0)
        val now = ZonedDateTime.now()

        assertEquals(emptyList(), db.fooQueries.getAll().executeAsList())
        this as JdbcDriver
        val preparedStatement = this.getConnection().prepareStatement(
            """INSERT INTO SYSTEM.foo (id, name, name2, nu, dd, tz) VALUES (?, ?, ?, ?, ?, ?)"""
        )
        preparedStatement.setLong(1, 42)
        preparedStatement.setString(2, "Foo")
        preparedStatement.setString(3, "BAR")
        preparedStatement.setObject(4, NUMBER(1.toBigDecimal()))
        preparedStatement.setObject(5, DATE(epoch))
        preparedStatement.setNull(6, Types.OTHER)

        preparedStatement.execute()

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
        val localTimestamp: Instant = s.localTimestamp
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
