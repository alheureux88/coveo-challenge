package com.coveo.models

import io.swagger.annotations.ApiModelProperty
import play.api.libs.json.{Format, Json}

case class Response(
                     @ApiModelProperty(value = "Possible city name.") name: String,
                     @ApiModelProperty(value = "Latitude of the city.") latitude: BigDecimal,
                     @ApiModelProperty(value = "Longitude of the city.") longitude: BigDecimal,
                     @ApiModelProperty(value ="""Score that can be used for ranking.Mix of the fuzzy search distance and the distance from the user.""")
                     score: Int)

object Response {
  implicit val responseFormatter: Format[Response] = Json.format[Response]
}