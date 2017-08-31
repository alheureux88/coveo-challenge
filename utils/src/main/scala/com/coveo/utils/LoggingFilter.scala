package com.coveo.utils

import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject

import akka.stream.Materializer
import com.codahale.metrics.{JmxReporter, MetricFilter, Slf4jReporter}
import nl.grons.metrics.scala.DefaultInstrumented
import play.api.Logger
import play.api.mvc.{Filter, RequestHeader, Result}
import LoggerUtils._
import com.codahale.metrics.jvm._
import play.api.inject.ApplicationLifecycle

import scala.concurrent.{ExecutionContext, Future}

class LoggingFilter @Inject()(lifecycle: ApplicationLifecycle)(implicit val mat: Materializer, ec: ExecutionContext) extends Filter with DefaultInstrumented {

  lifecycle.addStopHook { () =>
    //Some cleanup needed otherwise it won't let app refresh in dev
    Future.successful{
      sl4jReporter.stop()
      jmxReporter.stop()
      metricRegistry.removeMatching(MetricFilter.ALL)
    }
  }
  metricRegistry.registerAll(new MemoryUsageGaugeSet())
  metricRegistry.registerAll(new GarbageCollectorMetricSet())
  metricRegistry.registerAll(new ThreadStatesGaugeSet())
  metricRegistry.register("File-Descriptor", new FileDescriptorRatioGauge())
  metricRegistry.registerAll(new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer))

  //Normally the ouput should not be log file but a metric application
  private val sl4jReporter = Slf4jReporter
    .forRegistry(metricRegistry)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .convertRatesTo(TimeUnit.SECONDS)
    .outputTo(Logger.logger)
    .build()

  sl4jReporter.start(1, TimeUnit.MINUTES)

  private val jmxReporter = JmxReporter
    .forRegistry(metricRegistry)
    .convertDurationsTo(TimeUnit.MILLISECONDS)
    .convertRatesTo(TimeUnit.SECONDS)
    .build()
  jmxReporter.start()

  def apply(nextFilter: RequestHeader => Future[Result])
           (requestHeader: RequestHeader): Future[Result] = {

    val fullUri = s"${requestHeader.method}-${requestHeader.path}"
    val timerCtx = metricRegistry.timer(fullUri).time
    nextFilter(requestHeader).map { result =>
      val requestTime = TimeUnit.NANOSECONDS.toMillis(timerCtx.stop())
      markStatusCode(fullUri, result.header.status)

      Logger.info(
        LoggingItem(RequestID.fromRequestHeader(requestHeader),
          requestHeader.method, requestHeader.path,
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
