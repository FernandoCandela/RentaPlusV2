package pe.edu.ulima.pm.renta.model

import java.io.Serializable

data class Historial(
    val id: String,
    val arrendatario: String,
    val monto: Float,
    val fecha_pago: String,
    val idInmueble: String,
    val url: String
) : Serializable