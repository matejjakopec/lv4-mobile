package com.mjakopec.lv4

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mjakopec.lv4.ui.theme.Lv4Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState)
        setContent {
            val currentUser = FirebaseAuth.getInstance().currentUser
            var screen = "main_screen"
            if (currentUser != null) {
                screen = "user_screen"
            }
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = screen) {
                composable("main_screen") {
                    LoginRegisterScreen(navController)
                }
                composable("user_screen") {
                    if (currentUser != null) {
                        UserScreen(user = currentUser, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun UserScreen(user: FirebaseUser, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        user.email?.let { Text(text = it) }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = user.metadata.toString())
    }
    Button(
        onClick = {
            // Navigate to OtherScreen when button clicked
            FirebaseAuth.getInstance().signOut();
            navController.navigate("main_screen")
        },
        modifier = Modifier
            .padding(16.dp)
    ) {
        Text(text = "Log out")
    }
}

@Composable
fun LoginRegisterScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { signIn(context, email, password, navController) }) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { register(context, email, password) }) {
            Text("Register")
        }
    }
}
private fun signIn(context: Context, email: String, password: String, navController: NavController) {
    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Prijavljeno uspješno
                Toast.makeText(context, "Logged in successfully",
                    Toast.LENGTH_SHORT).show()
                navController.navigate("user_screen")
            } else {
                // Prijavljivanje neuspješno
                Toast.makeText(context, "Login failed",
                    Toast.LENGTH_SHORT).show()
            }
        }
}
private fun register(context: Context, email: String, password: String) {
    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,
        password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Registracija uspješna
                Toast.makeText(context, "Registered successfully",
                    Toast.LENGTH_SHORT).show()
            } else {
                // Registracija neuspješna
                Toast.makeText(context, "Registration failed",
                    Toast.LENGTH_SHORT).show()
            }
        }
}