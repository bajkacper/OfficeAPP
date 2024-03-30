package com.domain

import doobie.enumerated.SqlState


object Errors {

  abstract class Errors(val message:String){}

  object NotFoundError extends Errors("Not Found")

  object UniqueConstraintError extends Errors("Duplicate data")

  case class CustomError(state: SqlState) extends Errors("SQL error: " + state.value)

}
