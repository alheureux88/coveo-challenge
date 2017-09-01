package scala.com.coveo.testutils

import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.prop.PropertyChecks

trait BaseTest extends FlatSpec with Matchers with ScalaFutures with PropertyChecks {

}
