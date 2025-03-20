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
import androidx.exifinterface.media.ExifInterface
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.FileProvider
import com.example.vitrapp.ui.theme.deleteReadingButton
import com.example.vitrapp.ui.theme.highBloodPressureState
import com.example.vitrapp.ui.theme.lowBloodPressureState
import com.example.vitrapp.ui.theme.normalBloodPressureState
import com.example.vitrapp.ui.theme.unknownBloodPressureState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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
    val bloodPressureViewModel = viewModel<Page2ViewModel>()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Reload values when navigating to page1
    LaunchedEffect(currentRoute) {
        if (currentRoute == "page1") {
            bloodPressureViewModel.loadSavedValues(sharedPreferences)
        }
    }

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
                    Page1(bloodPressureViewModel)
                }
                composable("page2") {// Blood Pressure Values
                    Page2(bloodPressureViewModel)
                }
                composable("page3") {// Skin camera
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
fun Page1(viewModel: Page2ViewModel, quoteViewModel: Page1ViewModel = viewModel()) {
    var data by remember { mutableStateOf<List<Page1ViewModel.QuoteResponse>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Force recomposition when the savedSystolic or savedDiastolic values change
    val bpValues by remember(viewModel.savedSystolic, viewModel.savedDiastolic) {
        mutableStateOf("${viewModel.savedSystolic}/${viewModel.savedDiastolic}")
    }

    LaunchedEffect(true) {
        try {
            data = Page1ViewModel.RetrofitClient.apiService.getDataList()
            isLoading = false
        } catch (e: Exception) {
            error = e.message
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        // Quote section
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Loading quotes...")
            }
        }
        else if (error != null) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                //Text("Error: $error", color = Color.Red)
                Text("No quotes available")
            }
        }
        else if (data != null && data!!.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    .padding(16.dp)
            ) {
                // Display first quote from the list
                data!!.firstOrNull()?.let { quote ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "\"${quote.q}\"",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(
                            text = "— ${quote.a}",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
            }
        }
        else {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("No quotes available")
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Average Blood Pressure Section
        Text(
            text = " Recent Blood Pressure Average",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Blood pressure status
        if (viewModel.readingsList.isNotEmpty()) {
            val (avgSystolic, avgDiastolic) = viewModel.getAverageBloodPressure()
            val status = viewModel.getAverageBloodPressureStatus()
            val statusText = when(status) {
                "LOW" -> "Low Blood Pressure"
                "NORMAL" -> "Normal Blood Pressure"
                "HIGH" -> "High Blood Pressure"
                else -> "Unknown Status"
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when (status) {
                            "LOW" -> lowBloodPressureState.copy(alpha = 0.2f)
                            "NORMAL" -> normalBloodPressureState.copy(alpha = 0.2f)
                            "HIGH" -> highBloodPressureState.copy(alpha = 0.2f)
                            else -> unknownBloodPressureState.copy(alpha = 0.2f)
                        }
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Text(
                        text = "$avgSystolic/$avgDiastolic mmHg (Average)",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Based on ${viewModel.readingsList.size} previous readings",
                        style = MaterialTheme.typography.bodySmall
                    )

                }
            }
        } else if (viewModel.savedSystolic.isNotEmpty() && viewModel.savedDiastolic.isNotEmpty()) {
            // Show latest reading if no history available
            val status = viewModel.getBloodPressureStatus()
            val statusText = when (status) {
                "LOW" -> "Low Blood Pressure"
                "NORMAL" -> "Normal Blood Pressure"
                "HIGH" -> "High Blood Pressure"
                else -> "Unknown Status"
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when (status) {
                            "LOW" -> lowBloodPressureState.copy(alpha = 0.2f)
                            "NORMAL" -> normalBloodPressureState.copy(alpha = 0.2f)
                            "HIGH" -> highBloodPressureState.copy(alpha = 0.2f)
                            else -> unknownBloodPressureState.copy(alpha = 0.2f)
                        }
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "$statusText (Latest)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Text(
                        text = "${viewModel.savedSystolic}/${viewModel.savedDiastolic} mmHg",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    if (viewModel.formattedTimestamp.isNotEmpty()) {
                        Text(
                            text = "Measured: ${viewModel.formattedTimestamp}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp)
            ) {
                Text(
                    text = "No blood pressure data saved yet.\nGo to Blood Pressure tab to record your values.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}


// Second page
class Page2ViewModel : ViewModel() {
    data class BloodPressureReading(
        val systolic: String,
        val diastolic: String,
        val timestamp: Long,
        val formattedTime: String
    )

    var systolicPressure by mutableStateOf("")
    var diastolicPressure by mutableStateOf("")
    var savedSystolic by mutableStateOf("")
    var savedDiastolic by mutableStateOf("")
    var savedTimestamp by mutableStateOf<Long?>(null)
    var formattedTimestamp by mutableStateOf("")
    var readingsList by mutableStateOf<List<BloodPressureReading>>(listOf())

    fun saveValues(sharedPreferences: SharedPreferences): Boolean {
        // Basic validation
        val systolic = systolicPressure.toIntOrNull()
        val diastolic = diastolicPressure.toIntOrNull()

        return if (systolic != null && diastolic != null && systolic > 0 && diastolic > 0 && systolic < 300 && diastolic < 200) {
            val currentTime = System.currentTimeMillis()
            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            val formattedTime = sdf.format(Date(currentTime))

            // Create new reading
            val newReading = BloodPressureReading(
                systolic = systolicPressure,
                diastolic = diastolicPressure,
                timestamp = currentTime,
                formattedTime = formattedTime
            )

            // Get current list and add new reading
            val currentList = getReadingsList(sharedPreferences).toMutableList()
            //currentList.add(newReading)

            // Add previous latest reading to history if it exists
            if (savedSystolic.isNotEmpty() && savedDiastolic.isNotEmpty() && savedTimestamp != null && savedTimestamp != 0L) {
                val previousLatest = BloodPressureReading(
                    systolic = savedSystolic,
                    diastolic = savedDiastolic,
                    timestamp = savedTimestamp!!,
                    formattedTime = formattedTimestamp
                )
                currentList.add(previousLatest)
            }

            // Keep only the 3 most recent readings
            val updatedList = currentList.sortedByDescending { it.timestamp }.take(3)

            // Save updated list
            val gson = Gson()
            val json = gson.toJson(updatedList)
            sharedPreferences.edit()
                .putString("readings_list", json)
                .apply()

            // Update the most recent reading as the current saved value
            sharedPreferences.edit()
                .putString("systolic_pressure", systolicPressure)
                .putString("diastolic_pressure", diastolicPressure)
                .putLong("timestamp", currentTime)
                .apply()

            // Update saved values
            savedSystolic = systolicPressure
            savedDiastolic = diastolicPressure
            savedTimestamp = currentTime
            formattedTimestamp = formattedTime
            readingsList = updatedList

            true
        } else {
            false
        }
    }

    fun getReadingsList(sharedPreferences: SharedPreferences): List<BloodPressureReading> {
        val json = sharedPreferences.getString("readings_list", null)
        if (json != null) {
            try {
                val type = object : TypeToken<List<BloodPressureReading>>() {}.type
                return Gson().fromJson(json, type)
            } catch (e: Exception) {
                // Handle parsing errors
                e.printStackTrace()
            }
        }
        return emptyList()
    }

    fun deleteLatestValue(sharedPreferences: SharedPreferences) {
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

    fun deleteReading(sharedPreferences: SharedPreferences, timestamp: Long) {
        // Get current list
        val currentList = getReadingsList(sharedPreferences).toMutableList()

        // Remove the specific reading with matching timestamp
        val updatedList = currentList.filter { it.timestamp != timestamp }

        // Save updated list
        val gson = Gson()
        val json = gson.toJson(updatedList)
        sharedPreferences.edit()
            .putString("readings_list", json)
            .apply()

        // Update the viewModel's list
        readingsList = updatedList
    }

    fun getBloodPressureStatus(systolic: String, diastolic: String): String {
        val systolicValue = systolic.toIntOrNull() ?: 0
        val diastolicValue = diastolic.toIntOrNull() ?: 0
        return when {
            systolicValue < 100 && diastolicValue < 60 -> "LOW"
            systolicValue in 100..129 && diastolicValue in 60..84 -> "NORMAL"
            systolicValue > 130 && diastolicValue > 85 -> "HIGH"
            else -> "UNKNOWN STATUS"
        }
    }

    fun getBloodPressureStatus(): String {
        return getBloodPressureStatus(savedSystolic, savedDiastolic)
    }


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

        // Load readings list
        readingsList = getReadingsList(sharedPreferences)
    }

    fun getAverageBloodPressure(): Pair<Int, Int> {
        if (readingsList.isEmpty()) {
            return Pair(0, 0)
        }

        val avgSystolic = readingsList
            .mapNotNull { it.systolic.toIntOrNull() }
            .takeIf { it.isNotEmpty() }
            ?.average()
            ?.toInt() ?: 0

        val avgDiastolic = readingsList
            .mapNotNull { it.diastolic.toIntOrNull() }
            .takeIf { it.isNotEmpty() }
            ?.average()
            ?.toInt() ?: 0

        return Pair(avgSystolic, avgDiastolic)
    }

    fun getAverageBloodPressureStatus(): String {
        val (avgSystolic, avgDiastolic) = getAverageBloodPressure()
        return if (avgSystolic > 0 && avgDiastolic > 0) {
            getBloodPressureStatus(avgSystolic.toString(), avgDiastolic.toString())
        } else {
            "UNKNOWN STATUS"
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun Page2( viewModel: Page2ViewModel = viewModel()) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
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
            if (viewModel.savedTimestamp != null && viewModel.savedTimestamp != 0L) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (viewModel.getBloodPressureStatus()) {
                                "LOW" -> lowBloodPressureState.copy(alpha = 0.2f)
                                "NORMAL" -> normalBloodPressureState.copy(alpha = 0.2f)
                                "HIGH" -> highBloodPressureState.copy(alpha = 0.2f)
                                else -> unknownBloodPressureState.copy(alpha = 0.2f)
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
                        text = "Systolic blood pressure: ${viewModel.savedSystolic}",
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
                            viewModel.deleteLatestValue(sharedPreferences)
                        },
                        colors = ButtonDefaults.buttonColors(
                            deleteReadingButton
                        )
                    ) {
                        Text(stringResource(R.string.delete_saved_values))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
            if (viewModel.readingsList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Blood Pressure History",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                ) {
                    items(viewModel.readingsList) { reading ->
                        val readingStatus = viewModel.getBloodPressureStatus(reading.systolic, reading.diastolic)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when (readingStatus) {
                                        "LOW" -> lowBloodPressureState.copy(alpha = 0.2f)
                                        "NORMAL" -> normalBloodPressureState.copy(alpha = 0.2f)
                                        "HIGH" -> highBloodPressureState.copy(alpha = 0.2f)
                                        else -> unknownBloodPressureState.copy(alpha = 0.2f)
                                    }
                                )
                                .padding(16.dp)
                        ) {
                            Row() {
                                Column() {
                                    Text(
                                        text = "${reading.systolic}/${reading.diastolic} mmHg",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        text = "Measured: ${reading.formattedTime}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(deleteReadingButton)
                                        .clickable {
                                            viewModel.deleteReading(sharedPreferences, reading.timestamp)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete reading",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}

// Third page for skin camera
@Composable
fun Page3() {
    val baseUrlEuro = "https://www.euromelanoma.eu/"
    val euromelanomaLocalization =
        stringResource(R.string.en_intl_learn_about_skin_cancer_benign_lesion)

    val errorMessage by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
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

                        // Convert date format
                        dateTime?.let {

                            val inputFormat = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
                            val date = inputFormat.parse(it)

                            val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                            return date?.let { outputFormat.format(it) } ?: "No photo taken"
                        } ?: context.getString(R.string.no_photo_taken)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    "Unknown date"
                }
            }

            // Show the image if it exists
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
                    val intentBrowserDataJSON = Intent(Intent.ACTION_VIEW, Uri.parse(baseUrlEuro + euromelanomaLocalization))
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
