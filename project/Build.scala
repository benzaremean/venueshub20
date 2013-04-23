import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "venues_hub_20"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(javaCore, javaJdbc, javaEbean,
    // Add your project dependencies here,
    "com.google.code.morphia" % "morphia" % "0.99",
    "org.mongodb" % "mongo-java-driver" % "2.10.1",
    "com.google.code.morphia" % "morphia-logging-slf4j" % "0.99",
    "com.googlecode.json-simple" % "json-simple" % "1.1.1",
    "org.apache.httpcomponents" % "httpclient" % "4.2.3" % "compile",
    "com.google.code.geocoder-java" % "geocoder-java" % "0.12",
    "com.amazonaws" % "aws-java-sdk" % "1.3.11"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    resolvers += "Maven repository" at "http://morphia.googlecode.com/svn/mavenrepo/",
    resolvers += "MongoDb Java Driver Repository" at "http://repo1.maven.org/maven2/org/mongodb/mongo-java-driver/"
  )

}
