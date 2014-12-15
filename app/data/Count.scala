package data

import anorm.Pk
import play.api.db.DB
import anorm.SqlParser._
import anorm._
import play.api.Play.current

case class Count(id: Option[Long],fileId: Long, word: String, count: Int)

object Count{
  
	def save(count: Count) : Option[Long] = {
		DB.withConnection { implicit connection =>
		  SQL(""" 
		        INSERT INTO counts(file_id,word,count) 
		        VALUES({file_id}, {word},{count})
		  """).on(
		      'file_id -> count.fileId,
		      'word -> count.word,
		      'count -> count.count
		  ).executeInsert()
		}
	}
	  
	def listForFile(fileId: Long) = {
		DB.withConnection { implicit connection =>
		  SQL("SELECT * from counts WHERE file_id = {file_id}").on('file_id -> fileId)as(CountParser *)
		}
	}
	
	private val CountParser: RowParser[Count] = {
		get[Long]("id") ~ get[Long]("file_id") ~ get[String]("word") ~ get[Int]("count") map {
			case id ~ fileId ~ word ~ count => Count(Some(id), fileId, word, count)
		}
	}
}