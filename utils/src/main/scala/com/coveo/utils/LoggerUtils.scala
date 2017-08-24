package com.coveo.utils

import play.api.MarkerContext

object LoggerUtils {

  implicit class PimpedLogger(val logger: play.api.Logger) extends AnyVal {
    def trace(item: LoggingItem)(implicit mc: MarkerContext): Unit = logger.trace(s"${item.toString}")
    def debug(item: LoggingItem)(implicit mc: MarkerContext): Unit = logger.debug(s"${item.toString}")
    def info(item: LoggingItem)(implicit mc: MarkerContext): Unit = logger.info(s"${item.toString}")
    def warn(item: LoggingItem)(implicit mc: MarkerContext): Unit = logger.warn(s"${item.toString}")
    def error(item: LoggingItem)(implicit mc: MarkerContext): Unit = logger.error(s"${item.toString}")

    def trace(item: LoggingItem, message: => String)(implicit mc: MarkerContext): Unit = logger.trace(s"${item.toString} - $message")
    def debug(item: LoggingItem, message: => String)(implicit mc: MarkerContext): Unit = logger.debug(s"${item.toString} - $message")
    def info(item: LoggingItem, message: => String)(implicit mc: MarkerContext): Unit = logger.info(s"${item.toString} - $message")
    def warn(item: LoggingItem, message: => String)(implicit mc: MarkerContext): Unit = logger.warn(s"${item.toString} - $message")
    def error(item: LoggingItem, message: => String)(implicit mc: MarkerContext): Unit = logger.info(s"${item.toString} - $message")
  }

}
