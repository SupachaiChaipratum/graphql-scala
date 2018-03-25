package com.howtographql.scala.sangria


object BooksMockData {
  var books : List[Book]=List(
    Book(isbn = 1785880519,
      title = "Mastering Java Machine Learning",
      author = "Dr. Uday Kamath")
    ,
    Book(isbn = 1783987421,
      title = "Test-Driven Java Development",
      author = "Viktor Farcic")
    ,
    Book(isbn = 1788392825,
      title = "Learning Scala Programming: Object-oriented programming meets functional reactive to create Scalable and Concurrent programs",
      author = "Vikash Sharma")
  )
}

class BookRepository {

  import BooksMockData._

  def allBooks = books

  def getBook(isbn: Int) ={
    books.filter(b=>b.isbn == isbn).headOption
  }

}

