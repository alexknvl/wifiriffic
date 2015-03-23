import android.Keys._
import android.Dependencies.apklib
import android.Dependencies.aar

android.Plugin.androidBuild

name := "WifiDataCollector"

scalaVersion := "2.11.4"

run <<= run in Android

proguardScala in Android := true

javacOptions ++= Seq(
  "-source", "1.7",
  "-target", "1.7",
  "-encoding", "UTF-8"
)

scalacOptions ++= Seq(
  "-target:jvm-1.7",
  "-deprecation",
  "-feature"
)

scalaSource in Compile <<= baseDirectory (_/"src")

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.0.0",
  "org.spire-math" %% "spire" % "0.8.2",
  "org.scalanlp" %% "breeze" % "0.10"
)

// Macroid
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("public"),
  "jcenter" at "http://jcenter.bintray.com"
)

libraryDependencies ++= Seq(
  aar("org.macroid" %% "macroid" % "2.0.0-M3"),
  aar("org.macroid" %% "macroid-akka-fragments" % "2.0.0-M3"),
  "com.android.support" % "support-v4" % "20.0.0",
  "com.typesafe.akka" %% "akka-actor" % "2.3.6"
)

// Macroid linter
addCompilerPlugin("org.brianmckenna" %% "wartremover" % "0.11")

scalacOptions in (Compile, compile) ++=
  (dependencyClasspath in Compile).value.files.map("-P:wartremover:cp:" + _.toURI.toURL)

scalacOptions in (Compile, compile) ++= Seq(
  "-P:wartremover:traverser:macroid.warts.CheckUi"
)

// Generic ProGuard rules
proguardOptions in Android ++= Seq(
  "-ignorewarnings",
  "-keep class scala.Dynamic"
)

// ProGuard rules for Akka
proguardOptions in Android ++= Seq(
  "-keep class akka.actor.Actor$class { *; }",
  "-keep class akka.actor.LightArrayRevolverScheduler { *; }",
  "-keep class akka.actor.LocalActorRefProvider { *; }",
  "-keep class akka.actor.CreatorFunctionConsumer { *; }",
  "-keep class akka.actor.TypedCreatorFunctionConsumer { *; }",
  "-keep class akka.dispatch.BoundedDequeBasedMessageQueueSemantics { *; }",
  "-keep class akka.dispatch.UnboundedMessageQueueSemantics { *; }",
  "-keep class akka.dispatch.UnboundedDequeBasedMessageQueueSemantics { *; }",
  "-keep class akka.dispatch.DequeBasedMessageQueueSemantics { *; }",
  "-keep class akka.dispatch.MultipleConsumerSemantics { *; }",
  "-keep class akka.actor.LocalActorRefProvider$Guardian { *; }",
  "-keep class akka.actor.LocalActorRefProvider$SystemGuardian { *; }",
  "-keep class akka.dispatch.UnboundedMailbox { *; }",
  "-keep class akka.actor.DefaultSupervisorStrategy { *; }",
  "-keep class macroid.akkafragments.AkkaAndroidLogger { *; }",
  "-keep class akka.event.Logging$LogExt { *; }",
  "-keep class macroid.FullDsl$ { *; }"
)
