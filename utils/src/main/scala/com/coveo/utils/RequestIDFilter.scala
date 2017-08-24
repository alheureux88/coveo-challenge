package com.coveo.utils

import javax.inject.Inject

import akka.stream.Materializer
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

class RequestIDFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter {


  override def apply(nextFilter: (RequestHeader) => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    val requestId = RequestID.fromRequestHeader(requestHeader) match {
      case Some(possiblyRequest) =>
        possiblyRequest
      case _ =>
        val generatedID = java.util.UUID.randomUUID.toString
        generatedID
    }

      nextFilter(requestHeader.withHeaders(requestHeader.headers.add(RequestID.requestIdHeader -> requestId))).map { result =>
        result.withHeaders(RequestID.requestIdHeader -> requestId)
    }

  }
}
