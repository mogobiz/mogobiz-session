/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.session.es

import akka.actor.ActorSystem
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import com.mogobiz.es.EsClient
import com.mogobiz.session.config.Settings
import com.mogobiz.utils.HttpRequestor
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.index.admin.DeleteIndexResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object Mapping {

  val mappinNames = List("ESSession")

  def clear: DeleteIndexResponse =
    Await.result(EsClient().execute(deleteIndex(Settings.Session.EsIndex)),
                 Duration.Inf)

  def set() {
    def route(url: String) =
      "http://" + com.mogobiz.es.Settings.ElasticSearch.FullUrl + url

    def mappingFor(name: String) = {
      // new File(this.getClass.getClassLoader.getResource(s"es/session/mappings/$name.json").toURI)
      getClass.getResourceAsStream(s"/es/session/mappings/$name.json")
    }

    implicit val system: ActorSystem = akka.actor.ActorSystem("mogopay-boot")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    mappinNames foreach { name =>
      val url = s"/${Settings.Session.EsIndex}/$name/_mapping"
      val mapping = scala.io.Source.fromInputStream(mappingFor(name)).mkString
      val request = HttpRequest(
        method = HttpMethods.POST,
        uri = Uri(route(url)),
        entity = HttpEntity(MediaTypes.`application/json`, mapping)
      )
      val x: Future[Any] = HttpRequestor.doRequest(request) map {
        response: HttpResponse =>
          response.status match {
            case StatusCodes.OK =>
              System.err.println(
                s"The mapping for `$name` was successfully set.")
            case _ =>
              Unmarshal(response.entity).to[String].map { data =>
                System.err.println(
                  s"Error while setting the mapping for `$name`: $data")
              }
          }
      }
      Await.result(x, 10 seconds)
    }
    system.terminate()
  }
}
