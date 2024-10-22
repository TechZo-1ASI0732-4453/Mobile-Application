import android.net.Uri
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.techzo.cambiazo.presentation.profile.editprofile.EditProfileViewModel
import java.util.UUID

@Composable
fun ImageUploadDialog(
    onDismiss: () -> Unit,
    onImageUploaded: (String) -> Unit,
    viewModel: EditProfileViewModel= hiltViewModel()
) {
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        fileUri = uri
        uri?.let {
            val bitmap = android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            imageBitmap = bitmap
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Upload Image") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = { launcher.launch("image/*") }) {
                    Text(text = "Choose Image")
                }

                Spacer(modifier = Modifier.height(16.dp))

                imageBitmap?.let {
                    Image(bitmap = it.asImageBitmap(), contentDescription = null, modifier = Modifier.size(200.dp))
                }
            }
        },
        confirmButton = {
            val uuid = UUID.randomUUID().toString()
            Button(onClick = {
                fileUri?.let { uri ->
                    val ref: StorageReference = FirebaseStorage.getInstance().reference.child("images/$uuid")
                    val progressDialog = android.app.ProgressDialog(context).apply {
                        setTitle("Uploading Image...")
                        setMessage("Processing...")
                        show()
                    }

                    ref.putFile(uri).addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener { downloadUri ->
                            progressDialog.dismiss()
                            val imageUrl = downloadUri.toString()
                            onImageUploaded(imageUrl)
                            viewModel.onProfilePictureChanged(imageUrl)
                            onDismiss()
                        }
                    }.addOnFailureListener {
                        progressDialog.dismiss()
                        Toast.makeText(context, "File Upload Failed...", Toast.LENGTH_LONG).show()
                    }
                }
            }) {
                Text(text = "Accept")
            }
        },

        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}