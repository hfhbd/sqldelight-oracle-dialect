package app.softwork.sqldelight.oracledialect

import app.cash.sqldelight.dialect.api.*
import app.softwork.sqldelight.oracledialect.grammar.*
import com.alecstrong.sql.psi.core.*
import com.intellij.icons.*
import com.squareup.kotlinpoet.*
import javax.swing.*

public class OracleDialect : SqlDelightDialect {
    override val icon: Icon = AllIcons.Providers.Oracle
    override fun setup() {
        SqlParserUtil.reset()

        OracleParserUtil.reset()
        OracleParserUtil.overrideSqlParser()
    }

    override fun typeResolver(parentResolver: TypeResolver): TypeResolver = OracleTypeResolver(parentResolver)

    override val runtimeTypes: RuntimeTypes = RuntimeTypes(
        ClassName("app.cash.sqldelight.driver.jdbc", "JdbcCursor"),
        ClassName("app.cash.sqldelight.driver.jdbc", "JdbcPreparedStatement")
    )

    override val asyncRuntimeTypes: RuntimeTypes
        get() = throw UnsupportedOperationException("Oracle does not support an async driver")
}
