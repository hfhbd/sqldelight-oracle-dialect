package app.softwork.sqldelight.oracledialect

import com.alecstrong.sql.psi.test.fixtures.FixturesTest
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters
import java.io.File

@RunWith(Parameterized::class)
class OracleFixturesTest(name: String, fixtureRoot: File) : FixturesTest(name, fixtureRoot) {
  override val replaceRules = arrayOf(
    "TEXT" to "VARCHAR(8)",
    "?1" to "?",
    "?2" to "?",
    "AUTOINCREMENT" to "DEFAULT SYS_GUID()",
    "GENERATED ALWAYS AS (2)" to "GENERATED ALWAYS AS IDENTITY",
    "CURRENT_TIME," to "SYSDATE,",
    "CURRENT_TIME;" to "SYSDATE;",
  )

  override fun setupDialect() {
    OracleDialect().setup()
  }

  companion object {
    @Suppress("unused")
    // Used by Parameterized JUnit runner reflectively.
    @Parameters(name = "{0}")
    @JvmStatic
    fun parameters() =  ansiFixtures + OracleFixtures.fixtures_oracle
  }
}
