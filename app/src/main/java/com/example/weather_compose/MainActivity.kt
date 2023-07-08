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
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.weather_compose.model.CurrentWeatherResponse
import com.example.weather_compose.ui.theme.WeatherComposeTheme
import com.example.weather_compose.viewModel.MainViewModel

class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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
                    modifier = Modifier.fillMaxSize().background(brush = Brush.verticalGradient(listOf(Color.Blue.copy(0.25F),Color.White))),
                    color = Color.Blue.copy(0.2f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
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
                                /*.border(
                                    border = BorderStroke(1.dp, Color.Blue.copy(0.5F)),
                                    shape = RoundedCornerShape(12.dp)
                                ),*/
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
                            Button(
                                onClick = {
                                    if (cityInput.value.isNotBlank()) {
                                        mainViewModel.getWeatherData(cityInput.value.trim())
                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Enter the city please!!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Blue.copy(0.35F)
                                ),
                                elevation = ButtonDefaults.buttonElevation(10.dp)
                            ) {
                                Text(text = "Get Weather")
                            }
                        } // Row

                        mainViewModel.isLoading.observeAsState().value.let { isLoading ->
                            if (isLoading == true) CircularProgressIndicator()
                        }

                        mainViewModel.isError.observeAsState().value.let { isError ->
                            if (isError == true) Text(mainViewModel.errorMessage)
                        }

                        mainViewModel.weatherData.observeAsState().value.let { data ->
                            SetResultText(weatherData = data)
                        }
                    }
                }
            }
        } // setContent
    }

    @Composable
    private fun SetResultText(weatherData: CurrentWeatherResponse?) {
        Column(
            modifier = Modifier.padding(horizontal = 5.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (weatherData != null) {
                weatherData.current.let { current ->
                    Row(modifier = Modifier.height(200.dp)) {
                        Text(
                            text = "${current?.tempC}\u2103",
                            fontSize = 50.sp,
                            modifier = Modifier.weight(1F)
                        )
                        current?.condition.let { condition ->
                            Column(modifier = Modifier.weight(1F)) {
                                SetResultImage(condition?.icon)
                                Text("${condition?.text}")
                            }
                        }
                    }
                    /*Text(
                        text = "${current?.tempF}\u2109",
                        fontSize = 20.sp
                    )*/
                }

                weatherData.location.let { location ->
                    Text("${location?.name}, ${location?.region}, ${location?.country}")
                    Text("Timezone: ${location?.tzId}")
                    Text("Local Time: ${location?.localtime}")
                    Text("${location?.lat}:${location?.lon}")
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
                        .size(160.dp)
                        .clip(RoundedCornerShape(10)),
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }

    /*@OptIn(ExperimentalMaterial3Api::class)
    @Preview(showBackground = true)
    @Composable
    fun Previews() {
        WeatherComposeTheme {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.padding(bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = "",
                        onValueChange = { },
                        modifier = Modifier
                            .weight(1F)
                            .background(color = Color.White, shape = RoundedCornerShape(10.dp))
                            .shadow(elevation = 2.dp, shape = RoundedCornerShape(10.dp))
                            .clip(shape = RoundedCornerShape(10.dp)),
                        /*.border(
                            border = BorderStroke(1.dp, Color.Blue.copy(0.5F)),
                            shape = RoundedCornerShape(12.dp)
                        ),*/
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
                        //trailingIcon = if (cityInput.value.isNotEmpty()) trailingIconView else null,
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
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Blue.copy(0.35F)
                        ),
                        elevation = ButtonDefaults.buttonElevation(10.dp)
                    ) {
                        Text(text = "Get Weather")
                    }
                } // Row
            }
        }
    } // Row*/
}
