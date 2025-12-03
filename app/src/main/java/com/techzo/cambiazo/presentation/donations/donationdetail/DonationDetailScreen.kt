package com.techzo.cambiazo.presentation.donations.donationdetail
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Public
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.MainScaffoldApp
import com.techzo.cambiazo.common.components.ProfileImage
import com.techzo.cambiazo.domain.AccountNumber
import com.techzo.cambiazo.domain.OngDetail
import com.techzo.cambiazo.domain.Project



@Composable
fun DonationDetailScreen(
    back: () -> Unit,
    ongDetailsViewModel: DonationDetailViewModel = hiltViewModel(),
) {
    val ongState = ongDetailsViewModel.ong.value
    val ong = ongState.data

    MainScaffoldApp(
        paddingCard = PaddingValues(top = 20.dp),
        contentsHeader = {
            ButtonIconHeaderApp(Icons.Filled.ArrowBack, onClick = { back() })
        },
        profileImage = {
            ProfileImage(
                url = ong?.logo ?: "",
                shape = CircleShape,
                size = 120.dp)
        },
        content = {
            when {
                ongState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                ong != null -> {
                    Spacer(Modifier.padding(vertical = 10.dp))
                    OngDetailModernCard(ong)
                }
                else -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(ongState.message)
                    }
                }
            }
        }
    )
}

@Composable
fun OngDetailModernCard(ong: OngDetail) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val addressParts = ong.address.split(",")
    val distrito = addressParts.getOrNull(1)?.trim() ?: ""
    val ciudad = addressParts.getOrNull(2)?.trim() ?: ""


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .verticalScroll(scrollState),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            Modifier
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = ong.name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF222222)
            )
            Text(
                text = ong.type,
                fontSize = 15.sp,
                color = Color(0xFF8B8B8B),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF24A19C),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$distrito, $ciudad",
                    fontSize = 14.sp,
                    color = Color(0xFF8B8B8B),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            SectionTitleOng("Sobre Nosotros")
            Text(ong.aboutUs, fontSize = 16.sp, color = Color(0xFF333333))
            Spacer(modifier = Modifier.height(14.dp))

            SectionTitleOng("Misión y Visión")
            Text(ong.missionAndVision, fontSize = 16.sp, color = Color(0xFF333333))
            Spacer(modifier = Modifier.height(14.dp))

            SectionTitleOng("Formas de Apoyo")
            Text(ong.supportForm, fontSize = 16.sp, color = Color(0xFF333333))
            Spacer(modifier = Modifier.height(14.dp))

            SectionTitleOng("Contacto")
            ContactBlockOng(ong, onWebsiteClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ong.website))
                context.startActivity(intent)
            })
            Spacer(modifier = Modifier.height(14.dp))

            SectionTitleOng("Categoría")
            Text(ong.category.name, fontSize = 16.sp, color = Color(0xFF333333))

            if (ong.projects.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                SectionTitleOng("Proyectos")
                ong.projects.forEach { project ->
                    ProjectDetailCard(project)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            if (ong.accounts.isNotEmpty()) {
                Spacer(modifier = Modifier.height(18.dp))
                SectionTitleOng("Cuentas Bancarias")
                ong.accounts.forEach { acc ->
                    AccountDetailCard(acc)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

    }
}

@Composable
fun SectionTitleOng(text: String) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = text,
            fontSize = 17.sp,
            color = Color(0xFF24A19C),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 7.dp, bottom = 3.dp)
        )
        Divider(
            Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color(0xFFF2F2F2))
        )
        Spacer(Modifier.height(2.dp))
    }
}

@Composable
fun ContactBlockOng(ong: OngDetail, onWebsiteClick: () -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Text("Dirección: ${ong.address}", fontSize = 15.sp, color = Color(0xFF333333))
        Text("Email: ${ong.email}", fontSize = 15.sp, color = Color(0xFF333333))
        Text("Teléfono: ${ong.phone}", fontSize = 15.sp, color = Color(0xFF333333))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Public,
                contentDescription = null,
                tint = Color(0xFF24A19C),
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Sitio web",
                fontSize = 15.sp,
                color = Color(0xFF24A19C),
                textDecoration = TextDecoration.Underline,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onWebsiteClick() }
            )
        }
        Text("Horario: ${ong.schedule}", fontSize = 15.sp, color = Color(0xFF333333))
    }
}

@Composable
fun ProjectDetailCard(project: Project) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9FB))
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(project.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF222222))
            Text(project.description, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun AccountDetailCard(acc: AccountNumber) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("Banco: ${acc.bankName}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF222222))
            Text("Cuenta: ${acc.accountNumber}", fontSize = 14.sp, color = Color.Gray)
            Text("Moneda: ${acc.currency}", fontSize = 14.sp, color = Color.Gray)
        }
    }
}