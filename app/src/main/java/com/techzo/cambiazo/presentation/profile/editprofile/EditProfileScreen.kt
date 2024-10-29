package com.techzo.cambiazo.presentation.profile.editprofile

import ImageUploadDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skydoves.landscapist.glide.GlideImage
import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.components.ButtonApp
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.DialogApp
import com.techzo.cambiazo.common.components.FieldTextApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.SubTitleText
import com.techzo.cambiazo.common.components.TextTitleHeaderApp
import com.techzo.cambiazo.presentation.profile.ProfileOption
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun EditProfileScreen(
    deleteAccount: () -> Unit = {},
    back: () -> Unit = {},
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }

    val state = viewModel.state.value

    val username = viewModel.username.value
    val name = viewModel.name.value
    val phoneNumber = viewModel.phoneNumber.value
    val profilePicture = viewModel.profilePicture.value

    if (showDialog) {
        ImageUploadDialog(
            onDismiss = { showDialog = false },
            onImageUploaded = { imageUrl ->
                viewModel.onProfilePicture(imageUrl)
                viewModel.saveProfile()
            },
            viewModel = viewModel
        )
    }

    if (showDeleteDialog) {
        DialogApp(
            message = "Confirmación",
            description = "¿Está seguro de que desea eliminar tu cuenta?",
            labelButton1 = "Aceptar",
            labelButton2 = "Cancelar",
            onDismissRequest = { showDeleteDialog = false },
            onClickButton1 = {
                viewModel.deleteAccount()
                showDeleteDialog = false
                deleteAccount()
            },
            onClickButton2 = { showDeleteDialog = false }
        )
    }

    if (showSaveDialog) {
        DialogApp(
            message = "Éxito",
            description = "Cambios guardados exitosamente.",
            labelButton1 = "Aceptar",
            onDismissRequest = {
                showSaveDialog = false
                back()
            },
            onClickButton1 = {
                showSaveDialog = false
                back()
            }
        )
    }

    LaunchedEffect(state.data) {
        state.data?.let { user ->
            viewModel.onUsernameChange(user.username)
            viewModel.onNameChange(user.name)
            viewModel.onPhoneNumberChange(user.phoneNumber)
            viewModel.onProfilePicture(user.profilePicture)
        }
    }

    MainScaffoldApp(
        paddingCard = PaddingValues(top = 20.dp),
        contentsHeader = {
            Column(
                Modifier
                    .padding(bottom = 30.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ButtonIconHeaderApp(Icons.Filled.ArrowBack, onClick = { back() })
                TextTitleHeaderApp("Editar Perfil")
            }
        },
        content = {
            Column(modifier = Modifier.padding(horizontal = 25.dp)) {
                if (state.data != null) {
                    val user = state.data!!
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp, bottom = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box {
                            GlideImage(
                                imageModel = { profilePicture ?: user.profilePicture },
                                modifier = Modifier
                                    .size(140.dp)
                                    .clip(CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(35.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black)
                                    .clickable { showDialog = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Change Profile Image",
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        SubTitleText(subTittle = "Nombre")
                        FieldTextApp(name, "Nombre", onValueChange = { viewModel.onNameChange(it) })
                        Spacer(modifier = Modifier.height(20.dp))

                        SubTitleText(subTittle = "Correo electrónico")
                        FieldTextApp(username, "Correo electrónico", onValueChange = { viewModel.onUsernameChange(it) })
                        Spacer(modifier = Modifier.height(20.dp))

                        SubTitleText(subTittle = "Número de  teléfono")
                        FieldTextApp(phoneNumber, "Número de  teléfono", onValueChange = { viewModel.onPhoneNumberChange(it) })
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    ButtonApp("Guardar Cambios", onClick = { viewModel.saveProfile() })


                    val dateFormat = SimpleDateFormat("d MMM yyyy", Locale("es", "ES"))
                    val formattedDate = dateFormat.format(user.createdAt)

                    Row(
                        modifier = Modifier.padding(top = 10.dp),
                    ) {
                        Text(
                            text = "En Cambiazo desde ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                        Text(
                            text = formattedDate,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    HorizontalDivider(color = Color(0xFFF2F2F2), thickness = 1.5.dp)

                    ProfileOption(
                        icon = Icons.Filled.Delete,
                        text = "Eliminar Cuenta",
                        onClick = {
                            showDeleteDialog = true
                        }
                    )
                } else {
                    Text(text = state.message, fontSize = 16.sp)
                }
            }
        }
    )
}

