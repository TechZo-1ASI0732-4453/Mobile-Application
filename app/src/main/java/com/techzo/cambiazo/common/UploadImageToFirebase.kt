package com.techzo.cambiazo.common
import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

fun uploadImageToFirebase(
    context: Context,
    fileUri: Uri,
    path: String,
    onSuccess: (String) -> Unit,
    onFailure: () -> Unit,
    onUploadStateChange: (Boolean) -> Unit
) {
    val uuid = UUID.randomUUID().toString()
    val ref: StorageReference = FirebaseStorage.getInstance().reference.child("$path/$uuid")

    onUploadStateChange(true)

    ref.putFile(fileUri).addOnSuccessListener {
        ref.downloadUrl.addOnSuccessListener { downloadUri ->
            onUploadStateChange(false)
            val imageUrl = downloadUri.toString()
            onSuccess(imageUrl)
        }
    }.addOnFailureListener {
        onUploadStateChange(false)
        Toast.makeText(context, "File Upload Failed...", Toast.LENGTH_LONG).show()
        onFailure()
    }
}

fun deleteImageFromFirebase(
    imageUrl: String,
    onSuccess: () -> Unit,
    onFailure: () -> Unit
) {
    val storageReference: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)

    storageReference.delete().addOnSuccessListener {
        onSuccess()
    }.addOnFailureListener {
        onFailure()
    }
}