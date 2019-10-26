package movietube.movietube.pojo

import java.util.*

data class Comment(
    var id: Int = 0,
    var Body: String = "",
    var PostDate: Date = Date(),
    var Author: String = "",
    var u_id: Int = 0,
    var p_id: Int = 0,
    var Type: String = "",
    var Votes: Int = 0,
    var Replies: Int = 0
) {
    override fun toString(): String {
        return "Comment(id=$id, Body='$Body', PostDate=$PostDate, Author='$Author', u_id=$u_id, p_id=$p_id, Type='$Type')"
    }
}