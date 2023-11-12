package app.softwork.sqldelight.oracledialect

import app.cash.sqldelight.dialect.api.*
import com.squareup.kotlinpoet.*

internal enum class OracleType(override val javaType: TypeName) : DialectType {
    TINY_INT(BYTE) {
        override fun decode(value: CodeBlock) = CodeBlock.of("%L.toByte()", value)

        override fun encode(value: CodeBlock) = CodeBlock.of("%L.toLong()", value)
    },
    SMALL_INT(SHORT) {
        override fun decode(value: CodeBlock) = CodeBlock.of("%L.toShort()", value)

        override fun encode(value: CodeBlock) = CodeBlock.of("%L.toLong()", value)
    },
    INTEGER(INT) {
        override fun decode(value: CodeBlock) = CodeBlock.of("%L.toInt()", value)

        override fun encode(value: CodeBlock) = CodeBlock.of("%L.toLong()", value)
    },
    BIG_INT(LONG),
    BOOL(BOOLEAN) {
        override fun decode(value: CodeBlock) = CodeBlock.of("%L == 1L", value)

        override fun encode(value: CodeBlock) = CodeBlock.of("if (%L) 1L else 0L", value)
    },
    DATE(ClassName("kotlinx.datetime", "LocalDate")) {
        override fun decode(value: CodeBlock) =
            CodeBlock.of("%L.%M()", value, MemberName("kotlinx.datetime", "toJavaLocalDate", isExtension = true))

        override fun encode(value: CodeBlock) = CodeBlock.of(
            "(%L as java.time.LocalDate?)?.%M()", value,
            MemberName("kotlinx.datetime", "toKotlinLocalDate", isExtension = true)
        )
    },
    TIME(ClassName("kotlinx.datetime", "LocalTime")),
    TIMESTAMP(ClassName("kotlinx.datetime", "LocalDateTime")),
    TIMESTAMP_TIMEZONE(ClassName("kotlinx.datetime", "Instant")),
    ;

    override fun prepareStatementBinder(columnIndex: CodeBlock, value: CodeBlock): CodeBlock {
        return CodeBlock.builder()
            .add(
                when (this) {
                    TINY_INT, SMALL_INT, INTEGER, BIG_INT, BOOL -> "bindLong"
                    DATE, TIME, TIMESTAMP, TIMESTAMP_TIMEZONE -> "bindObject"
                }
            )
            .add("(%L, %L)\n", columnIndex, value)
            .build()
    }

    override fun cursorGetter(columnIndex: Int, cursorName: String): CodeBlock {
        return CodeBlock.of(
            when (this) {
                TINY_INT, SMALL_INT, INTEGER, BIG_INT, BOOL -> "$cursorName.getLong($columnIndex)"
                DATE, TIME, TIMESTAMP, TIMESTAMP_TIMEZONE -> "$cursorName.getObject<%T>($columnIndex)"
            }
        )
    }
}
