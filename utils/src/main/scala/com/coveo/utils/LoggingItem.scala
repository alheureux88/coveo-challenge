package com.coveo.utils

import scala.collection.immutable.ListMap

class LoggingItem(elements: ListMap[String, Any]) {
  override def toString: String = {
    elements.map{
      case (key, value) =>
        s"$key=$value"
    }.mkString(" ")
  }
}

object LoggingItem{
  def apply(requestID: Option[String], method: String, uri: String, queryString: String, statusCode: Int, responseTime: Long): LoggingItem =
    new LoggingItem(
      ListMap("RequestID" -> requestID,
        "Method" -> method,
        "uri" -> uri,
        "queryString" -> queryString,
        "status code" -> statusCode,
        "ResponseTime" -> responseTime)
    )
}

