package edu.wpi.first.toolchain.roborio

import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import org.junit.Rule
import spock.lang.Shared
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.IgnoreIf

@IgnoreIf({ !Boolean.valueOf(env['SPOCK_RUN_TOOLCHAINS']) })
class RoboRioDownloadTest extends Specification {
  @Rule TemporaryFolder testProjectDir = new TemporaryFolder()
  File buildFile
  @Shared File toolchainDir

  def setup() {
    buildFile = testProjectDir.newFile('build.gradle')
  }

  def setupSpec() {
    RoboRioToolchainExtension ext = new RoboRioToolchainExtension()
    String roborioVersion = ext.toolchainVersion.split("-")[0].toLowerCase();
    toolchainDir = RoboRioToolchainPlugin.toolchainInstallLoc(roborioVersion)
    def result = toolchainDir.deleteDir()  // Returns true if all goes well, false otherwise.
    assert result
  }

  def "Toolchain Can Download"() {
    given:
    buildFile << """plugins {
  id 'cpp'
  id 'edu.wpi.first.Toolchain'
}

toolchainsPlugin.withRoboRIO()
"""
    when:
    def result = GradleRunner.create()
                             .withProjectDir(testProjectDir.root)
                             .withArguments('installRoboRioToolchain', '--stacktrace')
                             .withPluginClasspath()
                             .build()

    then:
    result.task(':installRoboRioToolchain').outcome == SUCCESS
  }
}
