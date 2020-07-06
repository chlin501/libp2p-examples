scalaVersion := "2.13.2"

resolvers ++= Seq(Resolver.jcenterRepo)

libraryDependencies ++= Seq(
  "io.libp2p" % "jvm-libp2p-minimal" % "0.5.0-RELEASE",
  "org.slf4j" % "slf4j-api" % "1.7.30",
  "org.slf4j" % "slf4j-simple" % "1.7.30"
)
