package movietube.movietube.pojo

data class Result(
    val fieldCount: Int,
    val affectedRows: Int,
    val insertId: Int,
    val serverStatus: Int,
    val warningCount: Int,
    val message: String,
    val protocol41: Boolean,
    val changedRows: Int
){
    override fun toString(): String {
        return "Result(fieldCount=$fieldCount, affectedRows=$affectedRows, insertId=$insertId, serverStatus=$serverStatus, warningCount=$warningCount, message='$message', protocol41=$protocol41, changedRows=$changedRows)"
    }
}
