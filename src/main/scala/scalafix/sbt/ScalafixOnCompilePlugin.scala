package scalafix.sbt

import sbt._
import sbt.Keys._
import scalafix.sbt.ScalafixPlugin.autoImport._

object ScalafixOnCompilePlugin extends AutoPlugin {
  override def trigger: PluginTrigger = allRequirements
  override def requires: Plugins = ScalafixPlugin
  override lazy val projectSettings: Seq[Def.Setting[_]] =
    Seq(Compile, Test).flatMap(c => inConfig(c)(scalafixOnCompileSettings))
  // Controls whether scalafix should depend on compile (true) & whether compile may depend on
  // scalafix (false), to avoid cyclic dependencies causing deadlocks during executions (as
  // dependencies come from dynamic tasks).
  private lazy val scalafixRunExplicitly: Def.Initialize[Task[Boolean]] =
    Def.task {
      executionRoots.value.exists { root =>
        Seq(scalafix.key, scalafixAll.key).contains(root.key)
      }
    }
  private lazy val scalafixOnCompileSettings = List(
    scalafixCaching := true,
    compile := Def.taskDyn {
      val oldCompile =
        compile.value // evaluated first, before the potential scalafix evaluation
      if (!scalafixRunExplicitly.value) scalafix.toTask("").map(_ => oldCompile)
      else Def.task(oldCompile)
    }.value
  )
}
