package com.techzo.cambiazo.presentation.profile.editprofile
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

fun uploadImageToFirebase(
    context: Context,
    fileUri: Uri,
    onSuccess: (String) -> Unit,
    onFailure: () -> Unit
) {
    val uuid = UUID.randomUUID().toString()
    val ref: StorageReference = FirebaseStorage.getInstance().reference.child("images/$uuid")
    val progressDialog = ProgressDialog(context).apply {
        setTitle("Uploading Image...")
        setMessage("Processing...")
        show()
    }

    ref.putFile(fileUri).addOnSuccessListener {
        ref.downloadUrl.addOnSuccessListener { downloadUri ->
            progressDialog.dismiss()
            val imageUrl = downloadUri.toString()
            onSuccess(imageUrl)
        }
    }.addOnFailureListener {
        progressDialog.dismiss()
        Toast.makeText(context, "File Upload Failed...", Toast.LENGTH_LONG).show()
        onFailure()
    }
}