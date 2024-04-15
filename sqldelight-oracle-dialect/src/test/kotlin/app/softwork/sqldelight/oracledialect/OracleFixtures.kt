package app.softwork.sqldelight.oracledialect

import com.alecstrong.sql.psi.test.fixtures.loadFolderFromResources
import java.io.File

object OracleFixtures {
    val fixtures_oracle by loadFolderFromResources(File("build"))
}
