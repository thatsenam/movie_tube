package movietube.movietube.pojo

import java.util.*


data class LiveTv(
    var Links: List<Link>?,
    var Plot: String?,
    var Poster: String?,
    var Title: String?,
    var PostDate: Date?,
    var Views: Int?,
    var Type: String?,
    var Likes: Int?,
    var Dislikes: Int?,
    var Watching: Int?,
    var id: Int = 0
) {
    constructor() : this(emptyList<Link>(), "", "", "", null, 0, "", 0, 0, 0, 0)

    override fun toString(): String {
        return "LiveTv(Links=$Links, Plot=$Plot, Poster=$Poster, Title=$Title, PostDate=$PostDate, Views=$Views, Type=$Type, Likes=$Likes, Dislikes=$Dislikes, Watching=$Watching, id=$id)"
    }


}

