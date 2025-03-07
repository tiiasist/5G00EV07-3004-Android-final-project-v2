package com.example.vitrapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.vitrapp.ui.theme.VITRAppTheme
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.example.vitrapp.ui.theme.errorContainerLight
import com.example.vitrapp.ui.theme.primaryContainerLight
import com.example.vitrapp.ui.theme.secondaryLight
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.unit.times
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import android.Manifest
import android.content.Context
import android.media.ExifInterface
import android.provider.MediaStore
import android.util.Log

import androidx.core.content.ContextCompat


import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider

import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

const val BASE_URL = "https://www.euromelanoma.eu/"
//const val EUROMELANOMA_LOCATION = "en-intl/learn-about-skin-cancer/benign-lesion"
const val LATEST_PRICES_ENDPOINT = "v1/latest-prices.json"
const val API_MAIN_PAGE_URL = "https://www.porssisahko.net/api"


val dummyPrices = listOf(
    Price(5.874, "2024-12-13T07:00:00.000Z", "2024-12-13T08:00:00.000Z"),
    Price(9.457, "2024-12-13T06:00:00.000Z", "2024-12-13T07:00:00.000Z"),
    Price(0.2, "2024-12-13T05:00:00.000Z", "2024-12-13T06:00:00.000Z"),
    Price(10.899, "2024-12-13T04:00:00.000Z", "2024-12-13T05:00:00.000Z"),
    Price(3.172, "2024-12-13T03:00:00.000Z", "2024-12-13T04:00:00.000Z"),
    Price(11.0, "2024-12-13T02:00:00.000Z", "2024-12-13T03:00:00.000Z"),
    Price(0.2, "2024-12-13T01:00:00.000Z", "2024-12-13T02:00:00.000Z"),
    Price(9.113, "2024-12-13T00:00:00.000Z", "2024-12-13T01:00:00.000Z"),
    Price(3.533, "2024-12-12T23:00:00.000Z", "2024-12-13T00:00:00.000Z"),
    Price(3.533, "2024-12-12T22:00:00.000Z", "2024-12-12T23:00:00.000Z"),
    Price(-26.333, "2024-12-12T21:00:00.000Z", "2024-12-12T22:00:00.000Z"),
    Price(0.0, "2024-12-12T20:00:00.000Z", "2024-12-12T21:00:00.000Z"),
    Price(8.757, "2024-12-12T19:00:00.000Z", "2024-12-12T20:00:00.000Z"),
    Price(-10.58, "2024-12-12T18:00:00.000Z", "2024-12-12T19:00:00.000Z"),
    Price(14.761, "2024-12-12T17:00:00.000Z", "2024-12-12T18:00:00.000Z"),
    Price(0.0, "2024-12-12T16:00:00.000Z", "2024-12-12T17:00:00.000Z"),
    Price(25.104, "2024-12-12T15:00:00.000Z", "2024-12-12T16:00:00.000Z"),
    Price(-128.118, "2024-12-12T14:00:00.000Z", "2024-12-12T15:00:00.000Z"),
    Price(20.541, "2024-12-12T13:00:00.000Z", "2024-12-12T14:00:00.000Z"),
    Price(14.761, "2024-12-12T12:00:00.000Z", "2024-12-12T13:00:00.000Z"),
    Price(0.0, "2024-12-12T11:00:00.000Z", "2024-12-12T12:00:00.000Z"),
    Price(25.104, "2024-12-12T10:00:00.000Z", "2024-12-12T11:00:00.000Z"),
    Price(-28.118, "2024-12-12T09:00:00.000Z", "2024-12-12T10:00:00.000Z"),
    Price(20.541, "2024-12-12T08:00:00.000Z", "2024-12-12T09:00:00.000Z")
)

