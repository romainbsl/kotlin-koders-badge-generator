import java.io.File

fun main() {
    val inputCsv = File("billetweb.csv")
        .readText()
        .trim()
        .replace("\"", "")
        .split("\n", "\r\n")
        .drop(1)

    val participants = inputCsv.map { participantCsv ->
        val values = participantCsv.split(";").drop(1)
        Participant(
            category = values[0].takeIf { it.lowercase() in listOf("speaker", "organiser") } ?: "" ,
            lastname = values[1] ,
            firstname = values[2] ,
            email = values[3] ,
            company = values[4] ,
            phone = values[5] ,
            twitter = values[6] ,
        ).csv()
    }

    File("badges/participants.csv").writeText(
        participants.fold(Participant.csvHeader()) { acc: String, s: String ->
            "$acc\n$s"
        },
        Charsets.UTF_16
    )

}