fun main() {
    val repository = VideojuegoRepository()
    val service = VideojuegoService(repository)
    val app = PrueInicio(service)
    app.iniciar()
}

class PrueInicio(private val service: VideojuegoService) {

    fun iniciar() {
        service.inicializar()
        var numMenu: Int
        do {
            numMenu = verMenuP()
            when (numMenu) {
                1 -> menuCSV()
                2 -> gestionXMLUI()
                3 -> gestionJSONUI()
                4 -> conversiones()
                5 -> menuBIN()
                0 -> println("Saliendo del programa...")
                else -> println("Opción no válida. Inténtalo de nuevo.")
            }
        } while (numMenu != 0)
    }

    private fun verMenuP(): Int {
        println(
            """
            ===== CATÁLOGO DE VIDEOJUEGOS =====
            1. Gestión CSV
            2. Leer catálogo desde XML
            3. Leer catálogo desde JSON
            4. Conversiones entre formatos
            5. Gestión fichero BIN
            0. Salir
            ====================================
            Elige una opción:""".trimIndent()
        )
        return readln().toIntOrNull() ?: -1
    }

    private fun menuBIN() {
        var eleccion: Int
        do {
            println(
                """
                
                ===== CRUD fichero BIN =====
                1. Importar información de un fichero de intercambio.
                2. Leer información del fichero binario.
                3. Añadir un registro nuevo
                4. Modificar un registro existente (por ID)
                5. Eliminar un registro existente (por ID)
                0. Volver
                ============================
                Elige una opción:""".trimIndent()
            )
            eleccion = readln().toIntOrNull() ?: -1

            when (eleccion) {
                0 -> println("Volviendo al menú principal...")
                1 -> {
                    service.importarABinario()
                    println("Fichero binario cargado/importado con éxito.")
                }
                2 -> {
                    val lista = service.leerBinario()
                    if (lista.isEmpty()) {
                        println("El fichero binario está vacío o no contiene registros válidos.")
                    } else {
                        println("\n--- REGISTROS EN FICHERO BINARIO ---")
                        lista.forEach { println(it) }
                    }
                }
                3 -> {
                    val juego = pedirDatosVideojuego()
                    if (juego != null) {
                        service.añadirABinario(juego)
                        println("Registro añadido con éxito al fichero binario.")
                    }
                }
                4 -> {
                    println("Ingrese ID del registro a modificar en BIN:")
                    val id = readln().toIntOrNull() ?: return println("ID no válida.")
                    println("Introduce los nuevos datos:")
                    val nuevoJuego = pedirDatosVideojuego(idFija = id)
                    if (nuevoJuego != null) {
                        if (service.modificarEnBinario(id, nuevoJuego)) {
                            println("Registro con ID $id modificado correctamente en el fichero BIN.")
                        } else {
                            println("No se encontró ningún registro con la ID especificada.")
                        }
                    }
                }
                5 -> {
                    println("Ingrese ID a eliminar en BIN:")
                    val id = readln().toIntOrNull() ?: return println("ID no válida.")
                    if (service.eliminarDeBinario(id)) {
                        println("Registro con ID $id eliminado (marcado como borrado) en BIN.")
                    } else {
                        println("No se encontró ningún registro con ID: $id")
                    }
                }
                else -> println("Opción no válida.")
            }
        } while (eleccion != 0)
    }

    private fun pedirDatosVideojuego(idFija: Int? = null): Videojuego? {
        return try {
            val id = idFija ?: run {
                println("Ingrese ID:")
                readln().toInt()
            }
            println("Ingrese Título:")
            val titulo = readln()
            println("Ingrese Género:")
            val genero = readln()
            println("Ingrese Año:")
            val anio = readln().toInt()
            println("Ingrese Nota:")
            val nota = readln().toDouble()

            Videojuego(id, titulo, genero, anio, nota)
        } catch (e: Exception) {
            println("Error introduciendo datos.")
            null
        }
    }

    private fun menuCSV() {
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
                0 -> service.guardarCSV()
                1 -> listarVideojuegosUI()
                2 -> buscarPorIdUI()
                3 -> añadirVideojuegoUI()
                4 -> modificarNotaUI()
                5 -> eliminarVideojuegoUI()
                else -> println("Opción no válida.")
            }
        } while (eleccion != 0)
    }

    private fun listarVideojuegosUI() {
        val juegos = service.listarVideojuegos()
        if (juegos.isEmpty()) {
            println("La lista de videojuegos está vacía.")
        } else {
            println("\n--- LISTA DE VIDEOJUEGOS ---")
            juegos.forEach { println(it) }
        }
    }

    private fun buscarPorIdUI() {
        println("Dame la ID a buscar:")
        val id = readln().toIntOrNull() ?: return println("ID no válido.")
        val juego = service.buscarPorId(id)
        if (juego != null) println("Videojuego encontrado: $juego")
        else println("No se encontró ningún videojuego con ID: $id")
    }

    private fun añadirVideojuegoUI() {
        val juego = pedirDatosVideojuego()
        if (juego != null) {
            service.añadirVideojuego(juego)
            println("Videojuego añadido con éxito.")
        }
    }

    private fun modificarNotaUI() {
        println("Dime la ID:")
        val id = readln().toIntOrNull() ?: return println("ID no válida.")
        val juego = service.buscarPorId(id)
        if (juego != null) {
            println("Nota actual: ${juego.nota}\nNueva nota:")
            val nuevaNota = readln().toDoubleOrNull()
            if (nuevaNota != null) {
                if (service.modificarNota(id, nuevaNota)) {
                    println("Nota actualizada correctamente a $nuevaNota.")
                }
            } else println("Nota introducida no válida.")
        } else println("No se encontró ningún videojuego con ID: $id")
    }

    private fun eliminarVideojuegoUI() {
        println("Dime la ID a eliminar:")
        val id = readln().toIntOrNull() ?: return println("ID no válida.")
        if (service.eliminarVideojuego(id)) println("Videojuego eliminado con éxito.")
        else println("No se encontró ningún videojuego con ID: $id")
    }

    private fun gestionXMLUI() {
        val añadidos = service.gestionXML()
        if (añadidos >= 0) {
            println("Se han añadido $añadidos videojuegos nuevos desde XML.")
        } else {
            println("No se pudieron cargar los datos del fichero XML.")
        }
    }

    private fun gestionJSONUI() {
        val añadidos = service.gestionJSON()
        if (añadidos >= 0) {
            println("Se han añadido $añadidos videojuegos nuevos desde JSON.")
        } else {
            println("No se pudieron cargar los datos del fichero JSON.")
        }
    }

    private fun conversiones() {
        var eleccion: Int
        do {
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
                Elige una opción:
                """.trimIndent()
            )
            eleccion = readln().toIntOrNull() ?: -1

            when (eleccion) {
                0 -> println("Volviendo al menú principal...")
                1 -> ejecutarConversion { service.convertirCSVaJSON() }
                2 -> ejecutarConversion { service.convertirCSVaXML() }
                3 -> ejecutarConversion { service.convertirJSONaCSV() }
                4 -> ejecutarConversion { service.convertirJSONaXML() }
                5 -> ejecutarConversion { service.convertirXMLaCSV() }
                6 -> ejecutarConversion { service.convertirXMLaJSON() }
                else -> println("Opción no válida.")
            }
        } while (eleccion != 0)
    }

    private inline fun ejecutarConversion(accion: () -> Unit) {
        try {
            accion()
            println("Hecho con éxito.")
        } catch (e: Exception) {
            println("Error al realizar la conversión: ${e.message}")
        }
    }
}