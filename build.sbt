name := """Crawler"""

version := "1.0"

scalaVersion := "2.11.1"

// Change this to another test framework if you prefer
libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "2.1.6" % "test",
    "org.jsoup" % "jsoup" % "1.7.3",
    "org.scala-lang.modules" %% "scala-async" % "0.9.2",
    "mysql" % "mysql-connector-java" % "5.1.24"
)

// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.3"

