package scala.integrationtest

import org.scalatest._
import org.scalatest.concurrent._
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.WsScalaTestClient
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.libs.ws.WSClient
import play.api.test.Injecting
import play.mvc.Http

class WSResponse extends FlatSpec with Matchers with GuiceOneServerPerSuite with Injecting with ScalaFutures with WsScalaTestClient {
  implicit val defaultPatience =
    PatienceConfig(timeout = scaled(Span(5, Seconds)), interval = scaled(Span(15, Millis)))

  implicit val wsClient = app.injector.instanceOf[WSClient]

  "Application" should "respond to the /suggestions URL" in {
    val call = com.coveo.controllers.routes.AutocompleteCitiesController.suggestions("montreal", None, None)
    val futureResponse = wsCall(call)
      .get()

    val response = futureResponse.futureValue
    response.status should be(Http.Status.OK)
  }

}
