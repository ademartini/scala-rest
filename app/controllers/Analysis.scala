package controllers

import play.api.mvc.Controller
import play.api.mvc.Action
import java.io.File
import analysis.FileAnalyzer
import scala.io.Source
import play.libs.Akka._
import play.libs.Akka
import play.api.libs.json.Writes
import analysis.AnalysisResult
import play.api.libs.json.Json
import data.UploadedFile
import anorm.Pk
import anorm.NotAssigned
import data.Count
import play.Logger


object Analysis extends Controller{

  case class AnalysisResponseData(result: AnalysisResult,fileId: Long)
  
  implicit val analysisResultWrites = new Writes[AnalysisResponseData] {
    
    def writes(response: AnalysisResponseData) = Json.obj(
    		
        "wordCount" -> response.result.wordCount,
        "wordMap" -> response.result.wordMap,
        "id" -> response.fileId
    )
  }
    
  def post(ignoreFilter: Option[String]) = Action(parse file(to = File createTempFile("upload", ".txt"))){ request =>
    
    //Note, the following is happening asynchronously since Play actions 
    //are asynchronous by default.
    
    val file = request body
    
    val source = Source fromFile(file)
    
    Logger debug("Beginning analysis")
    
    val result = FileAnalyzer analyze(source,ignoreFilter)

    Logger debug("Analysis complete")
    
    Logger debug("Saving UploadedFile DB entry")
    
    val uploadedFile = {UploadedFile save(UploadedFile(None,file getName(),result wordCount))}.get
    
    Logger debug("UploadedFile saved")
    
    Logger debug("Saving Count DB entries")
    
    for( entry <- result wordMap ) {
      
      Count save(Count(None,uploadedFile,entry._1 ,entry._2 ))
    }
    
    Logger debug("All done!")
        
    Ok(Json toJson(AnalysisResponseData(result,uploadedFile)))
  }
  
  def get(id : Long) = Action{request =>
      
	val queryResultOpt = UploadedFile load(id)

	queryResultOpt match{

	case Some(uploadedFile) =>

		Logger info("File Found")
	
		val responseData = getResponseData(uploadedFile)
		
		Ok(Json toJson(responseData))

	case None =>
		Logger debug("File not Found")
		Status(404)
	}
  }
  
  private def getResponseData(file: UploadedFile) : AnalysisResponseData = {
    
	val counts = Count listForFile(file.id.get)
	
	val map = counts map {count => (count word,count count)} toMap
	
	AnalysisResponseData(AnalysisResult(file wordCount,map),file.id.get)
  }
 
  def getList = Action{ result =>
    
    val files = UploadedFile list
    
    val analysisList = files map { file => getResponseData(file) }
    
    Ok(Json toJson(analysisList))
  }
  
}