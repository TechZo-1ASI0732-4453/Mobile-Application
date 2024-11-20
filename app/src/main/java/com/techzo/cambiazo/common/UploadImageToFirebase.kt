package com.techzo.cambiazo.common
import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.util.UUID

suspend fun uploadImageToFirebase(
    context: Context,
    fileUri: Uri,
    path: String,
    onSuccess: suspend (String) -> Unit,
    onFailure: suspend () -> Unit,
    onUploadStateChange: suspend (Boolean) -> Unit
) {
    val uuid = UUID.randomUUID().toString()
    val ref: StorageReference = FirebaseStorage.getInstance().reference.child("$path/$uuid")

    // Cambia el estado de carga a true antes de comenzar
    onUploadStateChange(true)

    try {
        ref.putFile(fileUri).await()
        val downloadUri = ref.downloadUrl.await()

        onUploadStateChange(false)
        onSuccess(downloadUri.toString())
    } catch (e: Exception) {
        onUploadStateChange(false)
        Toast.makeText(context, "Error al subir la imagen...", Toast.LENGTH_LONG).show()
        onFailure()
    }
}

suspend fun deleteImageFromFirebase(
    imageUrl: String,
    onSuccess: suspend () -> Unit,
    onFailure: suspend () -> Unit
) {
    val storageReference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)

    try {
        storageReference.delete().await()
        onSuccess()
    } catch (e: Exception) {

        onFailure()
    }
}