package com.mogobiz.session.boot

import com.mogobiz.es.EsClient
import com.mogobiz.session.config.Settings
import com.mogobiz.session.es.Mapping
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.indices.IndexAlreadyExistsException

object DBInitializer {
  def apply(): Unit = try {
    EsClient.client.sync.execute(create index Settings.Session.EsIndex)
    Mapping.set()
    fillDB()
  } catch {
    case e: IndexAlreadyExistsException =>
      println(s"Index ${Settings.Session.EsIndex} was not created because it already exists.")
    case e: Throwable => e.printStackTrace()
  }

  private def fillDB() {
  }
}
