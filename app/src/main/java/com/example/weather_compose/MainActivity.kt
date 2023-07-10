package com.example.weather_compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.weather_compose.model.CurrentWeatherResponse
import com.example.weather_compose.ui.theme.WeatherComposeTheme
import com.example.weather_compose.viewModel.MainViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            rememberSystemUiController().setStatusBarColor(color = Color.White)
            WeatherComposeTheme {
                val cityInput = remember { mutableStateOf("") }
                val trailingIconView = @Composable {
                    IconButton(onClick = { cityInput.value = "" }) {
                        Icon(Icons.Default.Clear, null, tint = Color.Black)
                    }
                }
                val roundShape = RoundedCornerShape(10.dp)

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.onSurface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.Blue.copy(0.25F),
                                        Color(0xFF00A5FD),
                                        Color(0xFF4EB8F1),
                                        Color.White
                                    )
                                )
                            )
                            .padding(10.dp)
                        //horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.padding(bottom = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = cityInput.value,
                                onValueChange = { cityInput.value = it },
                                modifier = Modifier
                                    .weight(1F)
                                    .background(color = Color.White, shape = roundShape)
                                    .shadow(elevation = 2.dp, shape = roundShape)
                                    .clip(shape = roundShape),
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color.LightGray.copy(0.20f),
                                    cursorColor = Color.Blue.copy(0.35F),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    errorIndicatorColor = Color.Transparent,
                                ),
                                placeholder = { Text("Enter City") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = Color.Green.copy(0.6F)
                                    )
                                },
                                trailingIcon = if (cityInput.value.isNotEmpty()) trailingIconView else null,
                                keyboardOptions = KeyboardOptions(
                                    autoCorrect = true,
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Search
                                ),
                                textStyle = TextStyle(
                                    color = Color.Black,
                                    fontSize = TextUnit.Unspecified,
                                    fontFamily = FontFamily.SansSerif
                                ),
                            )
                            Spacer(Modifier.width(10.dp))
                            GetWeatherButton(city = cityInput.value)
                        } // Row

                        mainViewModel.isLoading.observeAsState().value.let { isLoading ->
                            if (isLoading == true)
                                Box(modifier = Modifier.fillMaxSize()) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                }
                        }

                        mainViewModel.isError.observeAsState().value.let { isError ->
                            if (isError == true) Text(mainViewModel.errorMessage)
                        }

                        mainViewModel.weatherData.observeAsState().value.let { data ->
                            SetResultText(weatherData = data)
                        }
                    }
                } // Surface
            }
        } // setContent
    }

    @Composable
    fun GetWeatherButton(city: String) {
        Button(
            onClick = {
                if (city.isNotBlank()) {
                    mainViewModel.getWeatherData(city.trim())
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Enter the city please!!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue.copy(0.35F),
                disabledContainerColor = Color.LightGray
            ),
            enabled = city.isNotEmpty(),
            elevation = ButtonDefaults.buttonElevation(10.dp)
        ) { Text(text = "Get Weather") }
    }

    @Composable
    private fun SetResultText(weatherData: CurrentWeatherResponse?) {
        Column(
            modifier = Modifier.padding(horizontal = 5.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (weatherData != null) {
                weatherData.location.let { location ->
                    Text(
                        text = "📍 ${location?.name}, ${location?.region}, ${location?.country}",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "\uD83C\uDF10 ${location?.tzId}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                    )
                    Text(
                        text = "⌚ ${location?.localtime}",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                    )
                }

                weatherData.current.let { current ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${current?.tempC} \u2103",
                            fontSize = 70.sp,
                            color = Color.White,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.ExtraBold
                        )
                        current?.condition.let { condition ->
                            SetResultImage(condition?.icon)
                            Text(
                                text = "${condition?.text}\n",
                                color = Color.White,
                                fontSize = 25.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } // Column

                    Text(
                        text = "🌀 ${getWindDirection(current?.windDir)}, ${current?.windMph ?: "N/A"} mph (${current?.windKph ?: "N/A"} kmph)",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                    )
                    Text(
                        text = "💧 ${current?.humidity}%",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                    )
                    Text(
                        text = "🔦 ${current?.visMiles ?: "N/A"} miles (${current?.visKm ?: "N/A"} kms)",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                    )
                }
            } // if
        } // Column
    }

    @Composable
    private fun SetResultImage(imageURL: String?) {
        Box {
            imageURL?.let { url ->
                val painter = rememberAsyncImagePainter("https:$url")
                Image(
                    painter = painter, contentDescription = "Weather Photo",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(180.dp)
                        .clip(RoundedCornerShape(10)),
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }

    private fun getWindDirection(direction: String?): String {
        return when (direction) {
            "N" -> "North" // 0, 360
            "NNE" -> "North-Northeast" // 22.5
            "NE" -> "Northeast" // 45
            "ENE" -> "East-Northeast" // 67.5
            "E" -> "East" // 90
            "ESE" -> "East-Southeast" // 112.5
            "SE" -> "Southeast" // 135
            "SSE" -> "South-Southeast" // 157.5
            "S" -> "South" // 180
            "SSW" -> "South-Southwest" // 202.5
            "SW" -> "Southwest" // 225
            "WSW" -> "West-Southwest" // 247.5
            "W" -> "West" // 270
            "WNW" -> "West-Northwest" // 292.5
            "NW" -> "Northwest" // 315
            "NNW" -> "North-Northwest" // 337.5
            else -> "N/A"
        }
    }
}
