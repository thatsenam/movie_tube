package movietube.movietube.pojo

import com.google.firebase.firestore.PropertyName
import java.util.*


data class Movie(
    @PropertyName("Actors")
    var Actors: String? = null,

    @PropertyName("Awards")
    var Awards: String? = null,

    @PropertyName("BoxOffice")
    var BoxOffice: String? = null,

    @PropertyName("ChannelID")
    var ChannelID: Int? = null, var Likes: Int? = null, var Dislikes: Int? = null,

    @PropertyName("Contributor")
    var Contributor: String? = null,

    @PropertyName("Country")
    var Country: String? = null,

    @PropertyName("Director")
    var Director: String? = null,

    @PropertyName("DVD")
    var DVD: String? = null,

    @PropertyName("Genre")
    var Genre: String? = null,

    var id: Int = 0,

    var imdbID: String? = null,

    var imdbRating: String? = null,

    var imdbVotes: String? = null,

    @PropertyName("Language")
    var Language: String? = null,

    @PropertyName("Link")
    var Links: List<Link>? = null,

    @PropertyName("Metascore")
    var Metascore: String? = null,

    @PropertyName("Plot")
    var Plot: String? = null,

    @PropertyName("PostDate")
    var PostDate: Date? = null,

    @PropertyName("Poster")
    var Poster: String? = null,

    @PropertyName("Production")
    var Production: String? = null,

    @PropertyName("Rated")
    var Rated: String? = null,

    @PropertyName("Ratings")
    var Ratings: List<Rating>? = null,

    @PropertyName("Released")
    var Released: String? = null,

    @PropertyName("Response")
    var Response: String? = null,

    @PropertyName("Runtime")
    var Runtime: String? = null,

    @PropertyName("Title")
    var Title: String? = null,


    @PropertyName("Type")
    var Type: String? = null,

    @PropertyName("Views")
    var Views: Int? = null,

    @PropertyName("Website")
    var Website: String? = null,

    @PropertyName("Writer")
    var Writer: String? = null,

    @PropertyName("Year")
    var Year: String? = null
) {
    constructor() : this(
        "",
        "", "",
        0, 0, 0, "",
        "", "",
        "", "",
        0, "",
        "", "",
        "", emptyList(),
        "", "",
        null, "",
        "", "",
        null, "",
        "", "",
        "", "",
        0, "",
        "", ""
    )

    override fun toString(): String {
        return "Movie(Actors=$Actors, Awards=$Awards, BoxOffice=$BoxOffice, ChannelID=$ChannelID, Contributor=$Contributor, Country=$Country, Director=$Director, DVD=$DVD, Genre=$Genre, id=$id, imdbID=$imdbID, imdbRating=$imdbRating, imdbVotes=$imdbVotes, Language=$Language, Link=${Links.toString()}, Metascore=$Metascore, Plot=$Plot, PostDate=$PostDate, Poster=$Poster, Production=$Production, Rated=$Rated, Ratings=$Ratings, Released=$Released, Response=$Response, Runtime=$Runtime, Title=$Title, Type=$Type, Views=$Views, Website=$Website, Writer=$Writer, Year=$Year)"
    }


}

data class Rating(
    @PropertyName("Source")
    var Source: String,

    @PropertyName("Value")
    var Value: String
) {
    constructor() : this("", "") {

    }
}


