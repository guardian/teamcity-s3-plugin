package com.gu.teamcity

import jetbrains.buildServer.serverSide.ServerPaths
import org.scalatest._

class S3ConfigManagerSpec extends FlatSpec with Matchers {
  "secret" should "be persisted" in {
    val configManager = new S3ConfigManager(new ServerPaths("", "", ""))

    configManager.update(S3Config(None, None, None, Some("key"), Some("secret")))

    configManager.config.flatMap(_.awsSecretKey) should be (Some("secret"))
  }

  "secret" should "stay same if key same and secret empty" in {
    val configManager = new S3ConfigManager(new ServerPaths("", "", ""))

    configManager.update(S3Config(None, None, None, Some("key"), Some("secret")))
    configManager.update(S3Config(None, None, None, Some("key"), None))

    configManager.config.flatMap(_.awsSecretKey) should be (Some("secret"))
  }

  "secret" should "stay reset if key changes and secret empty" in {
    val configManager = new S3ConfigManager(new ServerPaths("", "", ""))

    configManager.update(S3Config(None, None, None, Some("key"), Some("secret")))
    configManager.update(S3Config(None, None, None, Some("new key"), None))

    configManager.config.flatMap(_.awsSecretKey) should be (None)
  }
}
