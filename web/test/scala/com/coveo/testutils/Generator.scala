package scala.com.coveo.testutils

import com.coveo.models.Suggestion
import org.scalacheck.{Gen, Arbitrary}

object Generator {
  val suggestionGenerator: Gen[Suggestion] = for{
      name <- Gen.alphaStr
      latitude <- Gen.chooseNum(-100.0, 100.0)
      longitude <- Gen.chooseNum(-100.0, 100.0)
      score <- Gen.chooseNum(0.0, 1.0)
  } yield Suggestion(name, latitude, longitude, score)

  implicit val suggestionArb: Arbitrary[Suggestion] = Arbitrary(suggestionGenerator)
}
