import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "videojuegos")
data class VideojuegosWrapper(
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "videojuego")
    val listaVideojuego: List<VideojuegoXML> = emptyList()
)

data class VideojuegoXML(
    @JacksonXmlProperty(localName = "id") val id: Int,
    @JacksonXmlProperty(localName = "titulo") val titulo: String,
    @JacksonXmlProperty(localName = "genero") val genero: String,
    @JacksonXmlProperty(localName = "anio") val anio: Int,
    @JacksonXmlProperty(localName = "nota") val nota: Double
) {
    fun toDomain(): Videojuego {
        return Videojuego(id, titulo, genero, anio, nota)
    }

    companion object {
        fun fromDomain(videojuego: Videojuego): VideojuegoXML {
            return VideojuegoXML(
                videojuego.id,
                videojuego.titulo,
                videojuego.genero,
                videojuego.anio,
                videojuego.nota
            )
        }
    }
}