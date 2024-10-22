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
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.presentation.profile.editprofile.EditProfileViewModel
import com.techzo.cambiazo.presentation.profile.editprofile.uploadImageToFirebase
import java.util.UUID

@Composable
fun ImageUploadDialog(
    onDismiss: () -> Unit,
    onImageUploaded: (String) -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
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

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .background(Color.White, shape = RoundedCornerShape(25.dp))
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Cambio de imagen de perfil",
                color = Color.Black,
                textAlign = TextAlign.Center,
                style = TextStyle(fontSize = 30.sp, color = Color.Black),
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.padding(8.dp))

            imageBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(100.dp)),
                    contentScale = ContentScale.Crop
                )
            } ?: Image(
                painter = rememberImagePainter("https://png.pngtree.com/element_our/20190601/ourlarge/pngtree-file-upload-icon-image_1344464.jpg"),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(100.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.padding(8.dp))

            ButtonApp(
                onClick = { launcher.launch("image/*") },
                text = "Choose Image"
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Column {
                ButtonApp(
                    text = "Accept",
                    onClick = {
                        fileUri?.let { uri ->
                            uploadImageToFirebase(
                                context = context,
                                fileUri = uri,
                                onSuccess = { imageUrl ->
                                    onImageUploaded(imageUrl)
                                    viewModel.onProfilePictureChanged(imageUrl)
                                    onDismiss()
                                },
                                onFailure = {
                                }
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                ButtonApp(
                    text = "Cancel",
                    onClick = onDismiss
                )
            }
        }
    }
}