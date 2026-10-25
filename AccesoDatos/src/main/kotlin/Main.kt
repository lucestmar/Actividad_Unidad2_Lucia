import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists

fun main() {
    val app = PrueInicio()
    app.iniciar()
}

@Serializable
data class VideojuegoJSON(
    @SerialName("id") val id: Int,
    @SerialName("titulo") val titulo: String,
    @SerialName("genero") val genero: String,
    @SerialName("anio") val anio: Int,
    @SerialName("nota") val nota: Double
)

data class VideojuegoXML(
    @JacksonXmlProperty(localName = "id")
    val id: Int = 0,
    @JacksonXmlProperty(localName = "titulo")
    val titulo: String = "",
    @JacksonXmlProperty(localName = "genero")
    val genero: String = "",
    @JacksonXmlProperty(localName = "anio")
    val anio: Int = 0,
    @JacksonXmlProperty(localName = "nota")
    val nota: Double = 0.0
)

@JacksonXmlRootElement(localName = "videojuegos")
data class VideojuegosWrapper(
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "videojuego")
    val listaVideojuego: List<VideojuegoXML> = emptyList()
)

class PrueInicio {
    var videoJuego: MutableList<Videojuego> = mutableListOf()
    val rutaVideoXml: Path = Path.of("Datos", "datos_ini", "videojuegos.xml")
    val rutaVideoJ: Path = Path.of("Datos", "datos_ini", "videojuegos.csv")
    val rutaVideoJson: Path = Path.of("Datos", "datos_ini", "videojuegos.json")

    fun iniciar() {
        rutas()
        var numMenu: Int
        do {
            numMenu = verMenuP()
            when (numMenu) {
                1 -> menuCSV()
                2 -> gestionXML()
                3 -> gestionJSON()
                4 -> conversiones()
                0 -> println("Saliendo del programa...")
                else -> println("Opción no válida. Inténtalo de nuevo.")
            }
        } while (numMenu != 0)
    }

    fun rutas() {
        val rutaDato: Path = Path.of("Datos")
        val rutaInicio: Path = Path.of("Datos", "datos_ini")
        val rutaFin: Path = Path.of("Datos", "datos_fin")

        try {
            if (!rutaDato.exists()) Files.createDirectories(rutaDato)
            if (!rutaFin.exists()) Files.createDirectories(rutaFin)
            if (!rutaInicio.exists()) Files.createDirectories(rutaInicio)

            if (!rutaVideoJ.exists()) Files.createFile(rutaVideoJ)
            if (!rutaVideoXml.exists()) Files.createFile(rutaVideoXml)

            videoJuego = leerDatosCSV(rutaVideoJ)

        } catch (e: Exception) {
            println("\n--- Ocurrió un error durante la carga de rutas ---")
            e.printStackTrace()
        }
    }

    fun verMenuP(): Int {
        println(
            """ 
            ===== CATÁLOGO DE VIDEOJUEGOS =====
            1. Gestión CSV
            2. Leer catálogo desde XML
            3. Leer catálogo desde JSON
            4. Conversiones entre formatos
            0. Salir
            ====================================
            Elige una opción:""".trimIndent()
        )
        return readln().toIntOrNull() ?: -1
    }

    fun menuCSV() {
        var eleccion: Int
        do {
            println(
                """
                
                --- Gestión CSV ---
                1. Listar todos los videojuegos
                2. Buscar videojuego por ID
                3. Añadir nuevo videojuego
                4. Modificar nota de un videojuego
                5. Eliminar videojuego
                0. Volver al menú principal 
                ====================================
                Elige una opción:""".trimIndent()
            )
            eleccion = readln().toIntOrNull() ?: -1

            when (eleccion) {
                0 -> guardarDatosCSV(rutaVideoJ, videoJuego)
                1 -> listarVideojuegos(videoJuego)
                2 -> buscarPorId(videoJuego)
                3 -> añadirVideojuego(videoJuego)
                4 -> modificarNota(videoJuego)
                5 -> eliminarVideojuego(videoJuego)
                else -> println("Opción no válida.")
            }
        } while (eleccion != 0)
    }
    fun conversiones(){
        var eleccion: Int
        do{
        println(
            """
             --- Conversiones ---
                1. CSV  → JSON
                2. CSV  → XML
                3. JSON → CSV
                4. JSON → XML
                5. XML  → CSV
                6. XML  → JSON
                0. Volver
             ====================================
                Elige una opción:""".trimIndent()
        )
        eleccion = readln().toIntOrNull() ?: -1

        when (eleccion) {
            0 ->
            1 ->
            2 ->
            3 ->
            4 ->
            5 ->
            6->
            else -> println("Opción no válida.")
        }
    } while (eleccion != 0)
    }

    fun leerDatosCSV(ruta: Path): MutableList<Videojuego> {
        val listaResultado = mutableListOf<Videojuego>()

        if (!Files.isReadable(ruta) || Files.size(ruta) == 0L) {
            return listaResultado
        }

        val reader = csvReader { delimiter = ';' }
        val filas: List<List<String>> = reader.readAll(ruta.toFile())

        val videojuegosLeidos = filas.mapNotNull { columnas ->
            if (columnas.size >= 5) {
                try {
                    val id = columnas[0].trim().toInt()
                    val titulo = columnas[1].trim()
                    val genero = columnas[2].trim()
                    val anio = columnas[3].trim().toInt()
                    val nota = columnas[4].trim().toDouble()
                    Videojuego(id, titulo, genero, anio, nota)
                } catch (e: Exception) {
                    null
                }
            } else null
        }.toMutableList()

        println("--- Información leída con éxito de: $ruta")
        return videojuegosLeidos
    }

