package com.coveo.controllers

import javax.inject.Inject

import com.coveo.models.Response
import io.swagger.annotations.{Api, ApiOperation, ApiParam}
import nl.grons.metrics.scala.DefaultInstrumented
import play.api.libs.json.Json
import play.api.mvc._

@Api
class AutocompleteCities @Inject()(cc: ControllerComponents) extends AbstractController(cc) with DefaultInstrumented {
  @ApiOperation(value = "Find list of cities",
    notes = "Return up to 5 cities",
    responseContainer = "List",
    response = classOf[Response])
  def complete(
                @ApiParam(value = "City to autocomplete.", required = true) query: String,
                @ApiParam(value = "The latitude close to the user.") latitude: Option[Double],
                @ApiParam(value = "The longitude close to the user.") longitude: Option[Double]): EssentialAction = Action {
    val response = Json.toJson(List(Response("test", 1, 2, 3)))
    Ok(response)
  }
}