val dummyPrices2 = listOf(
    Price(3.85, "2024-12-13T07:00:00.000Z", "2024-12-13T08:00:00.000Z"),
    Price(4.72, "2024-12-13T06:00:00.000Z", "2024-12-13T07:00:00.000Z"),
    Price(3.45, "2024-12-13T05:00:00.000Z", "2024-12-13T06:00:00.000Z"),
    Price(5.03, "2024-12-13T04:00:00.000Z", "2024-12-13T05:00:00.000Z"),
    Price(3.62, "2024-12-13T03:00:00.000Z", "2024-12-13T04:00:00.000Z"),
    Price(5.2, "2024-12-13T02:00:00.000Z", "2024-12-13T03:00:00.000Z"),
    Price(3.45, "2024-12-13T01:00:00.000Z", "2024-12-13T02:00:00.000Z"),
    Price(4.64, "2024-12-13T00:00:00.000Z", "2024-12-13T01:00:00.000Z"),
    Price(3.85, "2024-12-12T23:00:00.000Z", "2024-12-13T00:00:00.000Z"),
    Price(3.85, "2024-12-12T22:00:00.000Z", "2024-12-12T23:00:00.000Z"),
    Price(3.45, "2024-12-12T21:00:00.000Z", "2024-12-12T22:00:00.000Z"),
    Price(3.45, "2024-12-12T20:00:00.000Z", "2024-12-12T21:00:00.000Z"),
    Price(4.38, "2024-12-12T19:00:00.000Z", "2024-12-12T20:00:00.000Z"),
    Price(3.45, "2024-12-12T18:00:00.000Z", "2024-12-12T19:00:00.000Z"),
    Price(4.92, "2024-12-12T17:00:00.000Z", "2024-12-12T18:00:00.000Z"),
    Price(3.45, "2024-12-12T16:00:00.000Z", "2024-12-12T17:00:00.000Z"),
    Price(5.08, "2024-12-12T15:00:00.000Z", "2024-12-12T16:00:00.000Z"),
    Price(3.45, "2024-12-12T14:00:00.000Z", "2024-12-12T15:00:00.000Z"),
    Price(4.85, "2024-12-12T13:00:00.000Z", "2024-12-12T14:00:00.000Z"),
    Price(4.92, "2024-12-12T12:00:00.000Z", "2024-12-12T13:00:00.000Z"),
    Price(3.45, "2024-12-12T11:00:00.000Z", "2024-12-12T12:00:00.000Z"),
    Price(5.08, "2024-12-12T10:00:00.000Z", "2024-12-12T11:00:00.000Z"),
    Price(3.45, "2024-12-12T09:00:00.000Z", "2024-12-12T10:00:00.000Z"),
    Price(4.85, "2024-12-12T08:00:00.000Z", "2024-12-12T09:00:00.000Z")
)

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        enableEdgeToEdge()
        setContent {
            VITRAppTheme {
                var userName by remember { mutableStateOf(sharedPreferences.getString("user_name", "") ?: "") } // Read the user name from the shared preferences

                VITRApp(
                    sharedPreferences = sharedPreferences,
                    userName = userName,
                    onUserNameChange = { newUserName -> // Update the user name
                        userName = newUserName
                        sharedPreferences.edit().putString("user_name", newUserName).apply()
                    }
                )
            }
        }
    }
}


@Composable
fun VITRApp(sharedPreferences: SharedPreferences, userName: String, onUserNameChange: (String) -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopBar(navController, sharedPreferences)
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        },
        content = { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "page1",
                modifier = Modifier.padding(innerPadding) // Apply the innerPadding here
            ) {
                composable("page1") {// Home
                    Page1()
                }
                composable("page2") {// Calculator
                    Page2()
                }
                composable("page3") {// Data
                    Page3()
                }
                composable("page4") {// User
                    Page4( userName, onUserNameChange)
                }
            }
        }
    )
}

// Top app bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavHostController, sharedPreferences: SharedPreferences) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val title = when (currentRoute) {
        "page1" -> stringResource(R.string.home_title)
        "page2" -> stringResource(R.string.calculator_page_title)
        "page3" -> stringResource(R.string.title_data_page)
        "page4" -> stringResource(R.string.title_user_page)
        else -> stringResource(R.string.app_name)
    }
    val userName = sharedPreferences.getString("user_name", "") ?: ""

    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    textAlign = TextAlign.Start,
                    fontSize = 25.sp,
                    modifier = Modifier.padding(start= 8.dp, top = 16.dp, end = 16.dp, bottom = 16.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 0.dp)
                ) {
                    if (userName.isNotEmpty()) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = "User",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .size(if (userName.length > 5) 12.dp else 24.dp)
                        )
                        Text(
                            text = userName,
                            fontSize = if (userName.length > 5) 10.sp else 15.sp,
                            textAlign = TextAlign.Center

                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            titleContentColor = MaterialTheme.colorScheme.surface
        )
    )
}

