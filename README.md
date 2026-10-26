# Actividad_Unidad2_Lucia
# Catálogo de Videojuegos

Programa de consola en Kotlin para gestionar un catálogo de videojuegos.
Permite leer datos desde CSV, convertirlos a JSON o XML y a un fichero binario de acceso aleatorio, y exportarlos de nuevo a CSV.

## 1. Estructura de datos

### **Data Class:**

```kotlin
data class Videojuego(
    val id: Int,
    val titulo: String,
    val genero: String,
    val anio: Int,
    val nota: Double
)
```

### **Estructura del registro binario:**

- **ID**: Int - 4 bytes
- **titulo**: String - 40 bytes (longitud fija)
- **genero**: String - 20 bytes (longitud fija)
- **anio**: Int - 4 bytes
- **nota**: Double - 8 bytes
- **Tamaño Total del Registro**: 4 + 40 + 20 + 4 + 8 = 76 bytes

## 2. Instrucciones de ejecución

- **Requisitos previos**: JDK 17.
- **Compilación**: Abre el proyecto en IntelliJ IDEA y deja que Gradle sincronice las dependencias.
- **Fichero de datos inicial**: Coloca `videojuegos.csv` en la carpeta `datos_ini` antes de ejecutar.
- **Ejecución principal**: Ejecuta `Main.kt` para el catálogo con acceso aleatorio.
- **Conversión entre formatos**: Ejecuta dentro de `Main.kt` elige `Conversiones entre formatos` para elegir las transformaciones entre CSV o JSON o XML.

## 3. Decisiones de diseño
###Estructura de carpetas
src/
├── Videojuego.kt              ← data class
├── VideojuegoRepository.kt    ← leer/escribir CSV
├── VideojuegoService.kt       ← lógica CRUD
└── Main.kt                    ← menús + readln
