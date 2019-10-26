package movietube.movietube.pojo

import java.util.*

data class Channel(
    var Title: String?,
    var PostDate: Date?,
    var Poster: String?,
    var Type: String?,
    var id: Int?,
    var Subscriber: Int?
) {
    constructor() : this("", Date(), "", "", 0, 0)
}