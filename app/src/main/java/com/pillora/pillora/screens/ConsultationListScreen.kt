package com.pillora.pillora.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.pillora.pillora.model.Consultation
import com.pillora.pillora.repository.ConsultationRepository
import com.pillora.pillora.navigation.Screen
import com.pillora.pillora.viewmodel.ConsultationListUiState
import com.pillora.pillora.viewmodel.ConsultationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationListScreen(
    navController: NavController,
    viewModel: ConsultationViewModel
) {
    val consultationListState by viewModel.consultationListUiState.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var consultationToDelete by remember { mutableStateOf<Consultation?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(consultationListState) {
        when (consultationListState) {
            is ConsultationListUiState.Success -> {
                val consultations =
                    (consultationListState as ConsultationListUiState.Success).consultations
                Log.d("ConsultationListScreen", "Estado atualizado: ${consultations.size} consultas")
            }
            is ConsultationListUiState.Loading -> Log.d("ConsultationListScreen", "Estado: Carregando")
            is ConsultationListUiState.Error -> Log.d(
                "ConsultationListScreen",
                "Estado: Erro - ${(consultationListState as ConsultationListUiState.Error).message}"
            )
        }
    }

    if (showDeleteDialog && consultationToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirmar exclusão") },
            text = { Text("Deseja realmente excluir a consulta de ${consultationToDelete?.specialty}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        consultationToDelete?.id?.let { id ->
                            scope.launch {
                                ConsultationRepository.deleteConsultation(
                                    consultationId = id,
                                    onSuccess = {
                                        Toast.makeText(context, "Consulta excluída com sucesso", Toast.LENGTH_SHORT).show()
                                    },
                                    onFailure = {
                                        Toast.makeText(context, "Erro ao excluir: ${it.message}", Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        }
                        showDeleteDialog = false
                        consultationToDelete = null
                    }
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.ConsultationForm.route) }) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar Consulta")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(top = 16.dp)) {

            when (consultationListState) {
                is ConsultationListUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is ConsultationListUiState.Error -> {
                    val errorMsg = (consultationListState as ConsultationListUiState.Error).message
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMsg,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }

                is ConsultationListUiState.Success -> {
                    val consultations =
                        (consultationListState as ConsultationListUiState.Success).consultations

                    if (consultations.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Nenhuma consulta encontrada. Adicione uma nova consulta clicando no botão +",
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Text(
                                "Consultas Médicas",
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(consultations, key = { it.id }) { consultation ->
                                    ConsultationListItem(
                                        consultation = consultation,
                                        onEditClick = {
                                            navController.navigate("${Screen.ConsultationForm.route}?id=${consultation.id}")
                                        },
                                        onDeleteClick = {
                                            consultationToDelete = consultation
                                            showDeleteDialog = true
                                        }
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

@Composable
fun ConsultationListItem(
    consultation: Consultation,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = consultation.specialty,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Dr(a). ${consultation.doctorName.ifEmpty { "Não informado" }}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (consultation.patientName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Paciente: ${consultation.patientName}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Row {
                    IconButton(onClick = onEditClick, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDeleteClick, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Excluir")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.DateRange, contentDescription = "Data e Hora", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = consultation.dateTime.ifEmpty { "Data/Hora não informada" },
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (consultation.location.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = "Local", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = consultation.location, style = MaterialTheme.typography.bodySmall)
                }
            }
            if (consultation.observations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Obs: ${consultation.observations}",
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }
        }
    }
}
