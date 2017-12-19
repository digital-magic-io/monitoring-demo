package io.digitalmagic

import com.typesafe.config.ConfigFactory

object Config {

  def load(): Config = {
    val config = ConfigFactory.load()
    val httpConfig = config.getConfig("http")
    val influxConfig = config.getConfig("influx")
    apply(httpConfig.getString("host"), httpConfig.getInt("port"), influxConfig.getString("url"), influxConfig.getString("database"))
  }

}

case class Config(httpHost: String, httpPort: Int, influxUrl: String, influxDB: String)
