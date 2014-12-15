package data

import anorm.Pk
import play.api.db.DB
import anorm.SqlParser._
import anorm._
import play.api.Play.current


case class UploadedFile(id: Option[Long],name: String, wordCount: Int)

object UploadedFile{
  
	def save(file: UploadedFile) : Option[Long] = {
		DB.withConnection { implicit connection =>
		  SQL(""" 
		        INSERT INTO uploaded_files(name,word_count) 
		        VALUES({name}, {word_count})
		  """).on(
		      'name -> file.name,
		      'word_count -> file.wordCount
		  ).executeInsert()
		} 
	}

	def load(id: Long): Option[UploadedFile] = {
		DB.withConnection { implicit connection =>
		  SQL("SELECT * from uploaded_files WHERE id = {id}")
		    .on('id -> id)
		    .as(uploadedFileParser.singleOpt)
		}
	}
	  
	def list = {
		DB.withConnection { implicit connection =>
		  SQL("SELECT * from uploaded_files").as(uploadedFileParser *)
		}
	}
	
	private val uploadedFileParser: RowParser[UploadedFile] = {
		get[Long]("id") ~ get[String]("name") ~ get[Int]("word_count") map {
			case id ~ name ~ wordCount => UploadedFile(Some(id), name, wordCount)
		}
	}
}