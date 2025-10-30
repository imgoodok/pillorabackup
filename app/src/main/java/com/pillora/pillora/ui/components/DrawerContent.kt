package com.pillora.pillora.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DrawerContent(onClose: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Menu Lateral",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )
        Divider()
        Spacer(modifier = Modifier.height(8.dp))

        NavigationItem("Home", onClick = onClose)
        NavigationItem("Perfil", onClick = onClose)
        NavigationItem("Configurações", onClick = onClose)
    }
}

@Composable
fun NavigationItem(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        style = MaterialTheme.typography.bodyLarge
    )
}
