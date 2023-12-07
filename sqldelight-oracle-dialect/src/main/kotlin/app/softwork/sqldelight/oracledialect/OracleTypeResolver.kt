package app.softwork.sqldelight.oracledialect

import app.cash.sqldelight.dialect.api.*
import app.softwork.sqldelight.oracledialect.grammar.psi.OracleTypeName
import com.alecstrong.sql.psi.core.psi.*

internal class OracleTypeResolver(private val parentResolver: TypeResolver) : TypeResolver by parentResolver {
    override fun definitionType(typeName: SqlTypeName): IntermediateType {
        check(typeName is OracleTypeName)
        with(typeName) {
            return IntermediateType(when {
                approximateNumericDataType != null -> PrimitiveType.REAL
                binaryStringDataType != null -> PrimitiveType.BLOB
                dateDataType != null -> {
                    when (dateDataType!!.firstChild.text) {
                        "DATE" -> OracleType.DATE
                        "TIME" -> OracleType.TIME
                        "TIMESTAMP" -> if (dateDataType!!.node.getChildren(null)
                                .any { it.text == "WITH" }
                        ) OracleType.TIMESTAMP_TIMEZONE else OracleType.TIMESTAMP

                        "TIMESTAMPTZ" -> OracleType.TIMESTAMP_TIMEZONE
                        else -> throw IllegalArgumentException("Unknown date type ${dateDataType!!.text}")
                    }
                }

                smallIntDataType != null -> OracleType.SMALL_INT
                intDataType != null -> OracleType.INTEGER
                bigIntDataType != null -> OracleType.BIG_INT
                fixedPointDataType != null -> OracleType.NUMBER
                characterStringDataType != null -> PrimitiveType.TEXT
                booleanDataType != null -> OracleType.BOOL
                bitStringDataType != null -> PrimitiveType.BLOB
                intervalDataType != null -> PrimitiveType.BLOB
                else -> throw IllegalArgumentException("Unknown kotlin type for sql type ${typeName.text}")
            })
        }
    }

    override fun resolvedType(expr: SqlExpr): IntermediateType = when(expr) {
        is SqlBinaryExpr -> {
            encapsulatingType(
                expr.getExprList(),
                nullableIfAny = expr is SqlBinaryAddExpr || expr is SqlBinaryMultExpr || expr is SqlBinaryPipeExpr,
                OracleType.SMALL_INT,
                OracleType.INTEGER,
                OracleType.NUMBER,
                PrimitiveType.REAL,
                PrimitiveType.TEXT,
                PrimitiveType.BLOB,
                OracleType.DATE,
                OracleType.TIME,
                OracleType.TIMESTAMP,
            )
        }
        is SqlLiteralExpr -> when (expr.literalValue.text) {
            "CURRENT_DATE" -> IntermediateType(OracleType.DATE)
            "SYSDATE" -> IntermediateType(OracleType.DATE)
            "CURRENT_TIMESTAMP" -> IntermediateType(OracleType.TIMESTAMP)
            else -> parentResolver.resolvedType(expr)
        }
        else -> parentResolver.resolvedType(expr)
    }
}
