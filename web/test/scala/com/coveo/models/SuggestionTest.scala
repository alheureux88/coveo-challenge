package scala.com.coveo.models

import com.coveo.models.Suggestion
import play.api.libs.json.Json

import scala.com.coveo.testutils.BaseTest
import scala.com.coveo.testutils.Generator._

class SuggestionTest extends BaseTest {
  "All suggestions" should "be able to serialize and deserialize to the same value" in {
    forAll{ suggestion: Suggestion =>
      suggestion should be(Json.fromJson[Suggestion](Json.toJson(suggestion)).get)
    }
  }
}
