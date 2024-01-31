package app.softwork.sqldelight.oracledialect

import app.cash.sqldelight.dialect.api.*
import app.softwork.sqldelight.oracledialect.grammar.psi.*
import com.alecstrong.sql.psi.core.psi.*
import com.intellij.psi.*

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

    override fun argumentType(parent: PsiElement, argument: SqlExpr): IntermediateType {
        if (argument is SqlBindExpr && parent is SqlBinaryAddExpr) {
            val (right, left) = parent.getExprList()
            val rightType = resolvedType(right).dialectType
            if (rightType is OracleType) {
                when (rightType) {
                    OracleType.DATE, OracleType.TIMESTAMP, OracleType.TIMESTAMP_TIMEZONE ->
                        return IntermediateType(PrimitiveType.INTEGER).nullableIf(resolvedType(left).isNullable())

                    else -> {}
                }
            }
        }
        return parentResolver.argumentType(parent, argument)
    }

    private fun IntermediateType.isNullable(): Boolean {
        return javaType.isNullable
    }

    override fun resolvedType(expr: SqlExpr): IntermediateType = when (expr) {
        is SqlBinaryExpr -> {
            val type = encapsulatingType(
                expr.getExprList(),
                nullableIfAny = expr is SqlBinaryAddExpr || expr is SqlBinaryMultExpr || expr is SqlBinaryPipeExpr,
                OracleType.SMALL_INT,
                OracleType.INTEGER,
                OracleType.NUMBER,
                PrimitiveType.REAL,
                PrimitiveType.TEXT,
                PrimitiveType.BLOB,
                OracleType.DATE,
                OracleType.TIMESTAMP,
                OracleType.TIMESTAMP_TIMEZONE,
            )
            val resolvedType = type.dialectType
            if (resolvedType is OracleType) {
                when (resolvedType) {
                    OracleType.DATE,
                    OracleType.TIMESTAMP,
                    OracleType.TIMESTAMP_TIMEZONE -> type.nullableIf(
                        expr is SqlBinaryAddExpr && resolvedType(expr.getExprList()[1]).isNullable()
                    )

                    else -> type
                }
            } else type
        }

        is SqlLiteralExpr -> when (expr.literalValue.text) {
            "CURRENT_DATE" -> IntermediateType(OracleType.DATE)
            "CURRENT_TIMESTAMP" -> IntermediateType(OracleType.TIMESTAMP_TIMEZONE)
            "SYSDATE" -> IntermediateType(OracleType.DATE)
            "SYSTIMESTAMP" -> IntermediateType(OracleType.TIMESTAMP_TIMEZONE)
            "LOCALTIMESTAMP" -> IntermediateType(OracleType.TIMESTAMP)
            else -> parentResolver.resolvedType(expr)
        }

        else -> parentResolver.resolvedType(expr)
    }

    override fun functionType(functionExpr: SqlFunctionExpr): IntermediateType? {
        return when (functionExpr.functionName.text.lowercase()) {
            "round" -> when (functionExpr.exprList.size) {
                1 -> IntermediateType(PrimitiveType.INTEGER)
                else -> IntermediateType(OracleType.NUMBER)
            }.nullableIf(resolvedType(functionExpr.exprList[0]).javaType.isNullable)

            else -> parentResolver.functionType(functionExpr)
        }
    }
}
