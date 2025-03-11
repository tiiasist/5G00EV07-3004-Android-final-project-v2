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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import android.Manifest
import android.content.Context
import android.media.ExifInterface
import android.provider.MediaStore

import androidx.core.content.ContextCompat


import androidx.compose.foundation.Image
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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


class Page1ViewModel : ViewModel() {
    data class QuoteResponse(
        val id: Int,
        val q: String,  // Changed from "quote" to "q" to match API response
        val a: String,  // Changed from "author" to "a" to match API response
    )

    interface ApiService {
        @GET("api/random")
        suspend fun getDataList(): List<QuoteResponse>
    }

    object RetrofitClient {
        private const val BASE_URL = "https://zenquotes.io/"

        val apiService: ApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}

// First page for home screen
@SuppressLint("DefaultLocale")
@Composable
fun Page1(viewModel: Page1ViewModel = viewModel()) {
    var data by remember { mutableStateOf<List<Page1ViewModel.QuoteResponse>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(true) {
        try {
            data = Page1ViewModel.RetrofitClient.apiService.getDataList()
            isLoading = false
        } catch (e: Exception) {
            error = e.message
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading quotes...")
        }
    }
    // Display error if any
    else if (error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: $error", color = Color.Red)
            Text("No quotes available")
        }
    }
    // Display data if available
    else if (data != null && data!!.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(data!!) { quote ->
                Text(
                    text = "\"${quote.q}\"",  // Changed to match API response field
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = "— ${quote.a}",  // Changed to match API response field
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
    // Display empty state
    else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No quotes available")
        }
    }
}


// Second page
class Page2ViewModel : ViewModel() {
    var systolicPressure by mutableStateOf("")
    var diastolicPressure by mutableStateOf("")

    var statusMessage by mutableStateOf<String?>(null)

    var savedSystolic by mutableStateOf("")
    var savedDiastolic by mutableStateOf("")
    var savedTimestamp by mutableStateOf<Long?>(null)
    var formattedTimestamp by mutableStateOf("")

    // Load saved values
    fun loadSavedValues(sharedPreferences: SharedPreferences) {
        systolicPressure = sharedPreferences.getString("systolic_pressure", "") ?: ""
        diastolicPressure = sharedPreferences.getString("diastolic_pressure", "") ?: ""
        savedTimestamp = sharedPreferences.getLong("timestamp", 0)

        savedSystolic = systolicPressure
        savedDiastolic = diastolicPressure

        if (savedTimestamp != null && savedTimestamp != 0L) {
            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            formattedTimestamp = sdf.format(Date(savedTimestamp!!))
        }
    }
    // Save values
    fun saveValues(sharedPreferences: SharedPreferences): Boolean {
        // Basic validation
        val systolic = systolicPressure.toIntOrNull()
        val diastolic = diastolicPressure.toIntOrNull()

        return if (systolic != null && diastolic != null && systolic > 0 && diastolic > 0) {
            val currentTime = System.currentTimeMillis()  // Define the variable here
            sharedPreferences.edit()
                .putString("systolic_pressure", systolicPressure)
                .putString("diastolic_pressure", diastolicPressure)
                .putLong("timestamp", currentTime)
                .apply()

            // Update saved values
            savedSystolic = systolicPressure
            savedDiastolic = diastolicPressure
            savedTimestamp = currentTime  // Now using the defined variable

            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            formattedTimestamp = sdf.format(Date(currentTime))  // Using the defined variable
            true
        } else {
            false
        }
    }

    fun deleteSavedValues(sharedPreferences: SharedPreferences) {
        sharedPreferences.edit()
            .remove("systolic_pressure")
            .remove("diastolic_pressure")
            .remove("timestamp")
            .apply()

        savedSystolic = ""
        savedDiastolic = ""
        savedTimestamp = null
        formattedTimestamp = ""
    }

    fun getBloodPressureStatus(): String {
        val systolic = savedSystolic.toIntOrNull() ?: 0
        val diastolic = savedDiastolic.toIntOrNull() ?: 0
        return  when {
            systolic < 100 && diastolic < 60 -> "LOW"
            systolic in 100..129 && diastolic in 60..84 -> "NORMAL"
            systolic >130 && diastolic > 85 -> "HIGH"
            else -> "UNKNOWN"

        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun Page2( viewModel: Page2ViewModel = viewModel()) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val sharedPreferences = context.getSharedPreferences("BloodPressureData", Context.MODE_PRIVATE)
    var showSaveMessage by remember { mutableStateOf(false) }
    var isSaveSuccessful by remember { mutableStateOf(false) }

    // Load saved values when the screen is first composed
    LaunchedEffect(Unit) {
        viewModel.loadSavedValues(sharedPreferences)
    }

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
                value = viewModel.systolicPressure,
                onValueChange = { viewModel.systolicPressure = it },
                label = { Text(stringResource(R.string.systolic_blood_pressure)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.diastolicPressure,
                onValueChange = { viewModel.diastolicPressure = it },
                label = { Text(stringResource(R.string.diastolic_blood_pressure)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { keyboardController?.hide() }
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    keyboardController?.hide()
                    isSaveSuccessful = viewModel.saveValues(sharedPreferences)
                    showSaveMessage = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.save))
            }

            if (showSaveMessage) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (isSaveSuccessful)
                        stringResource(R.string.blood_pressure_values_saved)
                    else
                        stringResource(R.string.invalid_input_values2),
                    color = if (isSaveSuccessful)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )

                // Hide the message after a delay
                LaunchedEffect(showSaveMessage) {
                    delay(3000)
                    showSaveMessage = false
                }
            }
            // Display saved values if they exist
            // Display saved values if they exist
            if (viewModel.savedTimestamp != null && viewModel.savedTimestamp != 0L) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (viewModel.getBloodPressureStatus()) {
                                "LOW" -> Color.Blue.copy(alpha = 0.2f)
                                "NORMAL" -> Color.Green.copy(alpha = 0.2f)
                                "HIGH" -> Color.Red.copy(alpha = 0.2f)
                                else -> MaterialTheme.colorScheme.primaryContainer
                            }
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Last Saved Values",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Systoli blood pressure: ${viewModel.savedSystolic}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Diastolic blood pressure: ${viewModel.savedDiastolic}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Saved on: ${viewModel.formattedTimestamp}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            viewModel.deleteSavedValues(sharedPreferences)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(stringResource(R.string.delete_saved_values))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
    /*
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
    }*/
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
