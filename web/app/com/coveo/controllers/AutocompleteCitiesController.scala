package com.coveo.controllers

import javax.inject.Inject

import com.coveo.models.{Location, SuggestionWrapper}
import io.swagger.annotations.{Api, ApiOperation, ApiParam}
import nl.grons.metrics.scala.DefaultInstrumented
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

@Api
class AutocompleteCitiesController @Inject()(cc: ControllerComponents,
                                             service: CityAutocompletionService)
                                            (implicit ec: ExecutionContext)
  extends AbstractController(cc) with DefaultInstrumented {

  @ApiOperation(value = "Find list of cities",
    notes = "Return up to X cities",
    responseContainer = "List",
    response = classOf[SuggestionWrapper])
  def suggestions(
                   @ApiParam(value = "City to autocomplete.", required = true) query: String,
                   @ApiParam(value = "The latitude close to the user.") latitude: Option[Double],
                   @ApiParam(value = "The longitude close to the user.") longitude: Option[Double]): EssentialAction = Action.async {

    val possibleLocation = for{
      lat <- latitude
      lon <- longitude
    } yield Location(lat, lon)
    service.execute(query, possibleLocation)
      .map(SuggestionWrapper.apply)
      .map(listResp => Json.toJson(listResp))
      .map(json => Ok(json))
  }
}
