package com.coveo.models

sealed trait CityType{
  def fileName: String
}

case object City1000 extends CityType{
  override val fileName = "cities1000"
}

case object City5000 extends CityType{
  override val fileName = "cities5000"
}

case object City15000 extends CityType{
  override val fileName = "cities15000"
}