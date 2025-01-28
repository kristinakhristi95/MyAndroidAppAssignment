package com.example.myapplication3


import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.first
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.fillMaxSize


// DataStore instance
val Context.dataStore by preferencesDataStore(name = "user_data")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudentApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isdataload = false;
    // State variables
    var id by remember { mutableStateOf("429") }
    var username by remember { mutableStateOf("") }
    var courseName by remember { mutableStateOf("") }

    // Error message state
    var errorMessage by remember { mutableStateOf("") }

    // About Section Info
    val studentName = "Kristina Khristi"
    val studentID = "301483429"

    // State to determine if data has been loaded
    var dataLoaded by remember { mutableStateOf(false) }
    var dataStored by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Kristina Khristi App", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF013220) // This sets the background color of the top bar
                )
            )

        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE6F3E6))
                .padding(16.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Input Fields
            OutlinedTextField(
                value = id,
                onValueChange = { id = it },
                label = { Text("ID") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage.contains("ID")
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage.contains("Username")
            )
            OutlinedTextField(
                value = courseName,
                onValueChange = { courseName = it },
                label = { Text("Course Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = errorMessage.contains("Course Name")
            )

            // Display Error Message
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }

            // Buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = {
                    // Load Data from DataStore
                    scope.launch {
                        val data = loadUserData(context)
                        id = data["id"] ?: ""
                        username = data["username"] ?: ""
                        courseName = data["courseName"] ?: ""
                    }
                    dataLoaded = true // Mark data as loaded
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF013220), // Yellow background color
                        contentColor = Color.White // Black text for contrast
                    )) {
                    Text("Load")
                }
                Button(onClick = {
                    // Validate fields
                    val validationError = validateFields(id, username, courseName)
                    if (validationError.isEmpty()) {
                        // Store Data in DataStore if validation passes
                        scope.launch {
                            saveUserData(context, id, username, courseName)
                        }
                        errorMessage = ""
                        dataStored =true
                        dataLoaded = false


                    } else {
                        errorMessage = validationError
                    }
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF013220), // Yellow background color
                        contentColor = Color.White // Black text for contrast
                    )) {
                    Text("Store")
                }
                Button(onClick = {
                    // Reset Data in DataStore
                    scope.launch {
                        resetUserData(context)
                        id = ""
                        username = ""
                        courseName = ""
                        errorMessage = ""
                    }
                    dataLoaded = false // Reset data loaded flag
                    dataStored = false
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF013220), // Yellow background color
                        contentColor = Color.White // Black text for contrast
                    )) {
                    Text("Reset")
                }
            }

            // About Section
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("About", style = MaterialTheme.typography.titleLarge)
            Text("Student Name: $studentName", fontWeight = FontWeight.Bold)
            Text("Student ID: $studentID", fontWeight = FontWeight.Bold)

            // Data Display Section
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Data Management", style = MaterialTheme.typography.titleLarge)

            if (!dataLoaded && dataStored) {
                Text("Your Data has been stored.", color = Color.Magenta)
            }
            else if (dataLoaded && dataStored) {
                Text("Student Name: ${if (username.isNotEmpty()) username else "Not loaded"}")
                Text("Student ID: ${if (id.isNotEmpty()) id else "Not loaded"}")
                Text("Course Name: ${if (courseName.isNotEmpty()) courseName else "Not loaded"}")
            } else {
                Text("Data not loaded yet.")
            }
        }
    }
}

// Validate function to check all fields
fun validateFields(id: String, username: String, courseName: String): String {
    return when {
        id.isEmpty() -> "ID cannot be empty."
        !id.all { it.isDigit() } -> "ID must be numeric."
        username.isEmpty() -> "Username cannot be empty."
        courseName.isEmpty() -> "Course Name cannot be empty."
        else -> ""
    }
}

suspend fun saveUserData(context: Context, id: String, username: String, courseName: String) {

    val ID_KEY = stringPreferencesKey("id")
    val USERNAME_KEY = stringPreferencesKey("username")
    val COURSE_NAME_KEY = stringPreferencesKey("courseName")
    context.dataStore.edit { preferences ->
        preferences[ID_KEY] = id
        preferences[USERNAME_KEY] = username
        preferences[COURSE_NAME_KEY] = courseName
    }

}

suspend fun loadUserData(context: Context): Map<String, String> {
    val ID_KEY = stringPreferencesKey("id")
    val USERNAME_KEY = stringPreferencesKey("username")
    val COURSE_NAME_KEY = stringPreferencesKey("courseName")
    return context.dataStore.data.map { preferences ->
        mapOf(
            "id" to (preferences[ID_KEY] ?: ""),
            "username" to (preferences[USERNAME_KEY] ?: ""),
            "courseName" to (preferences[COURSE_NAME_KEY] ?: "")
        )
    }.first()
}

suspend fun resetUserData(context: Context) {
    context.dataStore.edit { it.clear() }
}
