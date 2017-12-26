/*
 * Copyright (C) 2015 Mogobiz SARL. All rights reserved.
 */

package com.mogobiz.session

import java.util.UUID

import akka.http.scaladsl.model.DateTime
import com.mogobiz.session.config.Settings

import scala.collection.mutable.Map

case class Session(data: Session.Data = Map(
                     (Settings.Session.CookieName, UUID.randomUUID.toString)),
                   expires: Option[DateTime] = Some(
                     DateTime.now + (1000 * Settings.Session.MaxAge)),
                   maxAge: Option[Int] = Some(Settings.Session.MaxAge),
                   domain: Option[String] = None,
                   path: Option[String] = Some("/"),
                   secure: Boolean = false,
                   httpOnly: Boolean = true,
                   extension: Option[String] = None) {
  private var dirty: Boolean = false

  def clear() = {
    val theId = id
    data.clear()
    this += Settings.Session.CookieName -> theId
  }

  def isDirty = dirty

  def get(key: String): Option[Any] = data.get(key)

  def isEmpty: Boolean = data.isEmpty

  def contains(key: String): Boolean = data.contains(key)

  def -=(key: String): Session = synchronized {
    dirty = true
    data -= key
    this
  }

  def +=(kv: (String, Any)): Session = synchronized {
    dirty = true
    data += kv
    this
  }

  def apply(key: String): Any = data(key)

  val id = data(Settings.Session.CookieName).asInstanceOf[String]
}

object Session {
  type Data = Map[String, Any]
}
