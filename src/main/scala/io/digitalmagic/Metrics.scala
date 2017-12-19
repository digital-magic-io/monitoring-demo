package io.digitalmagic

import java.util.concurrent.TimeUnit

import org.influxdb.dto.Point
import org.influxdb.{InfluxDB, InfluxDBFactory}

class Metrics(config: Config) {

  val FLD_VALUE = "value"

  val influxDB: InfluxDB = InfluxDBFactory.connect(config.influxUrl)
  if (!influxDB.databaseExists(config.influxDB)) {
    influxDB.createDatabase(config.influxDB)
  }
  influxDB.setDatabase(config.influxDB)
  this.influxDB.enableBatch(1000, 10, TimeUnit.SECONDS) // Use batches by 1000 elements & sent at least every 10 secs

  def createTick(measurementName: String, tags: Map[String, String]): Unit
    = createPoint(measurementName, tags, Map(FLD_VALUE -> new Integer(1)))

  def createPoint(measurementName: String, tags: Map[String, String], fields: Map[String, AnyRef]): Unit = {
    import scala.collection.JavaConverters._

    writePoint(() => Point.measurement(measurementName)
      .time(System.currentTimeMillis, TimeUnit.MILLISECONDS)
      .fields(fields.asJava)
      .tag(tags.asJava)
      .build
    )
  }

  private def writePoint(fn: () => Point): Unit = {
    if (influxDB != null) influxDB.write(fn.apply())
  }
}
