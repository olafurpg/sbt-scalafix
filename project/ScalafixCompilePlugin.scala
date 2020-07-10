import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

object ScalafixCompilePlugin extends sbt.AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins =
    JvmPlugin && scalafix.sbt.ScalafixOnCompilePlugin
}
