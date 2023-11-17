package app.softwork.sqldelight.oracledialect

import app.cash.sqldelight.dialect.api.*
import com.squareup.kotlinpoet.*

internal enum class OracleType(override val javaType: TypeName) : DialectType {
    SMALL_INT(SHORT) {
        override fun decode(value: CodeBlock) = CodeBlock.of("%L.toShort()", value)

        override fun encode(value: CodeBlock) = CodeBlock.of("%L.toLong()", value)
    },
    INTEGER(INT) {
        override fun decode(value: CodeBlock) = CodeBlock.of("%L.toInt()", value)

        override fun encode(value: CodeBlock) = CodeBlock.of("%L.toLong()", value)
    },
    BIG_INT(LONG),
    NUMBER(ClassName("java.math", "BigDecimal")),
    BOOL(BOOLEAN) {
        override fun decode(value: CodeBlock) = CodeBlock.of("%L == 1L", value)

        override fun encode(value: CodeBlock) = CodeBlock.of("if (%L) 1L else 0L", value)
    },
    DATE(ClassName("java.time", "LocalDate")),
    TIME(ClassName("java.time", "LocalTime")),
    TIMESTAMP(ClassName("java.time", "LocalDateTime")),
    TIMESTAMP_TIMEZONE(ClassName("java.time", "Instant")),
    ;

    override fun prepareStatementBinder(columnIndex: CodeBlock, value: CodeBlock): CodeBlock {
        return CodeBlock.builder()
            .add(
                when (this) {
                    SMALL_INT, INTEGER, BIG_INT, BOOL -> "bindLong"
                    NUMBER, DATE, TIME, TIMESTAMP, TIMESTAMP_TIMEZONE -> "bindObject"
                }
            )
            .add("(%L, %L)\n", columnIndex, value)
            .build()
    }

    override fun cursorGetter(columnIndex: Int, cursorName: String): CodeBlock = when (this) {
        SMALL_INT, INTEGER, BIG_INT, BOOL -> CodeBlock.of("$cursorName.getLong($columnIndex)")
        NUMBER, DATE, TIME, TIMESTAMP, TIMESTAMP_TIMEZONE -> CodeBlock.of(
            "$cursorName.getObject<%T>($columnIndex)",
            javaType
        )
    }
}
