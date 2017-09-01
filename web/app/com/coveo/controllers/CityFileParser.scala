package com.coveo.controllers

import java.io._
import java.util.zip.ZipFile
import javax.inject.Inject

import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import akka.util.ByteString
import com.coveo.models._
import play.api.{Configuration, Logger}
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.collection.JavaConverters._
import scala.concurrent.{Await, ExecutionContext, Future}

class CityFileParser @Inject()(ws: WSClient, config: Configuration)(implicit mat: Materializer, ec: ExecutionContext) {
  private val cityToLoad: CityType = {
    config.getOptional[String]("cityfile.type") match {
      case Some("City1000") => City1000
      case Some("City5000") => City5000
      case Some("City15000") => City15000
      case _ => City1000
    }
  }

  private val adminCode = config.get[String]("cityfile.adminCode")

  private val baseUrl: String = config.get[String]("cityfile.baseUrl")
  //On docker image, looks like java.io.tmpdir is not set
  private val tempFolder: String = Option(System.getProperty("java.io.tmpdir"))
    .filter(folder => folder.nonEmpty && folder != "/")
    .getOrElse(config.get[String]("cityfile.tempFolder")) + "/"


  Logger.info(s"Temp directory is set to $tempFolder")

  private val adminCodeLineMapping: Array[String] => (String, String) = array => array(0) -> array(1)

  implicit private val adminCodeMap: Map[String, String] = {
    val futureFile = download(adminCode, "")
    Await.result(parse(futureFile.map(file => new FileInputStream(file)), adminCodeLineMapping).map(_.toMap), 5 seconds)
  }

  def parse(implicit mat: Materializer, ec: ExecutionContext): Future[Seq[(String, Suggestion)]] = {
    val futureFile = download(cityToLoad.fileName, ".zip")
    val futureInputStream = futureFile.map{ file =>
      val rootZip = new ZipFile(file)
      rootZip.getInputStream(
        rootZip
          .entries()
          .asScala
          .filterNot(_.getName.toLowerCase contains "readme")
          .next())
    }
    parse[(String, Suggestion)](futureInputStream, Suggestion.nameToSuggestion)
  }

  private def parse[A](futureInputStream: Future[InputStream],
                       lineConstruction: Array[String] => A)
                      (implicit ec: ExecutionContext): Future[Seq[A]] = {

    futureInputStream.map { inputStream =>
      val entries = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"))

      Iterator.continually(entries.readLine())
        .takeWhile(_ != null)
        .map(_.split("\t"))
        .map(values => lineConstruction(values)).toSeq
    }
  }

  private def download(filename: String, extension: String): Future[File] = {
    val file = new File(tempFolder + filename + extension)
    if (!file.exists()) {
      Logger.info(s"File $filename not found in temp directory, downloading.")
      val futureResponse: Future[WSResponse] = ws.url(baseUrl + filename + extension)
        .withFollowRedirects(true)
        .withMethod("GET")
        .stream()

      futureResponse.flatMap {
        res =>
          val outputStream = java.nio.file.Files.newOutputStream(file.toPath)

          // The sink that writes to the output stream
          val sink = Sink.foreach[ByteString] { bytes =>
            outputStream.write(bytes.toArray)
          }

          // materialize and run the stream
          res.bodyAsSource.runWith(sink).andThen {
            case result =>
              // Close the output stream whether there was an error or not
              outputStream.close()
              // Get the result or rethrow the error
              result.get
          }.map(_ => file)
      }
    } else {
      Logger.info(s"File $filename found in temp directory, using cached file.")
      Future.successful(file)
    }
  }
}
