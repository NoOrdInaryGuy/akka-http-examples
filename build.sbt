name := "http-akka"
 
version := "1.0"
 
scalaVersion := "2.11.5"
 
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.10",
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0-RC2",
  "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0-RC2",
  "com.typesafe.akka" %% "akka-http-scala-experimental" % "1.0-RC2",
  "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23",
  "com.typesafe.play" %% "play-json" % "2.4.0-M2",
  "ch.qos.logback" % "logback-classic" % "1.1.2"
)
 
resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
 
resolvers += "Typesafe" at "https://repo.typesafe.com/typesafe/releases/"
 
//mainClass in (Compile, run) := Some("io.neilord.BootWithFlow")
