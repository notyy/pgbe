import de.johoop.jacoco4sbt._
import JacocoPlugin._

name := "pgbe"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

//code coverage
seq(jacoco.settings : _*)

libraryDependencies += "net.liftweb" %% "lift-webkit" % "2.4-M5" % "compile->default"

libraryDependencies += "net.liftweb" %% "lift-mapper" % "2.4-M5" % "compile->default"

libraryDependencies += "javax.servlet" % "servlet-api" % "2.5" % "provided->default"

libraryDependencies += "com.h2database" % "h2" % "1.3.164"

//add slf4j
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.6.4"

//add log4j
libraryDependencies += "log4j" % "log4j" % "1.2.16"

libraryDependencies += "org.slf4j" % "slf4j-log4j12" % "1.6.4"

//add http-client 
libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.1.3"

libraryDependencies += "org.apache.httpcomponents" % "httpmime" % "4.1.3"

libraryDependencies += "org.apache.httpcomponents" % "httpclient-cache" % "4.1.3"

// add a test dependency on ScalaCheck
libraryDependencies += "org.scala-tools.testing" %% "scalacheck" % "1.9" % "test"

//add test dependency on scalatest
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.6.1" % "test"
)

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

seq(webSettings :_*)

libraryDependencies += "org.mortbay.jetty" % "jetty" % "6.1.22" % "test,container"

libraryDependencies += "org.scala-tools.testing" %% "specs" % "1.6.9" % "test"