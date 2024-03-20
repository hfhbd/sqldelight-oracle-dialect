package app.softwork.sqldelight.oracledialect

import app.cash.sqldelight.dialect.api.*
import com.squareup.kotlinpoet.*

internal enum class OracleType(override val javaType: TypeName) : DialectType {
    SMALL_INT(SHORT),
    INTEGER(INT),
    BIG_INT(LONG),
    NUMBER(ClassName("java.math", "BigDecimal")),
    BOOL(BOOLEAN) {
        override fun decode(value: CodeBlock) = CodeBlock.of("%L == 1L", value)

        override fun encode(value: CodeBlock) = CodeBlock.of("if (%L) 1L else 0L", value)
    },
    DATE(ClassName("java.time", "LocalDateTime")),
    TIMESTAMP(ClassName("java.time", "LocalDateTime")),
    TIMESTAMP_TIMEZONE(ClassName("java.time", "ZonedDateTime")),
    ;

    override fun prepareStatementBinder(columnIndex: CodeBlock, value: CodeBlock): CodeBlock = when (this) {
        SMALL_INT -> CodeBlock.of("bindShort(%L, %L)\n", columnIndex, value)
        INTEGER -> CodeBlock.of("bindInt(%L, %L)\n", columnIndex, value)
        BIG_INT, BOOL -> CodeBlock.of("bindLong(%L, %L)\n", columnIndex, value)
        NUMBER -> CodeBlock.of("bindBigDecimal(%L, %L)\n", columnIndex, value)
        DATE -> CodeBlock.of(
            "bindObject(%L, %L, %M)\n",
            columnIndex,
            value,
            MemberName(ClassName("java.sql", "Types"), "DATE")
        )

        TIMESTAMP -> CodeBlock.of(
            "bindObject(%L, %M)\n",
            columnIndex,
            value,
            MemberName(ClassName("java.sql", "Types"), "TIMESTAMP")
        )

        TIMESTAMP_TIMEZONE -> CodeBlock.of(
            "bindObject(%L, %L, %M)\n",
            columnIndex,
            value,
            MemberName(ClassName("java.sql", "Types"), "TIMESTAMP_WITH_TIMEZONE")
        )
    }

    override fun cursorGetter(columnIndex: Int, cursorName: String): CodeBlock = when (this) {
        SMALL_INT -> CodeBlock.of("$cursorName.getShort($columnIndex)")
        INTEGER -> CodeBlock.of("$cursorName.getInt($columnIndex)")
        BIG_INT, BOOL -> CodeBlock.of("$cursorName.getLong($columnIndex)")
        NUMBER -> CodeBlock.of("$cursorName.getBigDecimal($columnIndex)")
        DATE, TIMESTAMP, TIMESTAMP_TIMEZONE -> CodeBlock.of(
            "$cursorName.getObject<%T>($columnIndex)",
            javaType,
        )
    }
}
