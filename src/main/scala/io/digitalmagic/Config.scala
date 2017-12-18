package io.digitalmagic

import com.typesafe.config.ConfigFactory

object Config {

  def load(): Config = {
    val config = ConfigFactory.load()
    val httpConfig = config.getConfig("http")
    apply(httpConfig.getString("host"), httpConfig.getInt("port"))
  }

}

case class Config(httpHost: String, httpPort: Int)
