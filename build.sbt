name := "http-akka"
 
version := "1.1"
 
scalaVersion := "2.11.6"
 
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0",
  "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0",
  "com.typesafe.akka" %% "akka-http-experimental" % "1.0",
  "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23",
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23",
  "com.typesafe.play" %% "play-json" % "2.4.0-M2",
  "ch.qos.logback" % "logback-classic" % "1.1.2"
)
 
resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
 
resolvers += "Typesafe" at "https://repo.typesafe.com/typesafe/releases/"
 
//mainClass in (Compile, run) := Some("io.neilord.BootWithFlow")
