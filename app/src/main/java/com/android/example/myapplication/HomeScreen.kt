package com.android.example.myapplication

import AuthViewModel
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

val imagePickerLauncher = rememberLauncherForActivityResult(
    contract = GetContent()
) { uri: Uri? ->
    uri?.let {
        selectedImageUri = it
        // Upload selected image
        authViewModel.uploadProfilePicture(it) { success, message
            ->
            if (success) {
                snackbarMessage = "Profile picture updated!"
            } else {
                snackbarMessage = message ?: "Upload failed."
            }
        }
    }
}
...
val imagePickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
    ...
Button(onClick = { imagePickerLauncher.launch("image/*") }) {
    Text("Upload/Change Profile Picture")
}
...
Button(onClick = {
    authViewModel.deleteProfilePicture { success, message ->
        if (success) {
            snackbarMessage = "Profile picture deleted!"
        } else {
            snackbarMessage = message ?: "Delete failed."
        }
    }
}) {
    Text("Delete Profile Picture")
}


@Composable
fun HomeScreen(navController: NavController, authViewModel:
AuthViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome, ${authViewModel.getCurrentUser()?.email}")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            authViewModel.signOut()
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }) {
            Text("Logout")
        }
    }
}
