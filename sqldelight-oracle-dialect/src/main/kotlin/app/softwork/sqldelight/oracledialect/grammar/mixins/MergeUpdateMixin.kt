package app.softwork.sqldelight.oracledialect.grammar.mixins

import com.alecstrong.sql.psi.core.psi.QueryElement
import com.alecstrong.sql.psi.core.psi.SqlCompositeElementImpl
import com.alecstrong.sql.psi.core.psi.SqlTableAlias
import com.alecstrong.sql.psi.core.psi.SqlTableName
import com.alecstrong.sql.psi.core.psi.SqlTableOrSubquery
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

internal abstract class MergeUpdateMixin(node: ASTNode) : SqlCompositeElementImpl(node) {
    private val table = PsiTreeUtil.getChildOfType(parent, SqlTableName::class.java)!!
    private val tableAlias: SqlTableAlias? = PsiTreeUtil.getChildOfType(parent, SqlTableAlias::class.java)
    private val using: SqlTableOrSubquery = PsiTreeUtil.getChildOfType(parent, SqlTableOrSubquery::class.java)!!

    override fun queryAvailable(child: PsiElement): Collection<QueryElement.QueryResult> {
        val table = tableAvailable(child, table.name)
        val tableAlias = if (tableAlias != null) {
            table.map {
                it.copy(table = tableAlias)
            }
        } else table

        return tableAlias + using.queryExposed()
    }
}
