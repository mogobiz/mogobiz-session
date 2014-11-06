package com.mogobiz.session

import java.io.File

import com.typesafe.config.ConfigFactory

object Settings {
  private val config = ConfigFactory.load("session")

  object Session {
    val Secret = config getString "session.secret"

    val Folder = new File(config getString "session.folder")

    val EsIndex =config getString "session.esindex"

    val CookieName = config getString "session.cookie.name"

    val MaxAge = config getLong "session.maxage"

    val RememberCookieName = config getString "session.remember.cookie.name"

    val RememberCookieMaxAge = config getLong "session.remember.cookie.maxage"
  }

  require(Session.Secret.nonEmpty, "session.secret must be non-empty")
  require(Session.CookieName.nonEmpty, "session.cookie.name must be non-empty")
  require(Session.RememberCookieName.nonEmpty, "session.remember.cookie.name must be non-empty")
}
