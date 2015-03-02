package com.mogobiz.session.boot

import com.mogobiz.es.EsClient
import com.mogobiz.session.config.Settings
import com.mogobiz.session.es.Mapping
import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.indices.IndexAlreadyExistsException
import org.elasticsearch.transport.RemoteTransportException

object DBInitializer {
  def apply(): Unit = try {
    EsClient().execute(create index Settings.Session.EsIndex).await
    Mapping.set()
    fillDB()
  } catch {
    case e: RemoteTransportException if e.getCause().isInstanceOf[IndexAlreadyExistsException] =>
      println(s"Index ${Settings.Session.EsIndex} was not created because it already exists.")
    case e: Throwable => e.printStackTrace()
  }

  private def fillDB() {
  }
}
