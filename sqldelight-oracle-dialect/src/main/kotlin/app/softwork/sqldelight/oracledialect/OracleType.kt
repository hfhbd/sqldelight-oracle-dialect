package app.softwork.sqldelight.oracledialect

import app.cash.sqldelight.dialect.api.*
import com.squareup.kotlinpoet.*

internal enum class OracleType(override val javaType: TypeName, val oracleType: TypeName? = null) : DialectType {
    SMALL_INT(SHORT) {
        override fun decode(value: CodeBlock) = CodeBlock.of("%L.toShort()", value)

        override fun encode(value: CodeBlock) = CodeBlock.of("%L.toLong()", value)
    },
    INTEGER(INT) {
        override fun decode(value: CodeBlock) = CodeBlock.of("%L.toInt()", value)

        override fun encode(value: CodeBlock) = CodeBlock.of("%L.toLong()", value)
    },
    BIG_INT(LONG),
    NUMBER(ClassName("java.math", "BigDecimal"), ClassName("oracle.sql", "NUMBER")) {
        override fun encode(value: CodeBlock): CodeBlock = CodeBlock.of("%T(%L)", oracleType, value)
        override fun decode(value: CodeBlock): CodeBlock = CodeBlock.of("%L.bigDecimalValue()", value)
    },
    BOOL(BOOLEAN) {
        override fun decode(value: CodeBlock) = CodeBlock.of("%L == 1L", value)

        override fun encode(value: CodeBlock) = CodeBlock.of("if (%L) 1L else 0L", value)
    },
    DATE(ClassName("java.time", "LocalDate"), ClassName("oracle.sql", "DATE")) {
        override fun encode(value: CodeBlock): CodeBlock = CodeBlock.of("%T(%L)", oracleType, value)
        override fun decode(value: CodeBlock): CodeBlock = CodeBlock.of("%L.localDateValue()", value)
    },
    TIMESTAMP(ClassName("java.time", "LocalDateTime"), ClassName("oracle.sql", "TIMESTAMP")) {
        override fun encode(value: CodeBlock): CodeBlock = CodeBlock.of("%T(%L)", oracleType, value)
        override fun decode(value: CodeBlock): CodeBlock = CodeBlock.of("%L.localDateTimeValue()", value)
    },
    TIMESTAMP_TIMEZONE(ClassName("java.time", "Instant"), ClassName("oracle.sql", "TIMESTAMPTZ")) {
        override fun encode(value: CodeBlock): CodeBlock = CodeBlock.of("%T(%L)", oracleType, value)
        override fun decode(value: CodeBlock): CodeBlock = CodeBlock.of("%L.toZonedDateTime().toInstant()", value)
    },
    ;

    override fun prepareStatementBinder(columnIndex: CodeBlock, value: CodeBlock): CodeBlock {
        return when (this) {
            SMALL_INT, INTEGER, BIG_INT, BOOL -> CodeBlock.of("bindLong(%L, %L)\n", columnIndex, value)
            NUMBER, DATE, TIMESTAMP, TIMESTAMP_TIMEZONE -> CodeBlock.of(
                "bindObject(%L, %L)\n",
                columnIndex,
                value
            )
        }
    }

    override fun cursorGetter(columnIndex: Int, cursorName: String): CodeBlock = when (this) {
        SMALL_INT, INTEGER, BIG_INT, BOOL -> CodeBlock.of("$cursorName.getLong($columnIndex)")
        NUMBER, DATE, TIMESTAMP, TIMESTAMP_TIMEZONE -> CodeBlock.of(
            "$cursorName.getObject<%T>($columnIndex)",
            oracleType
        )
    }
}
