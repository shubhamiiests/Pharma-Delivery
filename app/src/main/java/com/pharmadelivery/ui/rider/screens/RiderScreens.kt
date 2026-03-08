package com.pharmadelivery.ui.rider.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.pharmadelivery.data.models.*
import com.pharmadelivery.data.repository.MockData
import com.pharmadelivery.ui.common.*
import com.pharmadelivery.ui.theme.*
import com.pharmadelivery.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ===== RIDER DASHBOARD =====

@Composable
fun RiderDashboardScreen(
    rider: User?,
    isOffline: Boolean,
    onAvailablePickups: () -> Unit,
    onEarnings: () -> Unit,
    onProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val riderInfo = MockData.riderInfo
    var isOnline by remember { mutableStateOf(riderInfo.status == RiderStatus.ONLINE) }
    val orders = MockData.generateSampleOrders().filter { it.riderId == rider?.id || it.riderId == "rider_demo_001" }

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(
                    Triple(Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "Dashboard"),
                    Triple(Icons.Filled.LocalShipping, Icons.Outlined.LocalShipping, "Pickups"),
                    Triple(Icons.Filled.CurrencyRupee, Icons.Outlined.CurrencyRupee, "Earnings"),
                    Triple(Icons.Filled.Person, Icons.Outlined.Person, "Profile")
                ).forEachIndexed { i, (filled, outlined, label) ->
                    NavigationBarItem(
                        selected = i == 0,
                        onClick = {
                            when (i) {
                                1 -> onAvailablePickups()
                                2 -> onEarnings()
                                3 -> onProfile()
                            }
                        },
                        icon = { Icon(if (i == 0) filled else outlined, contentDescription = label) },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = RiderAccent.copy(alpha = 0.15f))
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with online toggle
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(RiderAccent, RiderAccent.copy(alpha = 0.8f))))
                    .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Hello, ${rider?.name?.substringBefore(" ") ?: "Rider"}!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(riderInfo.vehicleNumber, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                        }
                        IconButton(onClick = onProfile, modifier = Modifier.size(44.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)) {
                            Icon(Icons.Filled.Person, contentDescription = null, tint = Color.White)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Online/Offline toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                if (isOnline) "You are ONLINE" else "You are OFFLINE",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                if (isOnline) "Receiving new order requests" else "Toggle on to start receiving orders",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Switch(
                            checked = isOnline,
                            onCheckedChange = { isOnline = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF76FF03),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.White.copy(alpha = 0.4f)
                            )
                        )
                    }
                }
            }

            if (isOffline) NoInternetBanner()

            Column(modifier = Modifier.padding(16.dp)) {
                // Earnings cards
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Today's Earnings", riderInfo.earningsToday.toRupees(), Icons.Filled.CurrencyRupee, RiderAccent, Modifier.weight(1f))
                    StatCard("Deliveries Today", "${orders.count { it.status == OrderStatus.DELIVERED }}", Icons.Filled.LocalShipping, StatusDelivered, Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Total Deliveries", "${riderInfo.totalDeliveries}", Icons.Filled.Stars, PharmaTeal, Modifier.weight(1f))
                    StatCard("My Rating", "${riderInfo.rating} ⭐", Icons.Filled.Star, Color(0xFFFFC107), Modifier.weight(1f))
                }

                Spacer(Modifier.height(20.dp))

                // Available order alert
                if (isOnline) {
                    Surface(
                        color = RiderAccent.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, RiderAccent.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onAvailablePickups)
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                            val alpha by infiniteTransition.animateFloat(
                                initialValue = 0.5f, targetValue = 1f,
                                animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
                                label = "pulse_alpha"
                            )
                            Box(modifier = Modifier.size(10.dp).scale(alpha).background(RiderAccent, CircleShape))
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Orders Available!", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = RiderAccent)
                                Text("2 orders waiting for pickup", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = RiderAccent)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Recent deliveries
                Text("Recent Deliveries", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))

                if (orders.isEmpty()) {
                    EmptyState(Icons.Filled.LocalShipping, "No Deliveries Yet", "Accept orders to start earning")
                } else {
                    orders.forEach { order ->
                        RiderOrderCard(order = order)
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RiderOrderCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(42.dp).background(RiderAccent.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.LocalShipping, contentDescription = null, tint = RiderAccent, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(order.id, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(order.deliveryAddress.street, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(order.createdAt.toShortDate(), style = MaterialTheme.typography.bodySmall, color = TextHint)
            }
            Column(horizontalAlignment = Alignment.End) {
                OrderStatusChip(order.status)
                Spacer(Modifier.height(4.dp))
                Text(order.deliveryFee.toRupees(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = RiderAccent)
            }
        }
    }
}

// ===== AVAILABLE PICKUPS =====

@Composable
fun AvailablePickupsScreen(
    onBack: () -> Unit,
    onAcceptOrder: (String) -> Unit
) {
    val pendingOrders = MockData.generateSampleOrders().filter {
        it.status == OrderStatus.READY_FOR_PICKUP || it.status == OrderStatus.CONFIRMED
    }.ifEmpty {
        // Generate one more for demo
        listOf(MockData.generateSampleOrders().first().copy(
            id = "ORD-2024-0099",
            status = OrderStatus.READY_FOR_PICKUP,
            pharmacyName = "MedPlus Pharmacy",
            deliveryAddress = MockData.sampleAddresses.first()
        ))
    }

    Scaffold(
        topBar = {
            GradientTopBar(
                title = "Available Pickups",
                subtitle = "${pendingOrders.size} orders ready",
                accentColor = RiderAccent,
                onBackClick = onBack
            )
        }
    ) { padding ->
        if (pendingOrders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState(Icons.Filled.Inbox, "No Orders Right Now", "Check back shortly for new pickup requests")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(padding)
            ) {
                items(pendingOrders) { order ->
                    PickupOrderCard(order = order, onAccept = { onAcceptOrder(order.id) })
                }
            }
        }
    }
}

@Composable
private fun PickupOrderCard(order: Order, onAccept: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(order.id, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = RiderAccent)
                Surface(color = StatusDelivered.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text("₹30 earn", style = MaterialTheme.typography.labelSmall, color = StatusDelivered, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Spacer(Modifier.height(12.dp))

            // Pickup from
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Filled.Store, contentDescription = null, tint = RiderAccent, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Pickup from", style = MaterialTheme.typography.labelSmall, color = TextHint)
                    Text(order.pharmacyName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text("MG Road, Bangalore", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            Spacer(Modifier.height(8.dp))

            // Dotted line connector
            Box(
                modifier = Modifier.width(2.dp).height(20.dp).padding(start = 8.dp).background(
                    Brush.verticalGradient(listOf(RiderAccent.copy(alpha = 0.3f), RiderAccent.copy(alpha = 0.3f)))
                )
            )

            Spacer(Modifier.height(4.dp))

            // Deliver to
            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = StatusCancelled, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Deliver to", style = MaterialTheme.typography.labelSmall, color = TextHint)
                    Text(order.customerName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    Text(order.deliveryAddress.street, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.Schedule, contentDescription = null, tint = TextHint, modifier = Modifier.size(14.dp))
                    Text(" ~3.2 km  •  12 min", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Button(
                    onClick = onAccept,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RiderAccent)
                ) {
                    Text("Accept Order")
                }
            }
        }
    }
}

// ===== ACTIVE DELIVERY =====

@Composable
fun ActiveDeliveryScreen(
    orderId: String,
    onNavigate: () -> Unit,
    onProofOfDelivery: () -> Unit,
    onBack: () -> Unit
) {
    val order = MockData.generateSampleOrders().find { it.id == orderId }
        ?: MockData.generateSampleOrders().first()

    var deliveryStep by remember { mutableIntStateOf(0) }
    val steps = listOf("Head to Pharmacy", "Picked Up", "Out for Delivery", "Delivered")

    Scaffold(
        topBar = {
            GradientTopBar("Active Delivery", subtitle = orderId, accentColor = RiderAccent, onBackClick = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Progress
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = RiderAccent.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery Progress", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Step ${deliveryStep + 1}/${steps.size}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { (deliveryStep + 1).toFloat() / steps.size },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = RiderAccent
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        steps[deliveryStep],
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = RiderAccent
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            // Order info
            Card(shape = RoundedCornerShape(14.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Order Details", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Filled.Store, contentDescription = null, tint = RiderAccent, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Pickup", style = MaterialTheme.typography.labelSmall, color = TextHint)
                            Text(order.pharmacyName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = StatusCancelled, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Customer", style = MaterialTheme.typography.labelSmall, color = TextHint)
                            Text(order.customerName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text(order.deliveryAddress.street, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Divider(color = DividerColor)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Delivery Earnings", style = MaterialTheme.typography.bodyMedium)
                        Text("₹30.00", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = StatusDelivered)
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            // Action buttons
            Button(
                onClick = onNavigate,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RiderAccent)
            ) {
                Icon(Icons.Filled.Navigation, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Navigate to ${if (deliveryStep < 1) "Pharmacy" else "Customer"}")
            }

            Spacer(Modifier.height(10.dp))

            if (deliveryStep < steps.lastIndex) {
                OutlinedButton(
                    onClick = { deliveryStep++ },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RiderAccent),
                    border = BorderStroke(1.dp, RiderAccent)
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Mark: ${steps.getOrElse(deliveryStep + 1) { "Complete" }}")
                }
            }

            if (deliveryStep == steps.lastIndex - 1) {
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = onProofOfDelivery,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDelivered)
                ) {
                    Icon(Icons.Filled.Camera, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Upload Proof of Delivery")
                }
            }

            Spacer(Modifier.height(12.dp))

            // Contact customer
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                    Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Call Customer")
                }
                OutlinedButton(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp)) {
                    Icon(Icons.Filled.Message, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Message")
                }
            }
        }
    }
}

// ===== DELIVERY NAVIGATION (Mock) =====

@Composable
fun DeliveryNavigationScreen(orderId: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            GradientTopBar("Navigation", subtitle = "Live tracking", accentColor = RiderAccent, onBackClick = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            // Fake map area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Map, contentDescription = null, tint = RiderAccent.copy(alpha = 0.5f), modifier = Modifier.size(80.dp))
                    Text("Navigation Map", style = MaterialTheme.typography.titleMedium, color = RiderAccent)
                    Text("(Integrates with Google Maps in production)", style = MaterialTheme.typography.bodySmall, color = TextHint)

                    Spacer(Modifier.height(24.dp))

                    // Simulated route card
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Navigation, contentDescription = null, tint = RiderAccent, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("3.2 km away", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                Text("ETA: ~12 minutes via MG Road", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            // Bottom actions
            Surface(shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(onClick = {}, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Filled.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Call")
                    }
                    Button(
                        onClick = onBack,
                        modifier = Modifier.weight(2f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RiderAccent)
                    ) {
                        Text("Reached Destination")
                    }
                }
            }
        }
    }
}

// ===== PROOF OF DELIVERY =====

@Composable
fun ProofOfDeliveryScreen(
    orderId: String,
    onDelivered: () -> Unit,
    onBack: () -> Unit
) {
    var photoTaken by remember { mutableStateOf(false) }
    var otp by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            GradientTopBar("Proof of Delivery", subtitle = orderId, accentColor = RiderAccent, onBackClick = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))

            Text("Confirm Delivery", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Upload a photo or collect OTP from customer", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)

            Spacer(Modifier.height(24.dp))

            // Photo upload
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clickable { photoTaken = true },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, if (photoTaken) StatusDelivered else DividerColor),
                color = if (photoTaken) StatusDelivered.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (photoTaken) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = StatusDelivered, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Photo Captured!", style = MaterialTheme.typography.titleSmall, color = StatusDelivered, fontWeight = FontWeight.Bold)
                            Text("delivery_proof.jpg", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = TextHint, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Tap to take photo", style = MaterialTheme.typography.bodyMedium, color = TextHint)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = DividerColor)
                Text("  OR  ", style = MaterialTheme.typography.bodySmall, color = TextHint)
                Divider(modifier = Modifier.weight(1f), color = DividerColor)
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = { if (it.length <= 4) otp = it },
                label = { Text("Customer OTP") },
                placeholder = { Text("4-digit OTP") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                textStyle = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center, letterSpacing = 10.sp)
            )

            Spacer(Modifier.height(24.dp))

            PharmaButton(
                text = "Confirm Delivery",
                loading = isSubmitting,
                enabled = photoTaken || otp.length == 4,
                onClick = {
                    scope.launch {
                        isSubmitting = true
                        delay(1500)
                        isSubmitting = false
                        onDelivered()
                    }
                }
            )
        }
    }
}

// ===== RIDER EARNINGS =====

@Composable
fun RiderEarningsScreen(onBack: () -> Unit) {
    val rider = MockData.riderInfo

    Scaffold(
        topBar = {
            GradientTopBar("Earnings", accentColor = RiderAccent, onBackClick = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Total earnings hero
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(RiderAccent, RiderAccent.copy(alpha = 0.7f))))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total Lifetime Earnings", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                    Text(rider.earningsTotal.toRupees(), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(Modifier.height(8.dp))
                    Text("${rider.totalDeliveries} deliveries completed", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Today", rider.earningsToday.toRupees(), Icons.Filled.Today, RiderAccent, Modifier.weight(1f))
                    StatCard("This Week", "₹2,840", Icons.Filled.DateRange, PharmaTeal, Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("This Month", "₹11,200", Icons.Filled.CalendarMonth, StatusDelivered, Modifier.weight(1f))
                    StatCard("Avg/Delivery", "₹28.50", Icons.Filled.TrendingUp, StatusPending, Modifier.weight(1f))
                }

                Spacer(Modifier.height(20.dp))
                Text("Recent Transactions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))

                listOf(
                    Triple("ORD-2024-0001", "₹30.00", "Today, 2:30 PM"),
                    Triple("ORD-2024-0003", "₹30.00", "Today, 11:15 AM"),
                    Triple("ORD-2024-0098", "₹35.00", "Yesterday, 4:00 PM"),
                    Triple("ORD-2024-0097", "₹28.00", "Yesterday, 1:45 PM")
                ).forEach { (orderId, amount, time) ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(10.dp)) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).background(StatusDelivered.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.CurrencyRupee, contentDescription = null, tint = StatusDelivered, modifier = Modifier.size(18.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(orderId, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text(time, style = MaterialTheme.typography.bodySmall, color = TextHint)
                            }
                            Text(amount, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = StatusDelivered)
                        }
                    }
                }
            }
        }
    }
}

