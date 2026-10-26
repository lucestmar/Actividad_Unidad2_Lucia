data class Videojuego(
    val id: Int,
    var titulo: String,
    var genero: String,
    var anio: Int,
    var nota: Double
) {
    companion object {
        const val LONGITUD_TITULO = 30 // 30 caracteres
        const val LONGITUD_GENERO = 20 // 20 caracteres
        const val TAMANO_REGISTRO_BYTES = 4 + (LONGITUD_TITULO * 2) + (LONGITUD_GENERO * 2) + 4 + 8
    }
}