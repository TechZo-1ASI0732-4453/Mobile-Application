package com.techzo.cambiazo.presentation.auth.register.TyC

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techzo.cambiazo.common.components.ButtonIconHeaderApp
import com.techzo.cambiazo.common.components.MainScaffoldApp

@Composable
fun TermsAndConditionsScreen(back: () -> Unit) {
    MainScaffoldApp(
        paddingCard = PaddingValues(horizontal = 20.dp, vertical = 15.dp),
        contentsHeader = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                ButtonIconHeaderApp(
                    iconVector = Icons.Filled.ArrowBack,
                    onClick = { back() },
                    iconSize = 35.dp,
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 20.dp)
                ) {
                    Text(
                        text = "Acuerdo de Servicio",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "(SaaS)",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )  {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    "Última actualización: 01/07/2025",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Justify
                )
                Text(
                    "Bienvenido a CambiaZo, una plataforma que permite intercambiar o donar objetos de forma voluntaria, promoviendo la economía circular y evitando el desperdicio. Al usar CambiaZo aceptas estos términos y condiciones. Si no estás de acuerdo, por favor no utilices la plataforma.",
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Justify
                )
            }

            item {
                Text("1. Objeto del Servicio", fontWeight = FontWeight.Bold, textAlign = TextAlign.Justify)
                AnnotatedText(
                    boldText = "1.1. ",
                    normalText = "CambiaZo ofrece un espacio digital donde los usuarios pueden publicar objetos que ya no necesitan para que otros los intercambien o reciban como donación, siempre de manera voluntaria y sin fines de lucro.",
                    textAlign = TextAlign.Justify
                )
            }

            item {
                Text("2. Obligaciones del Usuario", fontWeight = FontWeight.Bold, textAlign = TextAlign.Justify)
                AnnotatedText(
                    boldText = "2.1. ",
                    normalText = "Los usuarios deben brindar información veraz y actualizada, usar la plataforma de forma ética y responsable, cumplir con la legislación vigente y no publicar contenido prohibido, ilegal u ofensivo.",
                    textAlign = TextAlign.Justify
                )
            }

            item {
                Text("3. Obligaciones de CambiaZo", fontWeight = FontWeight.Bold, textAlign = TextAlign.Justify)
                AnnotatedText(
                    boldText = "3.1. ",
                    normalText = "CambiaZo se compromete a ofrecer acceso ininterrumpido (salvo mantenimiento o fuerza mayor), proteger la privacidad de los usuarios, brindar soporte básico y notificar cambios importantes en el servicio o términos.",
                    textAlign = TextAlign.Justify
                )
            }

            item {
                Text("4. Restricciones de Uso", fontWeight = FontWeight.Bold, textAlign = TextAlign.Justify)
                AnnotatedText(
                    boldText = "4.1. ",
                    normalText = "Está prohibido usar CambiaZo para fines comerciales, emplear técnicas automatizadas, o compartir contenido ilegal, violento o discriminatorio que perjudique la experiencia en la plataforma.",
                    textAlign = TextAlign.Justify
                )
            }

            item {
                Text("5. Propiedad Intelectual", fontWeight = FontWeight.Bold, textAlign = TextAlign.Justify)
                AnnotatedText(
                    boldText = "5.1. ",
                    normalText = "El contenido publicado por los usuarios sigue siendo de su propiedad. Sin embargo, al publicarlo en CambiaZo, se otorga una licencia limitada para mostrarlo y distribuirlo dentro de la plataforma y permitir que otros lo vean e interactúen.",
                    textAlign = TextAlign.Justify
                )
            }

            item {
                Text("6. Modificaciones del Servicio", fontWeight = FontWeight.Bold, textAlign = TextAlign.Justify)
                AnnotatedText(
                    boldText = "6.1. ",
                    normalText = "CambiaZo puede actualizar o cambiar sus servicios y términos en cualquier momento, notificando a los usuarios por medios electrónicos. El uso continuo implica la aceptación de dichos cambios.",
                    textAlign = TextAlign.Justify
                )
            }

            item {
                Text("7. Terminación de Cuentas", fontWeight = FontWeight.Bold, textAlign = TextAlign.Justify)
                AnnotatedText(
                    boldText = "7.1. ",
                    normalText = "CambiaZo puede suspender o eliminar cuentas que incumplan estos términos, presenten información falsa, contenido prohibido o afecten el funcionamiento normal de la plataforma.",
                    textAlign = TextAlign.Justify
                )
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    "Gracias por usar CambiaZo. Si tienes alguna duda sobre este acuerdo, puedes contactarnos.",
                    modifier = Modifier.padding(top = 16.dp),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Justify
                )
            }
        }
    }
}

@Composable
fun AnnotatedText(boldText: String, normalText: String, textAlign: TextAlign) {
    Text(
        buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(boldText)
            }
            withStyle(style = SpanStyle(color = Color.Gray)) {
                append(normalText)
            }
        },
        modifier = Modifier.padding(bottom = 8.dp),
        textAlign = textAlign
    )
}