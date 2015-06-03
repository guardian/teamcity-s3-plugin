package com.gu.teamcity

import java.io.File

import org.scalatest._

class ArtifactUploaderSpec extends FlatSpec with Matchers {
  "getChildren" should "find the right stuff" in {
    val data = new File("../s3-plugin-server/TestResources")
    val result = ArtifactUploader.getChildren(data)
    // not exactly a unit test - good luck making this work in an automated build
    result.map(_._1).sorted should be(Seq("test123/test234/aFile", "test123/anotherFile").sorted)
  }

}
