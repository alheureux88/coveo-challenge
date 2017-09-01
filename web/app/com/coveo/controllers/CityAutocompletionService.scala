package com.coveo.controllers

import java.text.Normalizer
import java.util
import javax.inject.Inject

import akka.stream.Materializer
import com.coveo.models._
import me.xdrop.fuzzywuzzy.FuzzySearch
import me.xdrop.fuzzywuzzy.algorithms.TokenSort
import play.api.Configuration
import play.api.libs.ws.WSClient

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

class CityAutocompletionService @Inject()(config: Configuration,
                                          ws: WSClient,
                                          cityFileParser: CityFileParser)
                                         (implicit mat: Materializer, ec: ExecutionContext) {
  private val maxSearchNumber: Int = config.get[Int]("fuzzy.items.maxSearch")
  private val maxNumber: Int = config.get[Int]("fuzzy.items.maxReturn")
  private val cutoffScore: Int = config.get[Int]("fuzzy.items.cutoff")
  private val distanceWeight: Double = config.get[Double]("fuzzy.items.distanceWeight")

  private val futureResponses: Future[Map[String, Seq[Suggestion]]] = cityFileParser.parse.map { suggestions =>
    val suggestionGroupedByName = suggestions.groupBy { case (name, _) => CityAutocompletionService.normalize(name) }
    val normalizedNameToSuggestion = suggestionGroupedByName.mapValues { suggestions => suggestions.map { case (_, suggestion) => suggestion } }
    normalizedNameToSuggestion
  }

  private val futureCities: Future[util.Collection[String]] = futureResponses.map(_.keys).map(_.asJavaCollection)

  private def calculateScore(response: Suggestion, index: Int, listSize: Int) = {
    val fuzzyScoreWeighted = response.score * (1 - distanceWeight)
    val distanceScoreWeighted = (listSize - index)/listSize.toDouble * distanceWeight
    response.copy(score = fuzzyScoreWeighted + distanceScoreWeighted)
  }

  def execute(query: String, location: Option[Location]): Future[Seq[Suggestion]] = {

    for {
      cities <- futureCities
      cityNameToResponse <- futureResponses
    } yield {
      val fuzzySearch = FuzzySearch.extractTop(
        CityAutocompletionService.normalize(query),
        cities,
        CityAutocompletionService.algo,
        maxSearchNumber,
        cutoffScore).asScala
        .flatMap(response => cityNameToResponse(response.getString).map(suggestion => suggestion.copy(score = response.getScore / 100.0)))

      val fuzzyAndDistance = location.map { loc =>
        val listWithDistance = CityAutocompletionService.addDistance(loc)(fuzzySearch)
        val listSize = listWithDistance.size
          listWithDistance
          .sortBy { case (distance, _) => distance }
          .map(_._2)
          .zipWithIndex
          .map { case (response, index) => calculateScore(response, index, listSize) }
      }

      fuzzyAndDistance.getOrElse(fuzzySearch)
        .sortBy(-_.score)
        .take(maxNumber)

    }
  }
}

object CityAutocompletionService {
  def normalize(input: String): String = {
    Normalizer
      .normalize(input, Normalizer.Form.NFD)
      .replaceAll("[^\\p{ASCII}]", "")
      .toLowerCase
  }

  private def addDistance(location: Location)(elements: Seq[Suggestion]): Seq[(Double, Suggestion)] = {
    elements.map(element => location.distance(element.latitude, element.longitude) -> element)
  }

  private val algo = new TokenSort
}
