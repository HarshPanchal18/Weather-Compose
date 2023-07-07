package com.example.weather_compose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
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
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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
                            OutlinedTextField(
                                value = cityInput.value,
                                onValueChange = { cityInput.value = it },
                                modifier = Modifier.weight(1F),
                                label = { Text("Enter city") },
                                singleLine = true,
                                isError = cityInput.value.isBlank(),
                                leadingIcon = { Icon(Icons.Default.Place, null) },
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
                                            "Field can't be blank",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                            ) {
                                Text(text = "Get Weather")
                            }
                        } // Row

                        mainViewModel.isLoading.observeAsState().value.let { isLoading ->
                            if (isLoading == true) Text("Loading...")
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
        }
    }

    @Composable
    private fun SetResultText(weatherData: CurrentWeatherResponse?) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            //Text("Result:")
            weatherData?.location.let { location ->
                Text("Name: ${location?.name}\n")
                Text("Region: ${location?.region}\n")
                Text("Country: ${location?.country}\n")
                Text("Timezone ID: ${location?.tzId}\n")
                Text("Local Time: ${location?.localtime}\n")
            }

            weatherData?.current.let { current ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1F),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                        current?.condition.let { condition ->
                            SetResultImage(condition?.icon)
                            Text("Condition: ${condition?.text}\n")
                        }
                    }
                    Text("${current?.tempC}\u2103\t")
                    Text("${current?.tempF}\u2109")
                }
            }
        }

    }

    @Composable
    private fun SetResultImage(imageURL: String?) {
        imageURL?.let { url ->
            val painter = rememberAsyncImagePainter("https:$url")
            Image(
                painter = painter, contentDescription = null,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}
