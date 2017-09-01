package scala.com.coveo.controllers

import com.coveo.controllers.CityAutocompletionService

import scala.com.coveo.testutils.BaseTest

class CityAutocompletionServiceTest extends BaseTest {

  "All input" should "be normalized" in {
    val testTable = Table(
      ("Input", "Output"),
      ("MONTREAL", "montreal"),
      ("MOnTREAL", "montreal"),
      ("MONTREAl", "montreal"),
      ("mONTREAL", "montreal"),
      ("montréal", "montreal"),
      ("montrÉal", "montreal"),
      ("éàèùâêîôûçëïü", "eaeuaeiouceiu")
    )
    forAll(testTable) { (input: String, output: String) =>
      CityAutocompletionService.normalize(input) should be(output)
    }
  }
}
