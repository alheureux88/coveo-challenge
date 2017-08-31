package com.coveo.models

case class Location(latitude: Double, longitude: Double) {
  private val earthRadius = 6371

  def distance(latitude2: Double, longitude2: Double): Double = {
    val dLat = Math.toRadians(latitude2 - latitude)
    val dLon = Math.toRadians(longitude2 - longitude)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
      Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(latitude2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2)

    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    earthRadius * c
  }
}