// Bottom navigation bar
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.inverseSurface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = stringResource(R.string.icon_home)) },
            label = { Text(stringResource(R.string.home_nav_button),
                color = MaterialTheme.colorScheme.surface) },
            selected = currentRoute == "page1",
            onClick = {
                navController.navigate("page1") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.surface
            )
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.baseline_calculate_24), contentDescription = stringResource(
                R.string.icon_calculate
            )
            ) },
            label = { Text(stringResource(R.string.counter_nav_button),
                color = MaterialTheme.colorScheme.surface) },
            selected = currentRoute == "page2",
            onClick = {
                navController.navigate("page2") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.surface
            )
        )
        NavigationBarItem(
            icon = { Icon(painter = painterResource(id = R.drawable.baseline_data_object_24), contentDescription = stringResource(
                R.string.icon_data
            )
            ) },
            label = { Text(stringResource(R.string.data_nav_button),
                color = MaterialTheme.colorScheme.surface) },
            selected = currentRoute == "page3",
            onClick = {
                navController.navigate("page3") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.surface
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = stringResource(R.string.icon_user)) },
            label = { Text(stringResource(R.string.user_nav_button),
                color = MaterialTheme.colorScheme.surface) },
            selected = currentRoute == "page4",
            onClick = {
                navController.navigate("page4") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}

