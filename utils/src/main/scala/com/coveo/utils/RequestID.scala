package com.coveo.utils

import play.api.mvc.RequestHeader

object RequestID {
  val requestIdHeader = "X-REQUEST-ID"

  def fromRequestHeader(requestHeader: RequestHeader): Option[String] =
    requestHeader.headers.get(requestIdHeader)
}
