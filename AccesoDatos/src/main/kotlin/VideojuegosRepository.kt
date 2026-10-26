import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.RandomAccessFile
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists

class VideojuegoRepository(
    val rutaCsv: Path = Path.of("Datos", "datos_ini", "videojuegos.csv"),
    val rutaXml: Path = Path.of("Datos", "datos_ini", "videojuegos.xml"),
    val rutaJson: Path = Path.of("Datos", "datos_ini", "videojuegos.json"),
    val rutaBin: Path = Path.of("Datos", "datos_ini", "videojuegos.bin")
) {

    fun inicializarRutas() {
        val rutaDato: Path = Path.of("Datos")
        val rutaInicio: Path = Path.of("Datos", "datos_ini")
        val rutaFin: Path = Path.of("Datos", "datos_fin")

        try {
            if (!rutaDato.exists()) Files.createDirectories(rutaDato)
            if (!rutaFin.exists()) Files.createDirectories(rutaFin)
            if (!rutaInicio.exists()) Files.createDirectories(rutaInicio)

            if (!rutaCsv.exists()) Files.createFile(rutaCsv)
            if (!rutaXml.exists()) Files.createFile(rutaXml)
            if (!rutaJson.exists()) Files.createFile(rutaJson)
            if (!rutaBin.exists()) Files.createFile(rutaBin)
        } catch (e: Exception) {
            println("\n--- Ocurrió un error durante la carga de rutas ---")
            e.printStackTrace()
        }
    }

    fun leerCSV(): MutableList<Videojuego> {
        val listaResultado = mutableListOf<Videojuego>()
        if (!Files.isReadable(rutaCsv) || Files.size(rutaCsv) == 0L) return listaResultado

        val reader = csvReader { delimiter = ';' }
        val filas: List<List<String>> = reader.readAll(rutaCsv.toFile())

        return filas.mapNotNull { columnas ->
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
    }

    fun guardarCSV(lista: List<Videojuego>) {
        try {
            val writer = csvWriter { delimiter = ';' }
            val filas = lista.map {
                listOf(it.id.toString(), it.titulo, it.genero, it.anio.toString(), it.nota.toString())
            }
            writer.writeAll(filas, rutaCsv.toFile())
            println("Datos guardados correctamente en CSV.")
        } catch (e: Exception) {
            println("Error al guardar en el CSV: ${e.message}")
        }
    }

    fun leerXML(): List<Videojuego> {
        if (!Files.isReadable(rutaXml) || Files.size(rutaXml) == 0L) return emptyList()

        return try {
            val xmlMapper = XmlMapper().apply {
                registerKotlinModule()
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            }
            val contenedor: VideojuegosWrapper = xmlMapper.readValue(rutaXml.toFile())
            contenedor.listaVideojuego.map { it.toDomain() }
        } catch (e: Exception) {
            println("Error al leer el XML: ${e.message}")
            emptyList()
        }
    }

    fun guardarXML(juegos: List<Videojuego>) {
        try {
            val xmlMapper = XmlMapper().apply { registerKotlinModule() }
            val datos = VideojuegosWrapper(juegos.map { VideojuegoXML.fromDomain(it) })
            xmlMapper.writeValue(rutaXml.toFile(), datos)
            println("Datos guardados correctamente en XML: $rutaXml")
        } catch (e: Exception) {
            println("Error al guardar en XML: ${e.message}")
        }
    }

    fun leerJSON(): List<Videojuego> {
        if (!Files.isReadable(rutaJson) || Files.size(rutaJson) == 0L) return emptyList()

        return try {
            val jsonString = Files.readString(rutaJson)
            val json = Json { ignoreUnknownKeys = true }
            val datosJson = json.decodeFromString<List<VideojuegoJSON>>(jsonString)
            datosJson.map { it.toDomain() }
        } catch (e: Exception) {
            println("Error al leer el JSON: ${e.message}")
            emptyList()
        }
    }

    fun guardarJSON(juegos: List<Videojuego>) {
        try {
            val datos = juegos.map { VideojuegoJSON.fromDomain(it) }
            val json = Json { prettyPrint = true }
            Files.writeString(rutaJson, json.encodeToString(datos))
            println("Datos guardados correctamente en JSON: $rutaJson")
        } catch (e: Exception) {
            println("Error al guardar en JSON: ${e.message}")
        }
    }

    private fun String.ajustarLongitud(longitud: Int): String {
        return this.padEnd(longitud, ' ').take(longitud)
    }

    private fun RandomAccessFile.escribirStringFijo(texto: String, longitud: Int) {
        writeChars(texto.ajustarLongitud(longitud))
    }

    private fun RandomAccessFile.leerStringFijo(longitud: Int): String {
        val sb = StringBuilder()
        for (i in 0 until longitud) {
            sb.append(readChar())
        }
        return sb.toString().trim()
    }

    private fun escribirRegistroBIN(raf: RandomAccessFile, juego: Videojuego) {
        raf.writeInt(juego.id)
        raf.escribirStringFijo(juego.titulo, Videojuego.LONGITUD_TITULO)
        raf.escribirStringFijo(juego.genero, Videojuego.LONGITUD_GENERO)
        raf.writeInt(juego.anio)
        raf.writeDouble(juego.nota)
    }

    fun guardarTodosBIN(lista: List<Videojuego>) {
        RandomAccessFile(rutaBin.toFile(), "rw").use { raf ->
            raf.setLength(0)
            for (juego in lista) {
                escribirRegistroBIN(raf, juego)
            }
        }
    }

    fun leerBIN(): List<Videojuego> {
        val lista = mutableListOf<Videojuego>()
        if (!Files.exists(rutaBin) || Files.size(rutaBin) == 0L) return lista

        RandomAccessFile(rutaBin.toFile(), "r").use { raf ->
            val numRegistros = raf.length() / Videojuego.TAMANO_REGISTRO_BYTES
            for (i in 0 until numRegistros) {
                val id = raf.readInt()
                val titulo = raf.leerStringFijo(Videojuego.LONGITUD_TITULO)
                val genero = raf.leerStringFijo(Videojuego.LONGITUD_GENERO)
                val anio = raf.readInt()
                val nota = raf.readDouble()

                if (id != -1) {
                    lista.add(Videojuego(id, titulo, genero, anio, nota))
                }
            }
        }
        return lista
    }

    fun añadirBIN(juego: Videojuego) {
        RandomAccessFile(rutaBin.toFile(), "rw").use { raf ->
            raf.seek(raf.length())
            escribirRegistroBIN(raf, juego)
        }
    }

    fun modificarBIN(id: Int, nuevoJuego: Videojuego): Boolean {
        if (!Files.exists(rutaBin)) return false

        RandomAccessFile(rutaBin.toFile(), "rw").use { raf ->
            val numRegistros = raf.length() / Videojuego.TAMANO_REGISTRO_BYTES
            for (i in 0 until numRegistros) {
                raf.seek(i * Videojuego.TAMANO_REGISTRO_BYTES.toLong())
                val currentId = raf.readInt()
                if (currentId == id) {
                    raf.seek(i * Videojuego.TAMANO_REGISTRO_BYTES.toLong())
                    escribirRegistroBIN(raf, nuevoJuego)
                    return true
                }
            }
        }
        return false
    }

    fun eliminarBIN(id: Int): Boolean {
        if (!Files.exists(rutaBin)) return false

        RandomAccessFile(rutaBin.toFile(), "rw").use { raf ->
            val numRegistros = raf.length() / Videojuego.TAMANO_REGISTRO_BYTES
            for (i in 0 until numRegistros) {
                raf.seek(i * Videojuego.TAMANO_REGISTRO_BYTES.toLong())
                val currentId = raf.readInt()
                if (currentId == id) {
                    raf.seek(i * Videojuego.TAMANO_REGISTRO_BYTES.toLong())
                    raf.writeInt(-1)
                    return true
                }
            }
        }
        return false
    }
}