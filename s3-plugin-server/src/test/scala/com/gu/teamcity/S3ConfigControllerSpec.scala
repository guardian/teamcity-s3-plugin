package com.gu.teamcity

import org.scalatest._

class S3ConfigControllerSpec extends FlatSpec with Matchers {

  "emptyAsNone" should "coerce null to None" in {
    S3ConfigController.emptyAsNone(null) should be (None)
  }

  "emptyAsNone" should "treat empty string to None" in {
    S3ConfigController.emptyAsNone("") should be (None)
  }
  "emptyAsNone" should "wrap populated string in Some" in {
    S3ConfigController.emptyAsNone("x") should be (Some("x"))
  }
}