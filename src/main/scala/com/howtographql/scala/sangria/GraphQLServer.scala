package com.howtographql.scala.sangria


import akka.http.scaladsl.server.Route
import sangria.parser.QueryParser
import spray.json.{JsObject, JsString, JsValue}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import sangria.ast.Document
import sangria.execution._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import sangria.marshalling.sprayJson._


object GraphQLServer {



  private val repo = new BookRepository


  // รับ json มาจาก HTTP
  def endpoint(requestJSON: JsValue)(implicit ec: ExecutionContext): Route = {


    // แปลง Json ให้อยู่ในรูปแบบ
    /*{
      query: {},
      variables: {},
      operationName: ""
    }
    */

    val JsObject(fields) = requestJSON


    val JsString(query) = fields("query")


    // ส่วนนี้เป็นการ ตรวจสอบข้อมูลที่ได้รับมา
    QueryParser.parse(query) match {
      case Success(queryAst) =>

        val operation = fields.get("operationName") collect {
          case JsString(op) => op
        }


        val variables = fields.get("variables") match {
          case Some(obj: JsObject) => obj
          case _ => JsObject.empty
        }

        complete(executeGraphQLQuery(queryAst, operation, variables))
      case Failure(error) =>
        complete(BadRequest, JsObject("error" -> JsString(error.getMessage)))
    }

  }

  // หลังจาก ตรวจสอบข้อมูล ส่วนนี้จะทำงาน
  // ต้องไป  query ที่ไหน Schema ตัวไหน  จะถูกระบุในส่วนนี้
  private def executeGraphQLQuery(query: Document, operation: Option[String], vars: JsObject)(implicit ec: ExecutionContext) = {

    Executor.execute(
      GraphQLSchema.SchemaDefinition,
      query,
      repo,
      variables = vars,
      operationName = operation 
    ).map(OK -> _)
      .recover {
        case error: QueryAnalysisError => BadRequest -> error.resolveError
        case error: ErrorWithResolver => InternalServerError -> error.resolveError
      }
  }

}