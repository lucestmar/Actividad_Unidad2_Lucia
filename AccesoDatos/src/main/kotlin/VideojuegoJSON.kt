import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideojuegoJSON(
    @SerialName("id") val id: Int,
    @SerialName("titulo") val titulo: String,
    @SerialName("genero") val genero: String,
    @SerialName("anio") val anio: Int,
    @SerialName("nota") val nota: Double
) {
    fun toDomain(): Videojuego {
        return Videojuego(id, titulo, genero, anio, nota)
    }

    companion object {
        fun fromDomain(videojuego: Videojuego): VideojuegoJSON {
            return VideojuegoJSON(
                videojuego.id,
                videojuego.titulo,
                videojuego.genero,
                videojuego.anio,
                videojuego.nota
            )
        }
    }
}