package com.howtographql.scala.sangria

import sangria.schema.{Field, ListType, ObjectType}

import sangria.schema._

object GraphQLSchema {


  val BookType = ObjectType[Unit, Book](
    "Book",
    fields[Unit, Book](
      Field("isbn", IntType, resolve = _.value.isbn),
      Field("title", StringType, resolve = _.value.title),
      Field("author", StringType, resolve = _.value.author)
    )
  )

  
  val QueryType = ObjectType(
    "Query",
    fields[BookRepository, Unit](
      Field("allBooks", ListType(BookType), resolve = c => c.ctx.allBooks)

      ,
      Field("book", 
        OptionType(BookType), 
        arguments = List(Argument("isbn", IntType)), 
        resolve = c => c.ctx.getBook(c.arg[Int]("isbn"))
      )

    )
  )


  val SchemaDefinition = Schema(QueryType)
}