package com.techzo.cambiazo.presentation.register

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
                        text = "Términos y",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Condiciones",
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
                    "Última actualización: 21/10/2024",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Justify
                )
                Text(
                    "Bienvenido a TechZo y su aplicación CambiaZo. Al usar nuestra aplicación, usted acepta cumplir con los siguientes términos y condiciones. Estos términos son importantes tanto para usted como para nosotros, ya que están diseñados para crear un entorno seguro, justo y legal.",
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Justify
                )
            }

            item {
                Text(
                    "1. Uso de la Aplicación",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Justify
                )
                AnnotatedText(
                    boldText = "1.1. Objetivo:",
                    normalText = " La Aplicación está diseñada para ayudarte a intercambiar y donar objetos que ya no utilizas y deseas darle una segunda vida.",
                    textAlign = TextAlign.Justify
                )
                AnnotatedText(
                    boldText = "1.2. Uso Apropiado:",
                    normalText = " Te comprometes a utilizar la Aplicación de manera legal, ética y apropiada. No debes utilizar la Aplicación de manera que viole la ley o los derechos de terceros.",
                    textAlign = TextAlign.Justify
                )
            }

            item {
                Text(
                    "2. Privacidad y Datos",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Justify
                )
                AnnotatedText(
                    boldText = "2.1. Privacidad del Usuario:",
                    normalText = " Respetamos tu privacidad. La información personal que proporciones a la aplicación se regirá por nuestra Política de Privacidad. Al utilizar la aplicación, consientes la recopilación y el uso de datos de acuerdo con nuestra Política de Privacidad.",
                    textAlign = TextAlign.Justify
                )
                AnnotatedText(
                    boldText = "2.2. Control de Datos:",
                    normalText = " Tienes el control de tus datos personales y la capacidad de configurar la privacidad en la Aplicación, incluyendo la revocación de permisos para acceder a la cámara y la ubicación.",
                    textAlign = TextAlign.Justify
                )
            }

            item {
                Text(
                    "3. Propiedad Intelectual",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Justify
                )
                AnnotatedText(
                    boldText = "3.1. Derechos de Autor:",
                    normalText = " La aplicación y su contenido, incluyendo pero no limitado a texto, imágenes, logotipos y marcas registradas, están protegidos por derechos de autor y otras leyes de propiedad intelectual. No tienes permiso para copiar, modificar, distribuir o reproducir dicho contenido sin autorización.",
                    textAlign = TextAlign.Justify
                )
            }

            item {
                Text(
                    "4. Actualizaciones y Cambios",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Justify
                )
                AnnotatedText(
                    boldText = "4.1. Actualizaciones:",
                    normalText = " Podemos realizar actualizaciones y cambios en la aplicación de vez en cuando. Es responsabilidad del usuario mantener la aplicación actualizada para disfrutar de las últimas características y correcciones.",
                    textAlign = TextAlign.Justify
                )
            }

            item {
                Text(
                    "5. Terminación",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Justify
                )
                AnnotatedText(
                    boldText = "5.1. Terminación por el Usuario:",
                    normalText = " Puedes dejar de utilizar la aplicación en cualquier momento.",
                    textAlign = TextAlign.Justify
                )
                AnnotatedText(
                    boldText = "5.2. Terminación por Nosotros:",
                    normalText = " Nos reservamos el derecho de suspender o cancelar tu acceso a la aplicación en caso de incumplimiento de estos términos.",
                    textAlign = TextAlign.Justify
                )
            }

            item {
                Text(
                    "6. Disposiciones Finales",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Justify
                )
                AnnotatedText(
                    boldText = "6.1. Ley Aplicable:",
                    normalText = " Estos términos se rigen por las leyes del Perú y cualquier disputa se someterá a la jurisdicción de los tribunales del Perú.",
                    textAlign = TextAlign.Justify
                )
                AnnotatedText(
                    boldText = "6.2. Cambios en los Términos:",
                    normalText = " Nos reservamos el derecho de modificar estos términos en cualquier momento. Te notificaremos de cualquier cambio importante. Si continúas utilizando la Aplicación después de los cambios, se considerará que aceptas los nuevos términos.",
                    textAlign = TextAlign.Justify
                )
            }

            item {
                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    "Te agradecemos por utilizar CambiaZo. Si tienes alguna pregunta sobre estos términos, no dudes en contactarnos.",
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