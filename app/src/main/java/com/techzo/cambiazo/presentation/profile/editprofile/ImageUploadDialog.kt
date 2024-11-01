import android.net.Uri
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.deleteImageFromFirebase
import com.techzo.cambiazo.presentation.profile.editprofile.EditProfileViewModel
import com.techzo.cambiazo.common.uploadImageToFirebase

@Composable
fun ImageUploadDialog(
    onDismiss: () -> Unit,
    onImageUploaded: (String) -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    var fileUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        fileUri = uri
        uri?.let {
            val bitmap = android.provider.MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            imageBitmap = bitmap
        }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .background(Color.White, shape = RoundedCornerShape(25.dp))
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Foto de Perfil",
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 32.sp, color = Color.Black),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.padding(15.dp))

                imageBitmap?.let {
                    Box {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            modifier = Modifier
                                .size(200.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        if (!isUploading) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.8f))
                                    .clickable {
                                        fileUri = null
                                        imageBitmap = null
                                    },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Change Profile Image",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                } ?: Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .clickable { launcher.launch("image/*") }
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        drawRoundRect(
                            color = Color(0xFF888888),
                            style = Stroke(
                                width = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(25f, 25f), 0f)
                            ),
                            cornerRadius = CornerRadius(100.dp.toPx())
                        )
                    }
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Upload,
                            contentDescription = "Upload Icon",
                            modifier = Modifier.size(60.dp),
                            tint = Color(0xFF888888)
                        )
                        Text(
                            text = "Sube tu foto",
                            color = Color(0xFF888888),
                            style = TextStyle(fontSize = 16.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.padding(15.dp))

                Column {
                    if (isUploading) {
                        LinearProgressIndicator(
                            color = Color(0xFFFFD146),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        ButtonApp(
                            text = "Aceptar",
                            onClick = {
                                fileUri?.let { uri ->
                                    viewModel.imageToUploadFromFirebase(uri,context,isUpload = {isUploading = it}, onDismiss = onDismiss)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        ButtonApp(
                            text = "Cancelar",
                            bgColor = Color.White,
                            fColor = Color(0xFFFFD146),
                            onClick = onDismiss
                        )
                    }
                }
            }
        }
    }
}