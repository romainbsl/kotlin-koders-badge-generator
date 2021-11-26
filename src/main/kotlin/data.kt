import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.google.zxing.qrcode.encoder.Encoder
import java.awt.Color
import java.awt.Graphics2D
import java.awt.geom.AffineTransform
import java.awt.geom.Area
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

data class Participant(
    val lastname: String,
    val firstname: String,
    val email: String,
    val phone: String,
    val company: String = "",
    val twitter: String = "",
    val category: String = "",
) {

    private val vcard: String = "${firstname.lowercase()}-${lastname.lowercase()}.png"

    init {
        writeImage()
    }

    private fun  writeImage() {
        val qrCode = Encoder.encode(vCardContent(), ErrorCorrectionLevel.L)
        val matrix = qrCode.matrix
        val size = 400

        val area = Area()
        val module = Area(Rectangle2D.Float(0f, 0f, 1f, 1f))

        val affineTransform = AffineTransform()
        val width = matrix.width

        for (i in 0 until width) {
            for (j in 0 until width) {
                if (matrix[j, i].toInt() == 1) {
                    area.add(module)
                }
                affineTransform.setToTranslation(1.0, 0.0)
                module.transform(affineTransform)
            }

            affineTransform.setToTranslation(-width.toDouble(), 1.0)
            module.transform(affineTransform)
        }


        var ratio = size / width.toDouble()
        val adjustment = width / (width + 8).toDouble()
        ratio *= adjustment
        affineTransform.setToTranslation(4.0, 4.0)
        area.transform(affineTransform)
        affineTransform.setToScale(ratio, ratio)
        area.transform(affineTransform)

        val bufferedImage = BufferedImage(size, size, BufferedImage.TYPE_INT_RGB)
        val graphics2D = bufferedImage.graphics as Graphics2D
        graphics2D.paint = Color(0x000000)
        graphics2D.background = Color(0xf4d7d4)
        graphics2D.clearRect(0, 0, size, size)
        graphics2D.fill(area)

        // Ecriture sur le disque
        val file = File("badges/$vcard")
        file.setWritable(true)
        ImageIO.write(bufferedImage, "png", file)
        file.createNewFile()
    }



    fun csv() = "$lastname;$firstname;$company;$category;$vcard"

    private fun vCardContent() = """
        BEGIN:VCARD
        VERSION:3.0
        N:$firstname $lastname
        ORG:$company
        TEL:$phone
        EMAIL:$email
        ${if (twitter.isNotBlank()) "URL:https://twitter.com/$twitter" else ""}
        END:VCARD
    """.trimIndent()

    companion object {
        val absolutePath: String = File("").absolutePath
        fun csvHeader() = "lastname;firstname;company;category;@vcard"
    }
}