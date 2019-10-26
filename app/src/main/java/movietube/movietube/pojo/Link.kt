package movietube.movietube.pojo

data class Link(var Link: String, var LinkProvider: String) {
    constructor() : this("", "")

    override fun toString(): String {
        return "Link(Link='$Link', LinkProvider='$LinkProvider')"
    }


}