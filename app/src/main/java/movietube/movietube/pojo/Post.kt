
package movietube.movietube.pojo

import java.util.*

data class Post (
	val id: Int,
	val Title : String,
	val Author : String,
	val Body : String,
	val Replies : Int,
	val Type : String,
	var Votes : Int,
	val PostDate : Date,
	val Reports : Int,
	val u_id:Int

)