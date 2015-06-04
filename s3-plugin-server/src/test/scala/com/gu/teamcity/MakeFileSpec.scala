package com.gu.teamcity

import java.io.ByteArrayInputStream
import java.nio.file.{Files, Paths}

import org.scalatest._

class MakeFileSpec extends FlatSpec with Matchers {

  "file maker" should "turn an input stream into a file" in {
    val testData = Array.fill(scala.util.Random.nextInt(4096))((scala.util.Random.nextInt(256) - 128).toByte)
    val stream = new ByteArrayInputStream(testData)
    val result = MakeFile(stream)
    Files.readAllBytes(Paths.get(result.toURI)) should be(testData)
  }

}
