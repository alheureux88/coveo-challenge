package com.coveo.utils

import java.util.concurrent.TimeUnit
import javax.inject.Inject

import akka.stream.Materializer
import com.codahale.metrics.{JmxReporter, Slf4jReporter}
import nl.grons.metrics.scala.DefaultInstrumented
import play.api.Logger
import play.api.mvc.{Filter, RequestHeader, Result}
import LoggerUtils._

import scala.concurrent.{ExecutionContext, Future}

class LoggingFilter @Inject()(implicit val mat: Materializer, ec: ExecutionContext) extends Filter with DefaultInstrumented {

  Slf4jReporter
    .forRegistry(metricRegistry)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .convertRatesTo(TimeUnit.SECONDS)
    .outputTo(Logger.logger)
    .build()
    .start(1, TimeUnit.MINUTES)

  JmxReporter
    .forRegistry(metricRegistry)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .convertRatesTo(TimeUnit.SECONDS)
    .build()
    .start()

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val timerCtx = metricRegistry.timer(s"${requestHeader.method}-${requestHeader.uri}").time
    nextFilter(requestHeader).map { result =>
      val requestTime = TimeUnit.NANOSECONDS.toMillis(timerCtx.stop())
      val fullUri = s"${requestHeader.method}-${requestHeader.uri}"

      markStatusCode(fullUri, result.header.status)

      Logger.info(
        LoggingItem(RequestID.fromRequestHeader(requestHeader),
          requestHeader.method, requestHeader.uri,
          requestHeader.rawQueryString,
          result.header.status,
          requestTime)
      )

      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }

  private def markStatusCode(fullUri: String, statusCode: Int): Unit = {
    val meterName = fullUri + "-" + (
      if (statusCode >= 500) {"5XX"}
      else if (statusCode >= 400) {"4XX"}
      else if (statusCode >= 300) {"3XX"}
      else if (statusCode >= 200) {"2XX"}
      else if (statusCode >= 100) {"1XX"}
      )

    metricRegistry.meter(meterName).mark()
  }
}