    private fun guardarDatosCSV(ruta: Path, lista: List<Videojuego>) {
        try {
            val writer = csvWriter { delimiter = ';' }
            val filas = lista.map {
                listOf(it.id.toString(), it.titulo, it.genero, it.anio.toString(), it.nota.toString())
            }
            writer.writeAll(filas, ruta.toFile())
            println("Datos guardados correctamente en CSV.")
        } catch (e: Exception) {
            println("Error al guardar en el CSV: ${e.message}")
        }
    }

    private fun eliminarVideojuego(lista: MutableList<Videojuego>) {
        println("Dime la ID a eliminar:")
        val id = readln().toIntOrNull() ?: return println("ID no válida.")

        val eliminado = lista.removeIf { it.id == id }
        if (eliminado) println("Videojuego eliminado con éxito.")
        else println("No se encontró ningún videojuego con ID: $id")
    }

    private fun modificarNota(lista: MutableList<Videojuego>) {
        println("Dime la ID:")
        val id = readln().toIntOrNull() ?: return println("ID no válida.")

        val juego = lista.find { it.id == id }
        if (juego != null) {
            println("Nota actual: ${juego.nota}\nNueva nota:")
            val nuevaNota = readln().toDoubleOrNull()
            if (nuevaNota != null) {
                juego.nota = nuevaNota
                println("Nota actualizada correctamente a $nuevaNota.")
            } else println("Nota introducida no válida.")
        } else println("No se encontró ningún videojuego con ID: $id")
    }

    private fun añadirVideojuego(lista: MutableList<Videojuego>) {
        try {
            println("Ingrese ID:")
            val id = readln().toInt()
            println("Ingrese Título:")
            val titulo = readln()
            println("Ingrese Género:")
            val genero = readln()
            println("Ingrese Año:")
            val anio = readln().toInt()
            println("Ingrese Nota:")
            val nota = readln().toDouble()

            lista.add(Videojuego(id, titulo, genero, anio, nota))
            println("Videojuego añadido con éxito.")
        } catch (e: Exception) {
            println("Error al introducir los datos del videojuego.")
        }
    }

    private fun buscarPorId(lista: List<Videojuego>) {
        println("Dame la ID a buscar:")
        val id = readln().toIntOrNull() ?: return println("ID no válido.")

        val juego = lista.find { it.id == id }
        if (juego != null) println("Videojuego encontrado: $juego")
        else println("No se encontró ningún videojuego con ID: $id")
    }

    private fun listarVideojuegos(lista: List<Videojuego>) {
        if (lista.isEmpty()) {
            println("La lista de videojuegos está vacía.")
        } else {
            println("\n--- LISTA DE VIDEOJUEGOS ---")
            lista.forEach { println(it) }
        }
    }

    private fun leerVideojuegosXML(ruta: Path): List<Videojuego> {
        if (!Files.isReadable(ruta) || Files.size(ruta) == 0L) {
            println("Error o archivo vacío en la ruta: $ruta")
            return emptyList()
        }

        return try {
            val xmlMapper = XmlMapper().apply {
                registerKotlinModule()
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
            val contenedor: VideojuegosWrapper = xmlMapper.readValue(ruta.toFile())

            contenedor.listaVideojuego.map { xml ->
                Videojuego(xml.id, xml.titulo, xml.genero, xml.anio, xml.nota)
            }
        } catch (e: Exception) {
            println("Error al leer el XML: ${e.message}")
            emptyList()
        }
    }

    private fun gestionXML() {
        val listaXml = leerVideojuegosXML(rutaVideoXml)
        if (listaXml.isNotEmpty()) {
            println("\n--- Videojuegos leídos desde XML ---")
            listaXml.forEach { println(it) }
            val nuevos = listaXml.filter { xmlJuego -> videoJuego.none { it.id == xmlJuego.id } }
            videoJuego.addAll(nuevos)
            guardarDatosCSV(rutaVideoJ, videoJuego)
            println("Se han añadido ${nuevos.size} videojuegos nuevos.")

        } else {
            println("No se pudieron cargar los datos del fichero XML.")
        }
    }

    private fun gestionJSON() {
        val listaJson = leerVideojuegosJSON(rutaVideoJson)
        if (listaJson.isNotEmpty()) {
            println("\n--- Videojuegos leídos desde XML ---")
            listaJson.forEach { println(it) }
            val nuevos: List<VideojuegoJSON> = listaJson.filter { jsonJuego -> videoJuego.none { it.id == jsonJuego.id } }
            videoJuego.addAll(nuevos)
            guardarDatosCSV(rutaVideoJ, videoJuego)
            println("Se han añadido ${nuevos.size} videojuegos nuevos.")

        } else {
            println("No se pudieron cargar los datos del fichero XML.")
        }
    }
    private fun leerVideojuegosJSON(ruta: Path): List<VideojuegoJSON> {
        var plantas: List<VideojuegoJSON> = emptyList()

        if (!Files.isReadable(ruta)) {
            println("Error: No se puede leer el fichero en la ruta: $ruta")
        } else {

            val jsonString = Files.readString(ruta)

            plantas = Json.decodeFromString<List<VideojuegoJSON>>(jsonString)
            println("--- Información leída con éxito de: $ruta")
        }
        return plantas
    }
}