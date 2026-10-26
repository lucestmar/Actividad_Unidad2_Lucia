class VideojuegoService(private val repository: VideojuegoRepository) {

    private var videoJuegos: MutableList<Videojuego> = mutableListOf()

    fun inicializar() {
        repository.inicializarRutas()
        videoJuegos = repository.leerCSV()
    }

    fun listarVideojuegos(): List<Videojuego> = videoJuegos

    fun buscarPorId(id: Int): Videojuego? = videoJuegos.find { it.id == id }

    fun añadirVideojuego(juego: Videojuego) {
        videoJuegos.add(juego)
    }

    fun modificarNota(id: Int, nuevaNota: Double): Boolean {
        val juego = buscarPorId(id)
        return if (juego != null) {
            juego.nota = nuevaNota
            true
        } else false
    }

    fun eliminarVideojuego(id: Int): Boolean {
        return videoJuegos.removeIf { it.id == id }
    }

    fun guardarCSV() {
        repository.guardarCSV(videoJuegos)
    }

    fun importarABinario() {
        repository.guardarTodosBIN(videoJuegos)
    }

    fun leerBinario(): List<Videojuego> {
        return repository.leerBIN()
    }

    fun añadirABinario(juego: Videojuego) {
        repository.añadirBIN(juego)
    }

    fun modificarEnBinario(id: Int, nuevoJuego: Videojuego): Boolean {
        return repository.modificarBIN(id, nuevoJuego)
    }

    fun eliminarDeBinario(id: Int): Boolean {
        return repository.eliminarBIN(id)
    }

    fun gestionXML(): Int {
        val listaXml = repository.leerXML()
        if (listaXml.isNotEmpty()) {
            val nuevos = listaXml.filter { xmlJuego -> videoJuegos.none { it.id == xmlJuego.id } }
            videoJuegos.addAll(nuevos)
            guardarCSV()
            return nuevos.size
        }
        return -1
    }

    fun gestionJSON(): Int {
        val listaJson = repository.leerJSON()
        if (listaJson.isNotEmpty()) {
            val nuevos = listaJson.filter { jsonJuego -> videoJuegos.none { it.id == jsonJuego.id } }
            videoJuegos.addAll(nuevos)
            guardarCSV()
            return nuevos.size
        }
        return -1
    }

    fun convertirCSVaJSON() {
        val juegos = repository.leerCSV()
        repository.guardarJSON(juegos)
    }

    fun convertirCSVaXML() {
        val juegos = repository.leerCSV()
        repository.guardarXML(juegos)
    }

    fun convertirJSONaCSV() {
        val juegos = repository.leerJSON()
        repository.guardarCSV(juegos)
        videoJuegos = juegos.toMutableList()
    }

    fun convertirJSONaXML() {
        val juegos = repository.leerJSON()
        repository.guardarXML(juegos)
    }

    fun convertirXMLaCSV() {
        val juegos = repository.leerXML()
        repository.guardarCSV(juegos)
        videoJuegos = juegos.toMutableList()
    }

    fun convertirXMLaJSON() {
        val juegos = repository.leerXML()
        repository.guardarJSON(juegos)
    }
}