// First page for home screen
@SuppressLint("DefaultLocale")
@Composable
fun Page1() {

    var prices: List<Price> by remember { mutableStateOf(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = RetrofitInstance.api.getPrices()
                prices = response.prices
                //prices = dummyPrices
                loading = false
            } catch (e: Exception) {
                error = e.message
                loading = false
            }
        }
    }

    if (loading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.loading_price_data_text))
        }
    } else if (error != null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Error: $error")
        }
    } else {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item{
                Text(
                    text = stringResource(R.string.cents_kwh_prices),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                )
            }
            item{
                Spacer(modifier = Modifier.height(16.dp))
            }
            item{
                BarChart(prices = prices)
            }
            item{
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Text(
                            text = stringResource(R.string.date_title_price_column),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Text(
                            text = stringResource(R.string.time_title_price_column),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                    ) {
                        Text(
                            text = stringResource(R.string.cents_kwh_title_price_column),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }


            items(prices) { price ->
                val zonedDateTime = ZonedDateTime.parse(price.startDate)
                val date = zonedDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                val time = zonedDateTime.format(DateTimeFormatter.ofPattern("HH.mm"))
                val priceInCents = String.format("%.2f", price.price)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 2.dp, end = 4.dp, bottom = 2.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            text = date,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            text = time,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            text = priceInCents,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(4.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}


data class Price(
    val price: Double,
    val startDate: String,
    val endDate: String
)

data class PriceResponse(
    val prices: List<Price>
)

interface ApiService {
    @GET(LATEST_PRICES_ENDPOINT)
    suspend fun getPrices(): PriceResponse
}

object RetrofitInstance {
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun BarChart(prices: List<Price>) {
    val currentTime = ZonedDateTime.now()
    val startTime = currentTime.minusHours(4)
    val endTime = startTime.plusHours(12)
    val filteredPrices = prices.filter {
        val priceTime = ZonedDateTime.parse(it.startDate)
        priceTime.isAfter(startTime) && priceTime.isBefore(endTime)
    }.sortedBy { ZonedDateTime.parse(it.startDate) }
    val maxPrice = filteredPrices.maxOfOrNull { it.price } ?: 0.0
    val minPrice = filteredPrices.minOfOrNull { it.price } ?: 0.0
    var selectedPrice by remember { mutableStateOf<Triple<Double, String, String>?>(null) }
    val currentTimeFormatted = currentTime.format(DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy"))
    val currentPrice = filteredPrices.find { ZonedDateTime.parse(it.startDate).hour == currentTime.hour }?.price ?: 0.0

    // Calculate the maximum bar height based on the maximum price
    val maxBarHeight = (maxPrice - minPrice).toFloat() * 10.dp

    Canvas(modifier = Modifier
        .padding(start = 40.dp, top = 16.dp, end = 16.dp, bottom = 32.dp)
        .fillMaxWidth()
        .height(maxBarHeight)
        .pointerInput(Unit) {
            detectTapGestures { offset ->
                val gap = 4.dp.toPx()
                val barWidth = (size.width - gap * (filteredPrices.size - 1)) / filteredPrices.size
                val index = (offset.x / (barWidth + gap)).toInt()
                if (index in filteredPrices.indices) {
                    val price = filteredPrices[index]
                    val dateTime = ZonedDateTime.parse(price.startDate)
                    val time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                    val date = dateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                    selectedPrice = Triple(price.price, time, date)
                }
            }
        }
    ) {
        val gap = 8.dp.toPx()
        val barWidth = (size.width - gap * (filteredPrices.size - 1)) / filteredPrices.size
        val yAxisInterval = (maxPrice) / 5
        val cornerRadius = 3.dp.toPx()
        val zeroLine = size.height * (maxPrice / (maxPrice - minPrice)).toFloat()

        // Draw the bars first
        filteredPrices.forEachIndexed { index, price ->
            val barHeight = ((price.price / (maxPrice - minPrice)) * size.height).toFloat()
            val xOffset = index * (barWidth + gap)
            var barColor = when {
                price.price < 7 -> errorContainerLight
                price.price < 14 -> primaryContainerLight
                else -> secondaryLight
            }

            // Change the color opacity if the bar corresponds to the current time
            val priceTime = ZonedDateTime.parse(price.startDate)
            if (priceTime.hour == currentTime.hour) {
                barColor = barColor.copy(alpha = 0.6f)
            }

            // Draw the filled bar with rounded corners
            drawRoundRect(
                color = barColor,
                topLeft = androidx.compose.ui.geometry.Offset(xOffset, zeroLine - barHeight),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
            )

            // Draw a thin border with rounded corners if the bar corresponds to the current time
            if (priceTime.hour == currentTime.hour) {
                drawRoundRect(
                    color = Color.Black,
                    topLeft = androidx.compose.ui.geometry.Offset(xOffset, zeroLine - barHeight),
                    size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
            }

            // Draw x-axis labels centered to the bars
            drawContext.canvas.nativeCanvas.drawText(
                ZonedDateTime.parse(price.startDate).format(DateTimeFormatter.ofPattern("HH")),
                xOffset + barWidth / 2,
                size.height + 12.sp.toPx(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 12.sp.toPx()
                }
            )
        }

        // Draw y-axis help lines and labels centered to the lines
        for (i in 0..5) {
            val y = size.height - ((i * yAxisInterval - minPrice) / (maxPrice - minPrice) * size.height).toFloat()
            drawLine(
                color = Color.Gray,
                start = androidx.compose.ui.geometry.Offset(0f, y),
                end = androidx.compose.ui.geometry.Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
            val label = when {
                0 + i * yAxisInterval == 0.0 -> "0"
                maxPrice >= 10 -> String.format("%.0f", 0 + i * yAxisInterval)
                else -> String.format("%.1f", 0 + i * yAxisInterval)
            }

            drawContext.canvas.nativeCanvas.drawText(
                label,
                -90f,
                y + 6.sp.toPx(), // Adjusted to center the label to the line
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLACK
                    textAlign = android.graphics.Paint.Align.CENTER
                    textSize = 12.sp.toPx()
                }
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Box(
        modifier = Modifier
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
        ,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = selectedPrice?.let { (price, time, date) ->
                stringResource(R.string.selected_spot_cents_kwh, time, date, price)
            } ?: stringResource(R.string.current_spot_cents_kwh, currentTimeFormatted, currentPrice),
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

// Second page for calculation of electricity bill
class Page2ViewModel : ViewModel() {
    var consumption by mutableStateOf("")
    var fixedPrice by mutableStateOf("")
    var yearlyCost by mutableStateOf<Double?>(null)
    var monthlyCost by mutableStateOf<Double?>(null)
    var roundedYearlyCost by mutableStateOf("")
    var roundedMonthlyCost by mutableStateOf("")
    var averageYearlyCost by mutableStateOf<Double?>(null)
    var averageMonthlyCost by mutableStateOf<Double?>(null)
    var roundedAverageYearlyCost by mutableStateOf("")
    var roundedAverageMonthlyCost by mutableStateOf("")
    var prices: List<Price> by mutableStateOf(emptyList())
    var loading by mutableStateOf(true)
    var error by mutableStateOf<String?>(null)

    init {
        fetchPrices()
    }

    private fun fetchPrices() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getPrices()
                prices = response.prices
                loading = false
            } catch (e: Exception) {
                error = e.message
                loading = false
            }
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun Page2( viewModel: Page2ViewModel = viewModel()) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // Input fields
            OutlinedTextField(
                value = viewModel.consumption,
                onValueChange = { viewModel.consumption = it },
                label = { Text(stringResource(R.string.energy_consumption_kwh_year)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.fixedPrice,
                onValueChange = { viewModel.fixedPrice = it },
                label = { Text(stringResource(R.string.fixed_price_cents_kwh)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(64.dp))
        }

        item {
            // Calculate costs
            val consumptionValue = viewModel.consumption.toDoubleOrNull()
            val fixedPriceValue = viewModel.fixedPrice.toDoubleOrNull()

            if (consumptionValue != null && fixedPriceValue != null) {
                viewModel.yearlyCost = consumptionValue * fixedPriceValue / 100
                viewModel.monthlyCost = viewModel.yearlyCost!! / 12

                viewModel.roundedYearlyCost = String.format("%.2f", viewModel.yearlyCost)
                viewModel.roundedMonthlyCost = String.format("%.2f", viewModel.monthlyCost)
            } else {
                viewModel.yearlyCost = null
                viewModel.monthlyCost = null
            }

            // Display calculated costs
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.fixed_price_title),
                    style = MaterialTheme.typography.titleLarge.copy(textDecoration = TextDecoration.Underline),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        if (viewModel.consumption.isEmpty() || viewModel.fixedPrice.isEmpty()) {
                            append(stringResource(R.string.input_both_values_to_calculate))
                        } else if (viewModel.yearlyCost == null || viewModel.monthlyCost == null) {
                            append(stringResource(R.string.invalid_input_values))
                        } else {
                            append(stringResource(R.string.yearly_cost))
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${viewModel.roundedYearlyCost} €")
                            }
                            append(stringResource(R.string.monthly_cost))
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("${viewModel.roundedMonthlyCost} €")
                            }
                        }
                    },
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            // Display fetched data status and average price calculation
            if (viewModel.loading) {
                Text(text = stringResource(R.string.loading_price_data), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            } else if (viewModel.error != null) {
                Text(text = "Error: ${viewModel.error}", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            } else {
                val averagePrice = viewModel.prices.map { it.price }.average()
                val roundedAveragePrice = String.format("%.2f", averagePrice)

                if (viewModel.consumption.isNotEmpty()) {
                    viewModel.averageYearlyCost = viewModel.consumption.toDoubleOrNull()?.let { it * averagePrice / 100 }
                    viewModel.averageMonthlyCost = viewModel.averageYearlyCost?.div(12)

                    viewModel.roundedAverageYearlyCost = viewModel.averageYearlyCost?.let { String.format("%.2f", it) } ?: ""
                    viewModel.roundedAverageMonthlyCost = viewModel.averageMonthlyCost?.let { String.format("%.2f", it) } ?: ""
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.errorContainer)
                        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.recent_spot_price),
                        style = MaterialTheme.typography.titleLarge.copy(textDecoration = TextDecoration.Underline),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(R.string.average_price_from_past_few_days))
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("$roundedAveragePrice ")
                            }
                            append(stringResource(R.string.cents_kwh))
                            if (viewModel.consumption.isNotEmpty()) {
                                append(stringResource(R.string.average_yearly_cost))
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("${viewModel.roundedAverageYearlyCost} €")
                                }
                                append(stringResource(R.string.average_monthly_cost))
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("${viewModel.roundedAverageMonthlyCost} €")
                                }
                            } else {
                                append(stringResource(R.string.input_your_consumption_to_calculate_average_costs))
                            }
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

// Third page for mole check picture
@Composable
fun Page3() {
    val BASE_URL_EURO = "https://www.euromelanoma.eu/"
    val EUROMELANOMA_LOCATION =
        stringResource(R.string.en_intl_learn_about_skin_cancer_benign_lesion)

    var errorMessage by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val cameraPermissionGranted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var imageUriTemp by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUriTemp?.let { uri ->
                imageUri = uri
                sharedPreferences.edit().putString("image_uri", uri.toString()).apply()
                /*
                // Reload image after taking picture
                loadImage(uri, context, onBitmapLoaded = { loadedBitmap ->
                    bitmap = loadedBitmap
                    errorMessage = ""
                }, onError = { error ->
                    errorMessage = error
                    bitmap = null
                })*/
            }
        }
          imageUriTemp = null // Reset the imageUri state

    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        cameraPermissionGranted.value = isGranted
    }

    // Check and request permission if not granted
    LaunchedEffect(Unit) {
        if (!cameraPermissionGranted.value) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
        val savedUri = sharedPreferences.getString("image_uri", null)
        if (savedUri != null) {
            imageUri = Uri.parse(savedUri)
            // Reload image from sharedPreferences
        }
    }


    // Save imageUri to SharedPreferences when updated
    LaunchedEffect(imageUri) {
        imageUri?.let { uri ->
            sharedPreferences.edit().putString("image_uri", uri.toString()).apply()
        }
    }

    // UI with LazyColumn
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {

            Spacer(modifier = Modifier.height(16.dp))

            fun getImageDateTakenFromExif(context: Context, uri: Uri): String? {
                return try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val exif = ExifInterface(inputStream)
                        val dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME)

                        dateTime?.let {
                            // Alkuperäinen EXIF-muoto: yyyy:MM:dd HH:mm:ss
                            val inputFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
                            val date = inputFormat.parse(it)

                            // Muunnetaan muotoon dd.MM.yyyy
                            val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                            return date?.let { outputFormat.format(it) } ?: "No photo taken"
                        } ?: context.getString(R.string.no_photo_taken)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    "Unknown date"
                }
            }


            // Display image if bitmap is available
            imageUri?.let {uri ->
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

                val formattedDate = getImageDateTakenFromExif(context, uri)
                val safeFormattedDate = formattedDate ?: "N/A"
                Text(
                    text = stringResource(R.string.photo_taken, safeFormattedDate),
                    modifier = Modifier.padding(8.dp)
                )
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
            }

            // Show error message if there was an issue with the image
            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = Color.Red)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                imageUri = null // Reset the imageUri state
                // Create photo file
                val pictureFile = File(context.filesDir, "picture.jpg")
                val pictureUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    pictureFile
                )
                imageUriTemp = pictureUri
                cameraLauncher.launch(pictureUri)
            }) {
                Text(stringResource(R.string.take_picture))
            }

            Spacer(modifier = Modifier.height(16.dp))


            // Button to delete the picture
            Button(onClick = {
                imageUri = null
                sharedPreferences.edit().remove("image_uri").apply()

            }) {
                Text(stringResource(R.string.delete_picture))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val intentBrowserDataJSON = Intent(Intent.ACTION_VIEW, Uri.parse(BASE_URL_EURO + EUROMELANOMA_LOCATION))
                    context.startActivity(intentBrowserDataJSON)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(text = stringResource(R.string.about_benign_lesions))
            }


        }

    }
}




// Fourth page for adding user name
@Composable
fun Page4(userName: String, onUserNameChange: (String) -> Unit) {
    var localUserName by remember { mutableStateOf(userName) }
    var errorMessage by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            if (userName.isNotEmpty()) {
                Spacer(modifier = Modifier.padding(30.dp))
                Text(
                    text = stringResource(R.string.not, userName),
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )
                Button(onClick = {
                    onUserNameChange("") // Delete the user name
                }) {
                    Text(stringResource(R.string.delete_user_name))
                }
                Spacer(modifier = Modifier.padding(20.dp))
                Text(
                    text = stringResource(R.string.or_change_the_user_name),
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )
                TextField(
                    value = localUserName,
                    onValueChange = { newValue ->
                        if (newValue.length <= 16) {
                            localUserName = newValue
                            errorMessage = ""
                        } else {
                            errorMessage = "Username cannot exceed 10 characters"
                        }
                    },
                    label = { Text(stringResource(R.string.name)) }
                )
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.padding(30.dp))
                Text(
                    text = stringResource(R.string.who_s_using_this_app),
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )
                TextField(
                    value = localUserName,
                    onValueChange = { newValue ->
                        if (newValue.length <= 16) {
                            localUserName = newValue
                            errorMessage = ""
                        } else {
                            errorMessage = "Username cannot exceed 10 characters"
                        }
                    },
                    label = { Text(stringResource(R.string.textfield_label_name_2)) }
                )
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Button(onClick = {
                if (localUserName.length <= 16) {
                    onUserNameChange(localUserName) // Save the user name
                }
            }) {
                Text(stringResource(R.string.save_button))
            }
        }
    }
}