package com.quizscan.app.data
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
data class Category(val id:Int,val name:String)
data class Categories(val trivia_categories:List<Category>)
data class TriviaQuestion(val category:String,val question:String,val correct_answer:String,val incorrect_answers:List<String>)
data class TriviaResponse(val response_code:Int,val results:List<TriviaQuestion>)
interface TriviaApi {
 @GET("api_category.php") suspend fun categories():Categories
 @GET("api.php") suspend fun questions(@Query("amount") amount:Int=10,@Query("category") category:Int,@Query("difficulty") difficulty:String,@Query("type") type:String="multiple"):TriviaResponse
}
object Api { val service:TriviaApi by lazy { Retrofit.Builder().baseUrl("https://opentdb.com/").addConverterFactory(GsonConverterFactory.create()).build().create(TriviaApi::class.java) } }