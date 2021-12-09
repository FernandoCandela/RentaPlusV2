package pe.edu.ulima.pm.renta.model

import java.io.Serializable

data class Inmueble(
    val id: String,
    val direccion: String,
    val idUsuario: String,
    val titulo: String,
    val url: String
) : Serializable