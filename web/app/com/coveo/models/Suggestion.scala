package com.coveo.models

import io.swagger.annotations.ApiModelProperty
import play.api.libs.json.{Format, Json}

case class SuggestionWrapper(suggestions: Seq[Suggestion])

object SuggestionWrapper {
  implicit val suggestionWrapperFormatter: Format[SuggestionWrapper] = Json.format[SuggestionWrapper]
}

case class Suggestion(
                       @ApiModelProperty(value = "Possible city name.") name: String,
                       @ApiModelProperty(value = "Latitude of the city.") latitude: Double,
                       @ApiModelProperty(value = "Longitude of the city.") longitude: Double,
                       @ApiModelProperty(value ="""Score that can be used for ranking.Mix of the fuzzy search distance and the distance from the user.""")
                       score: Double)

object Suggestion {
  implicit val responseFormatter: Format[Suggestion] = Json.format[Suggestion]

  val nameField = 1
  val latitudeField = 4
  val longitudeField = 5
  val countryCode = 8
  val adminDivsionCode = 10

  def nameToSuggestion(values: Array[String])(implicit adminCode: Map[String, String]): (String, Suggestion) = {
    val name = values(nameField)
    val division = adminCode.getOrElse(values(countryCode) + "." + values(adminDivsionCode), values(adminDivsionCode))
    val country = values(countryCode)
    values(nameField) ->
      Suggestion(
        s"$name, $division, $country",
        values(latitudeField).toDouble,
        values(longitudeField).toDouble,
        0
      )
  }
}