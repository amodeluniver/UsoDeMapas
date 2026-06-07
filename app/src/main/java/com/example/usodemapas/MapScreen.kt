package com.example.usodemapas

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.Polyline
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.rememberMarkerState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.google.maps.android.compose.MapUiSettings
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch


@Composable
fun MapScreen() {
    val context = LocalContext.current
    val ArequipaLocation = LatLng(-16.4040102, -71.559611) // Arequipa, Perú
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(ArequipaLocation, 12f)
    }

    val coroutineScope = rememberCoroutineScope()

    // Cliente para obtener la ubicación GPS real del teléfono
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var currentMapType by remember { mutableStateOf(MapType.NORMAL) }

    LaunchedEffect(Unit) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newLatLngZoom(LatLng(-16.2520984, -71.6836503), 12f), // Ubicación Yura
            durationMs = 3000
        )
    }

    // ESTADOS DEL EJERCICIO 2: Verificación e inicialización de permisos de ubicación
    var isLocationPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Lanzador reactivo para solicitar el permiso de ubicación al usuario
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        isLocationPermissionGranted = isGranted
        if (isGranted) {
            Toast.makeText(context, "Permiso concedido. Cargando ubicación actual...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permiso de ubicación denegado.", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Añadir GoogleMap al layout
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = currentMapType,
                isMyLocationEnabled = isLocationPermissionGranted // 1. Activa el GPS y el punto azul
            ),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false
            )
        ){
            Marker(
                state = rememberMarkerState(position = ArequipaLocation),
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE),
                title = "Arequipa, Perú",
                snippet = "Punto de inicio guiado"
                )
            // 2. Múltiples marcadores (Iteración de lista de ubicaciones)
            val locations = listOf(
                LatLng(-16.433415, -71.5442652),  // JLByR
                LatLng(-16.4205151, -71.4945209), // Paucarpata
                LatLng(-16.3524187, -71.5675994)  // Zamacola
            )
            locations.forEach { location ->
                Marker(
                    state = rememberMarkerState(position = location),
                    title = "Ubicación de Interés",
                    snippet = "Punto de interés de la ciudad"
                )
            }

            val mallAventuraPolygon = listOf(
                LatLng(-16.432292, -71.509145),
                LatLng(-16.432757, -71.509626),
                LatLng(-16.433013, -71.509310),
                LatLng(-16.432566, -71.508853)
            )


            val parqueLambramaniPolygon = listOf(
                LatLng(-16.422704, -71.530830),
                LatLng(-16.422920, -71.531340),
                LatLng(-16.423264, -71.531110),
                LatLng(-16.423050, -71.530600)
            )

            val plazaDeArmasPolygon = listOf(
                LatLng(-16.398866, -71.536961),
                LatLng(-16.398744, -71.536529),
                LatLng(-16.399178, -71.536289),
                LatLng(-16.399299, -71.536721)
            )


            Polygon(
                points = plazaDeArmasPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue.copy(alpha = 0.4f), // Modificado para visibilidad transparente
                strokeWidth = 5f
            )
            Polygon(
                points = parqueLambramaniPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue.copy(alpha = 0.4f),
                strokeWidth = 5f
            )
            Polygon(
                points = mallAventuraPolygon,
                strokeColor = Color.Red,
                fillColor = Color.Blue.copy(alpha = 0.4f),
                strokeWidth = 5f
            )
             // Traza una ruta lineal que une la Plaza de Armas con un Punto Aleatorio
            val rutaEjemploPoints = listOf(
                LatLng(-16.398866, -71.536961), // Plaza de Armas
                LatLng(-16.408681, -71.505845), // Punto intermedio vial
                LatLng(-16.422704, -71.530830)  // Punto Aleatorio
            )
            Polyline(
                points = rutaEjemploPoints,
                clickable = true,
                color = Color.Green,
                width = 10f
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(12.dp)
                .background(Color.White.copy(alpha = 0.8f))
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { currentMapType = MapType.NORMAL },
                modifier = Modifier.padding(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if(currentMapType == MapType.NORMAL) Color.DarkGray else Color.Blue)
            ) { Text("Normal") }

            Button(
                onClick = { currentMapType = MapType.SATELLITE },
                modifier = Modifier.padding(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if(currentMapType == MapType.SATELLITE) Color.DarkGray else Color.Blue)
            ) { Text("Satélite") }

            Button(
                onClick = { currentMapType = MapType.HYBRID },
                modifier = Modifier.padding(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if(currentMapType == MapType.HYBRID) Color.DarkGray else Color.Blue)
            ) { Text("Híbrido") }

            Button(
                onClick = { currentMapType = MapType.TERRAIN },
                modifier = Modifier.padding(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if(currentMapType == MapType.TERRAIN) Color.DarkGray else Color.Blue)
            ) { Text("Terreno") }
        }

        // 🎯 NUEVO BOTÓN UBICADO EN LA PARTE INFERIOR DEL TELÉFONO
        if (isLocationPermissionGranted) {
            Button(
                onClick = {
                    try {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            if (location != null) {
                                // 🛠️ SOLUCIÓN: Ejecutamos la función de suspensión dentro de la corrutina lanzada
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        update = CameraUpdateFactory.newLatLngZoom(
                                            LatLng(location.latitude, location.longitude),
                                            16f
                                        ),
                                        durationMs = 1500
                                    )
                                }
                            } else {
                                Toast.makeText(context, "Buscando señal GPS...", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Ver Mi Ubicación Actual 🎯")
            }
        }

    }
}
