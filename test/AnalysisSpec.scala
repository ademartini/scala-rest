import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import play.libs.Json
import org.specs2.matcher.JsonMatchers
import org.specs2.mutable.Specification

@RunWith(classOf[JUnitRunner])
class FileAnalyzerSpec extends Specification with JsonMatchers{

  "Analysis" should {

    "Count words within a file" in new WithApplication{
      
      var body = "hello world\nworld\nhello again"
        
      var post = FakeRequest(POST, "/analysis").withTextBody(body)
      val result = route(post).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val json = contentAsJson(result).toString
      
      json must  /("wordCount" -> 5)
      json must  /("wordMap") / ("hello" -> 2)
      json must  /("wordMap") / ("world" -> 2)
      json must  /("wordMap") / ("again" -> 1)
    }
    
    "Ignore spaces" in new WithApplication{
      
      var body = "   hello world  \n   world\n hello again   "
        
      var post = FakeRequest(POST, "/analysis").withTextBody(body)
      val result = route(post).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val json = contentAsJson(result).toString
      
      json must  /("wordCount" -> 5)
      json must  /("wordMap") / ("hello" -> 2)
      json must  /("wordMap") / ("world" -> 2)
      json must  /("wordMap") / ("again" -> 1)
    }
    
    "Handle blank documents" in new WithApplication{
      
      var body = "    \n    \n"
        
      var post = FakeRequest(POST, "/analysis").withTextBody(body)
      val result = route(post).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val json = contentAsJson(result).toString
      
      json must  /("wordCount" -> 0)
    }
    
    "Support ignore filter" in new WithApplication{
      
      var body = "hello world\nworld\nhello again low"
        
      var post = FakeRequest(POST, "/analysis?ignoreFilter=lo").withTextBody(body)
      val result = route(post).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == "application/json")
      val json = contentAsJson(result).toString
      
      json must  /("wordCount" -> 3)
      json must not  (/("wordMap") / ("hello" -> 2))
      json must  /("wordMap") / ("world" -> 2)
      json must /("wordMap") / ("again" -> 1)
      json must not (/("wordMap") / ("low" -> 1))
    }
    
    "Return 404 for unknown file" in new WithApplication{
      
      var post = FakeRequest(GET, "/analysis/1000000")
      val result = route(post).get

      status(result) must equalTo(404)
    }
    
    "Do get on single file" in new WithApplication{
      
      var body = "hello world\nworld\nhello again"
        
      var post = FakeRequest(POST, "/analysis").withTextBody(body)
      val result = route(post).get

      val id = {contentAsJson(result)\("id")}.as[Long]

      var get = FakeRequest(GET, "/analysis/" + id)
      val getResult = route(get).get

      status(getResult) must equalTo(OK)
      contentType(getResult) must beSome.which(_ == "application/json")
      val json = contentAsJson(getResult).toString
      
      json must  /("wordCount" -> 5)
      json must  /("wordMap") / ("hello" -> 2)
      json must  /("wordMap") / ("world" -> 2)
      json must  /("wordMap") / ("again" -> 1)
      
    }
    
    "Return an empty list when there were no uploads" in new WithApplication{
      
      var get = FakeRequest(GET, "/analysis/")
      val getResult = route(get).get

      status(getResult) must equalTo(OK)
      contentType(getResult) must beSome.which(_ == "application/json")
      val json = contentAsJson(getResult).toString
      
      json must equalTo("[]")
      
    }
    
    "Do get when there were two uploads" in new WithApplication{
      
      var body = "hello world\nworld\nhello again"
      var body2 = "Awesome\ntest\ntest"
        
      val post1 = FakeRequest(POST, "/analysis").withTextBody(body)
      val post2 = FakeRequest(POST, "/analysis").withTextBody(body2)
      
      val id1 = {contentAsJson(route(post1).get)\("id")}.as[Long]
      val id2 = {contentAsJson(route(post2).get)\("id")}.as[Long]
      
      var get = FakeRequest(GET, "/analysis/")
      val getResult = route(get).get

      status(getResult) must equalTo(OK)
      contentType(getResult) must beSome.which(_ == "application/json")
      val json = contentAsJson(getResult).toString
      
      json must  /#(0) / ("wordCount" -> 5)
      json must  /#(0) / ("id" -> id1)
      json must  /#(0) /("wordMap") / ("hello" -> 2)
      json must  /#(0) /("wordMap") / ("world" -> 2)
      json must  /#(0) /("wordMap") / ("again" -> 1)

      json must  /#(1) / ("wordCount" -> 3)
      json must  /#(1) /("wordMap") / ("Awesome" -> 1)
      json must  /#(1) /("wordMap") / ("test" -> 2)
      json must  /#(1) / ("id" -> id2)
    }
  }
}
