package com.coveo.controllers

import javax.inject.Inject

import play.api.mvc.{AbstractController, ControllerComponents}

class AutocompleteCities @Inject() (cc: ControllerComponents) extends AbstractController(cc) {

  def complete = Action{
    Ok("yep!")
  }
}
