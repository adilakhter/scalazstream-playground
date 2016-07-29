import scalariform.formatter.preferences._

name := "scalazstream-playground"

organization  := "org.xiaon"

version := "1.0"

scalaVersion := "2.11.8"

val akkaVersion = "2.4.3"
val sprayVersion = "1.3.3"
val sparkVersion = "1.6.1"

libraryDependencies ++= Seq(
    "org.scalaz.stream"         %% "scalaz-stream"                  % "0.8.2", 
    "org.scalatest"             %% "scalatest"                      % "2.2.5"        % "test",
    "org.scalacheck"            %% "scalacheck"                     % "1.13.0"
)


libraryDependencies += "com.lihaoyi" %% "ammonite" % "0.7.0" % "test" cross CrossVersion.full


scalacOptions := Seq("-encoding", "utf8",
                     "-target:jvm-1.8",
                     "-feature",
                     "-language:implicitConversions",
                     "-language:postfixOps",
                     "-unchecked",
                     "-Xfatal-warnings",
                     "-Xlint",
                     "-deprecation",
                     "-Xlog-reflective-calls",
                     "-Ywarn-unused",
                     "-Ywarn-unused-import",
                     "-Ywarn-dead-code")
scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignParameters, false)
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 90)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(PreserveDanglingCloseParenthesis, true)
  .setPreference(RewriteArrowSymbols, true)

scalacOptions in (Compile, console) ~= (_ filterNot (_ == "-Ywarn-unused-import"))
scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value

initialCommands in console := "import scalaz._, Scalaz._"
initialCommands in (Test, console) := """ammonite.Main().run()"""

tutSettings



