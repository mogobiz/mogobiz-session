/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.session.boot

import com.mogobiz.es.EsClient
import com.mogobiz.session.config.Settings
import com.mogobiz.session.es.Mapping
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.typesafe.scalalogging.Logger
import org.elasticsearch.transport.RemoteTransportException
import org.slf4j.LoggerFactory

object DBInitializer {
  val logger = Logger(LoggerFactory.getLogger("com.mogobiz.session.boot.DBInitializer"))
  def apply(): Unit =
    try {
      EsClient().execute(createIndex(Settings.Session.EsIndex)).await
      Mapping.set()
      fillDB()
    } catch {
      case e: RemoteTransportException =>
        e.printStackTrace()
      case e: Throwable =>
        e.printStackTrace()
    }

  private def fillDB() {}
}