// ===== RIDER PROFILE =====

@Composable
fun RiderProfileScreen(
    user: User?,
    onDocuments: () -> Unit,
    onSupport: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val rider = MockData.riderInfo
    var showLogout by remember { mutableStateOf(false) }

    if (showLogout) {
        AlertDialog(
            onDismissRequest = { showLogout = false },
            title = { Text("Log Out") },
            text = { Text("Are you sure?") },
            confirmButton = { TextButton(onClick = onLogout) { Text("Log Out", color = StatusCancelled) } },
            dismissButton = { TextButton(onClick = { showLogout = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            GradientTopBar("My Profile", accentColor = RiderAccent, onBackClick = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = RiderAccent.copy(alpha = 0.08f)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(80.dp).background(RiderAccent.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(user?.name?.firstOrNull()?.uppercase() ?: "S", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = RiderAccent)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(user?.name ?: rider.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(user?.phone ?: rider.phone, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(color = Color(0xFFFFC107).copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                            Text("⭐ ${rider.rating}", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFFC107), modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                        }
                        Surface(color = StatusDelivered.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                            Text("${rider.totalDeliveries} Deliveries", style = MaterialTheme.typography.labelSmall, color = StatusDelivered, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("${rider.vehicleType}  •  ${rider.vehicleNumber}", style = MaterialTheme.typography.bodySmall, color = TextHint)
                }
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf(
                    Triple(Icons.Filled.Edit, "Edit Profile", {}),
                    Triple(Icons.Filled.Article, "Documents & License", onDocuments),
                    Triple(Icons.Filled.CurrencyRupee, "Earnings", {}),
                    Triple(Icons.Filled.Headset, "Support", onSupport),
                    Triple(Icons.Filled.Logout, "Log Out", { showLogout = true })
                ).forEach { (icon, label, action) ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable(onClick = action),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(icon, contentDescription = null, tint = if (label == "Log Out") StatusCancelled else RiderAccent, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(16.dp))
                            Text(label, style = MaterialTheme.typography.bodyMedium, color = if (label == "Log Out") StatusCancelled else MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextHint, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// ===== RIDER DOCUMENTS =====

@Composable
fun RiderDocumentsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            GradientTopBar("Documents", accentColor = RiderAccent, onBackClick = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Surface(color = StatusPending.copy(alpha = 0.08f), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Info, contentDescription = null, tint = StatusPending)
                    Spacer(Modifier.width(8.dp))
                    Text("Keep documents up to date to remain eligible for deliveries.", style = MaterialTheme.typography.bodySmall, color = StatusPending)
                }
            }
            Spacer(Modifier.height(16.dp))

            listOf(
                Triple("Driving License", "KA-DL-2019-00123456", "Verified ✓"),
                Triple("Aadhaar Card", "XXXX XXXX 4512", "Verified ✓"),
                Triple("PAN Card", "ABCDE1234F", "Verified ✓"),
                Triple("Vehicle RC Book", "KA 05 HB 9988", "Verified ✓"),
                Triple("Vehicle Insurance", "Policy No. INS-2024-XY", "Expiring Soon ⚠"),
                Triple("Profile Photo", "rider_photo.jpg", "Uploaded ✓")
            ).forEach { (doc, detail, status) ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Article, contentDescription = null, tint = RiderAccent, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(doc, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text(detail, style = MaterialTheme.typography.bodySmall, color = TextHint)
                        }
                        Text(
                            status,
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                status.contains("✓") -> StatusDelivered
                                status.contains("⚠") -> StatusPending
                                else -> StatusCancelled
                            }
                        )
                    }
                }
            }
        }
    }
}
