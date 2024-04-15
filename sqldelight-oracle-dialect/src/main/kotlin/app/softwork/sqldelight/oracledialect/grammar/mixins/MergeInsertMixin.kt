package app.softwork.sqldelight.oracledialect.grammar.mixins

import app.softwork.sqldelight.oracledialect.grammar.psi.OracleMerge
import com.alecstrong.sql.psi.core.psi.QueryElement
import com.alecstrong.sql.psi.core.psi.SqlTableAlias
import com.alecstrong.sql.psi.core.psi.SqlTableName
import com.alecstrong.sql.psi.core.psi.SqlTableOrSubquery
import com.alecstrong.sql.psi.core.psi.impl.SqlInsertStmtImpl
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

internal abstract class MergeInsertMixin(node: ASTNode): SqlInsertStmtImpl(node) {
    override fun getTableName(): SqlTableName =
        PsiTreeUtil.getChildOfType(parent as OracleMerge, SqlTableName::class.java)!!

    override fun getTableAlias(): SqlTableAlias? {
        return PsiTreeUtil.getChildOfType(parent as OracleMerge, SqlTableAlias::class.java)
    }

    private val using: SqlTableOrSubquery = PsiTreeUtil.getChildOfType(parent as OracleMerge, SqlTableOrSubquery::class.java)!!

    override fun queryAvailable(child: PsiElement): Collection<QueryElement.QueryResult> {
        val table = tableAvailable(child, tableName.name)
        val tableAlias = if (tableAlias != null) {
            table.map {
                it.copy(table = tableAlias)
            }
        } else table

        return tableAlias + using.queryExposed()
    }
}
