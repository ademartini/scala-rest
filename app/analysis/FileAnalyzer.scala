package analysis

import java.io.InputStream
import scala.io.Source
import scala.collection.mutable.Map

object FileAnalyzer {

  def analyze(source: Source, ignoreFilter: Option[String]) : AnalysisResult = {
    
    val lines = source.getLines
    val map  = Map[String,Int]() withDefaultValue 0
    
    lines foreach { line =>
      
      val words = line split("\\W+")
      
      def matchesFilter(str: String) = ignoreFilter exists {filter => str.contains(filter)}
           
      words filter{ word =>
        
        word.length > 0 && !matchesFilter(word)
        
      } foreach { word => map(word) += 1 }
      
    }
    
    val count = map.values.foldLeft(0)((result,current) => result + current)
    
    return AnalysisResult(count,map.toMap)
  }
}