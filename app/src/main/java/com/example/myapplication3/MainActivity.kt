package com.example.myapplication3

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
import com.example.myapplication3.ui.theme.MyApplication3Theme

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

    // State variables
    var id by remember { mutableStateOf("123") } // Replace "123" with your default value
    var username by remember { mutableStateOf("") }
    var courseName by remember { mutableStateOf("") }

    // About Section Info
    val studentName = "Your Name" // Replace with your name
    val studentID = "YourID"      // Replace with your student ID

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Student App") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Input Fields
            OutlinedTextField(
                value = id,
                onValueChange = { id = it },
                label = { Text("ID") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = courseName,
                onValueChange = { courseName = it },
                label = { Text("Course Name") },
                modifier = Modifier.fillMaxWidth()
            )



            // About Section
            Divider()
            Text("About", style = MaterialTheme.typography.titleMedium)
            Text("Student Name: ${if (username.isNotEmpty()) username else "Not loaded"}")
            Text("Student ID: ${if (id.isNotEmpty()) id else "Not loaded"}")
            Text("Course Name: ${if (courseName.isNotEmpty()) courseName else "Not loaded"}")
        }
    }
}

