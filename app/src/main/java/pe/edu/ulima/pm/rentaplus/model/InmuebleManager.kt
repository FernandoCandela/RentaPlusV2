package pe.edu.ulima.pm.renta.model

import android.net.Uri
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class InmuebleManager() {
    private val dbFirebase = Firebase.firestore

    fun getInmueblesByUser(
        idUser: String,
        callbackOK: (MutableList<Inmueble>) -> Unit,
        callbackError: (String) -> Unit
    ) {
        dbFirebase.collection("inmueble")
            .whereEqualTo("idUsuario", idUser)
            .get()
            .addOnSuccessListener { res ->
                val favoritos = arrayListOf<Inmueble>()
                for (document in res) {
                    val inm = Inmueble(
                        document.id,
                        document.data["direccion"]!! as String,
                        document.data["idUsuario"]!! as String,
                        document.data["titulo"]!! as String,
                        document.data["url"]!! as String
                    )
                    favoritos.add(inm)
                }
                callbackOK(favoritos)
            }
            .addOnFailureListener {
                callbackError(it.message!!)
            }
    }

    fun getHistorialByInmueble(
        idInmueble: String,
        callbackOK: (MutableList<Historial>) -> Unit,
        callbackError: (String) -> Unit
    ) {
        dbFirebase.collection("historial")
            .whereEqualTo("idInmueble", idInmueble)
            .get()
            .addOnSuccessListener { res ->
                val historial = arrayListOf<Historial>()
                for (document in res) {
                    val his = Historial(
                        document.id,
                        document.data["arrendatario"]!! as String,
                        document.data["monto"]!! as Float,
                        document.data["fecha_pago"]!! as String,
                        document.data["idInmueble"]!! as String,
                        document.data["url_factura"]!! as String
                    )
                    historial.add(his)
                }
                callbackOK(historial)
            }
            .addOnFailureListener {
                callbackError(it.message!!)
            }
    }
    fun getArrendatarioActivo(
        idInmueble: String,
        callbackOK: (Arrendatario) -> Unit,
        callbackError: (String) -> Unit
    ) {
        dbFirebase.collection("arrendatario")
            .whereEqualTo("idInmueble", idInmueble)
            .whereEqualTo("activo", true)
            .get()
            .addOnSuccessListener { res ->
                val arrendatarios = arrayListOf<Arrendatario>()
                for (document in res) {
                    val pk = Arrendatario(
                        document.id,
                        document.data["activo"]!! as Boolean,
                        document.data["apellidos"]!! as String,
                        document.data["email"]!! as String,
                        document.data["fecha_pago"]!! as String,
                        document.data["idInmueble"]!! as String,
                        document.data["monto"]!! as Float,
                        document.data["nombre"]!! as String,
                        document.data["telefono"]!! as String,
                    )
                    arrendatarios.add(pk)
                }
                callbackOK(arrendatarios.first())
            }
            .addOnFailureListener {
                callbackError(it.message!!)
            }
    }

    fun addInmueble(
        inmueble: Inmueble,
        idUser: String,
        callbackOK: (String) -> Unit,
        callbackError: (String) -> Unit
    ) {
        dbFirebase.collection("inmueble")
            .add(
                hashMapOf(
                    "direccion" to inmueble.direccion,
                    "idUsuario" to idUser,
                    "titulo" to inmueble.titulo,
                    "url" to inmueble.url
                )
            )
            .addOnSuccessListener { documentReference ->
                callbackOK(documentReference.id)
            }
            .addOnFailureListener {
                callbackError(it.message!!)
            }
    }

    fun addHistorial(
        historial: Historial,
        callbackOK: (String) -> Unit,
        callbackError: (String) -> Unit
    ) {
        dbFirebase.collection("historial")
            .add(
                hashMapOf(
                    "arrendatario" to historial.arrendatario,
                    "fecha_pago" to historial.fecha_pago,
                    "idInmueble" to historial.idInmueble,
                    "monto" to historial.monto,
                    "url" to historial.url
                )
            )
            .addOnSuccessListener { documentReference ->
                callbackOK(documentReference.id)
            }
            .addOnFailureListener {
                callbackError(it.message!!)
            }
    }

    fun addArrendatario(
        arrendatario: Arrendatario,
        callbackOK: (String) -> Unit,
        callbackError: (String) -> Unit
    ) {
        dbFirebase.collection("arrendatario")
            .add(
                hashMapOf(
                    "arrendatario" to arrendatario.activo,
                    "fecha_pago" to arrendatario.apellidos,
                    "idInmueble" to arrendatario.email,
                    "monto" to arrendatario.monto,
                    "nombre" to arrendatario.nombre,
                    "telefono" to arrendatario.telefono
                )
            )
            .addOnSuccessListener { documentReference ->
                callbackOK(documentReference.id)
            }
            .addOnFailureListener {
                callbackError(it.message!!)
            }
    }
    fun changetoinactivo(
        callbackOK: (Boolean) -> Unit,
        callbackError: (String) -> Unit
    ) {

        dbFirebase.collection("arrendatario").whereEqualTo("activo",true)
            .get()
            .addOnSuccessListener { res ->
                if (res.size()>0){
                    res.forEach { item ->
                        dbFirebase.collection("arrendatario").document(item.id)
                            .update("activo", false)
                    }
                }
                callbackOK(true)
            }
            .addOnFailureListener {
                callbackError(it.message!!)
            }

    }

    private fun imageUpload(
        mUri: Uri, pathReference: String, id: String, callbackOK: (String) -> Unit,
        callbackError: (String) -> Unit
    ) {
        val folder: StorageReference = FirebaseStorage.getInstance().reference.child(pathReference)
        val fileName: StorageReference = folder.child("img$id")

        fileName.putFile(mUri).addOnSuccessListener {
            fileName.downloadUrl.addOnSuccessListener { uri ->
                callbackOK(uri.toString())
            }
        }.addOnFailureListener {
            callbackError(it.message!!)
        }
    }
}