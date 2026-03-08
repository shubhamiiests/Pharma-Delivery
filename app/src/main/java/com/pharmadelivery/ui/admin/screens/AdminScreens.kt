package com.pharmadelivery.ui.admin.screens

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

// ===== ADMIN DASHBOARD =====

@Composable
fun AdminDashboardScreen(
    isOffline: Boolean,
    onUsers: () -> Unit,
    onShops: () -> Unit,
    onRiders: () -> Unit,
    onPrescriptions: () -> Unit,
    onDisputes: () -> Unit,
    onReports: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val report = MockData.adminReport
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(
                    Triple(Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "Overview"),
                    Triple(Icons.Filled.People, Icons.Outlined.People, "Users"),
                    Triple(Icons.Filled.BarChart, Icons.Outlined.BarChart, "Reports"),
                    Triple(Icons.Filled.Settings, Icons.Outlined.Settings, "Settings")
                ).forEachIndexed { i, (filled, outlined, label) ->
                    NavigationBarItem(
                        selected = selectedTab == i,
                        onClick = {
                            selectedTab = i
                            when (i) {
                                1 -> onUsers()
                                2 -> onReports()
                                3 -> onSettings()
                            }
                        },
                        icon = { Icon(if (selectedTab == i) filled else outlined, contentDescription = label) },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = AdminAccent.copy(alpha = 0.15f))
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
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(AdminAccent, AdminAccent.copy(alpha = 0.8f))))
                    .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Admin Panel", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                            Text("PharmaDelivery", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (report.openDisputes > 0) {
                                BadgedBox(badge = { Badge { Text("${report.openDisputes}") } }) {
                                    IconButton(
                                        onClick = onDisputes,
                                        modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
                                    ) {
                                        Icon(Icons.Filled.Warning, contentDescription = null, tint = Color.White)
                                    }
                                }
                            }
                            IconButton(
                                onClick = onSettings,
                                modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
                            ) {
                                Icon(Icons.Filled.Settings, contentDescription = null, tint = Color.White)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AdminHeroStat("Active Orders", "${report.activeOrders}", Modifier.weight(1f))
                        AdminHeroStat("Revenue", report.totalRevenue.toRupees().replace(",", "K").take(7), Modifier.weight(1f))
                        AdminHeroStat("Disputes", "${report.openDisputes}", Modifier.weight(1f))
                    }
                }
            }

            if (isOffline) NoInternetBanner()

            Column(modifier = Modifier.padding(16.dp)) {
                // Platform overview stats
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Total Users", "${report.totalUsers}", Icons.Filled.People, AdminAccent, Modifier.weight(1f))
                    StatCard("Pharmacies", "${report.totalPharmacies}", Icons.Filled.LocalPharmacy, PharmacyAccent, Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Riders", "${report.totalRiders}", Icons.Filled.DeliveryDining, RiderAccent, Modifier.weight(1f))
                    StatCard("Total Orders", "${report.totalOrders}", Icons.Filled.Receipt, PharmaTeal, Modifier.weight(1f))
                }

                Spacer(Modifier.height(20.dp))

                // Management cards
                Text("Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))

                AdminManagementGrid(
                    items = listOf(
                        AdminMenuCard(Icons.Filled.People, "User Management", "${report.totalUsers} users", AdminAccent, onUsers),
                        AdminMenuCard(Icons.Filled.Store, "Shop Management", "${report.totalPharmacies} pharmacies", PharmacyAccent, onShops),
                        AdminMenuCard(Icons.Filled.DeliveryDining, "Rider Management", "${report.totalRiders} riders", RiderAccent, onRiders),
                        AdminMenuCard(Icons.Filled.DocumentScanner, "Prescription Review", "${report.pendingPrescriptions} pending", StatusPending, onPrescriptions),
                        AdminMenuCard(Icons.Filled.Warning, "Dispute Handling", "${report.openDisputes} open", StatusCancelled, onDisputes),
                        AdminMenuCard(Icons.Filled.BarChart, "Reports", "View analytics", PharmaTeal, onReports)
                    )
                )

                Spacer(Modifier.height(20.dp))

                // Recent activity
                Text("Recent System Activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))

                listOf(
                    Triple(Icons.Filled.PersonAdd, "New user registered: Priya Iyer", "2 min ago"),
                    Triple(Icons.Filled.Store, "Pharmacy verified: HealthFirst Store", "15 min ago"),
                    Triple(Icons.Filled.Warning, "Dispute raised for ORD-2024-0003", "1 hour ago"),
                    Triple(Icons.Filled.DocumentScanner, "Prescription approved: ORD-2024-0002", "2 hours ago"),
                    Triple(Icons.Filled.DeliveryDining, "New rider onboarded: Ramesh P.", "3 hours ago")
                ).forEach { (icon, message, time) ->
                    ActivityItem(icon = icon, message = message, time = time)
                    Spacer(Modifier.height(6.dp))
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onLogout,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCancelled),
                    border = BorderStroke(1.dp, StatusCancelled)
                ) {
                    Icon(Icons.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Log Out")
                }
            }
        }
    }
}

@Composable
private fun AdminHeroStat(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.15f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center)
        }
    }
}

private data class AdminMenuCard(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val title: String,
    val subtitle: String,
    val color: Color,
    val onClick: () -> Unit
)

@Composable
private fun AdminManagementGrid(items: List<AdminMenuCard>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                row.forEach { item ->
                    Card(
                        modifier = Modifier.weight(1f).clickable(onClick = item.onClick),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = item.color.copy(alpha = 0.07f)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Box(
                                modifier = Modifier.size(38.dp).background(item.color.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(item.icon, contentDescription = null, tint = item.color, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(item.title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = item.color)
                            Text(item.subtitle, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }
                }
                if (row.size < 2) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ActivityItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    message: String,
    time: String
) {
    Card(shape = RoundedCornerShape(10.dp), elevation = CardDefaults.cardElevation(0.dp)) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(32.dp).background(AdminAccent.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = AdminAccent, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(10.dp))
            Text(message, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
            Text(time, style = MaterialTheme.typography.labelSmall, color = TextHint)
        }
    }
}

// ===== USER MANAGEMENT =====

@Composable
fun UserManagementScreen(onBack: () -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val users = listOf(
        User(id = "u1", name = "Arjun Mehta", email = "arjun@example.com", phone = "+91 99887 76655", role = UserRole.CUSTOMER, isVerified = true),
        User(id = "u2", name = "Preethi Iyer", email = "preethi@example.com", phone = "+91 88776 65544", role = UserRole.CUSTOMER, isVerified = true),
        User(id = "u3", name = "Ramesh Kumar", email = "ramesh@example.com", phone = "+91 77665 54433", role = UserRole.CUSTOMER, isVerified = false),
        User(id = "u4", name = "Anjali Singh", email = "anjali@example.com", phone = "+91 66554 43322", role = UserRole.CUSTOMER, isVerified = true)
    )

    val filtered = users.filter {
        (selectedFilter == "All" || (selectedFilter == "Verified") == it.isVerified) &&
                (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            GradientTopBar("User Management", subtitle = "${users.size} users", accentColor = AdminAccent, onBackClick = onBack)
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(padding)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search users...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Verified", "Unverified").forEach { filter ->
                    FilterChip(selected = selectedFilter == filter, onClick = { selectedFilter = filter }, label = { Text(filter) })
                }
            }
            Spacer(Modifier.height(8.dp))
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered) { user ->
                    UserManagementCard(user = user)
                }
            }
        }
    }
}

@Composable
private fun UserManagementCard(user: User) {
    var isBlocked by remember { mutableStateOf(false) }
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).background(AdminAccent.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(user.name.firstOrNull()?.uppercase() ?: "U", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AdminAccent)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(user.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    if (user.isVerified) {
                        Spacer(Modifier.width(6.dp))
                        Icon(Icons.Filled.Verified, contentDescription = null, tint = StatusDelivered, modifier = Modifier.size(14.dp))
                    }
                }
                Text(user.email, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text(user.phone, style = MaterialTheme.typography.bodySmall, color = TextHint)
            }
            Column(horizontalAlignment = Alignment.End) {
                Switch(
                    checked = !isBlocked,
                    onCheckedChange = { isBlocked = !it },
                    colors = SwitchDefaults.colors(checkedTrackColor = StatusDelivered, uncheckedTrackColor = StatusCancelled.copy(alpha = 0.4f))
                )
                Text(if (isBlocked) "Blocked" else "Active", style = MaterialTheme.typography.labelSmall, color = if (isBlocked) StatusCancelled else StatusDelivered)
            }
        }
    }
}

// ===== SHOP MANAGEMENT =====

@Composable
fun ShopManagementScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            GradientTopBar("Shop Management", subtitle = "${MockData.pharmacies.size} pharmacies", accentColor = AdminAccent, onBackClick = onBack)
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(padding)
        ) {
            items(MockData.pharmacies) { pharmacy ->
                AdminPharmacyCard(pharmacy = pharmacy)
            }
        }
    }
}

@Composable
private fun AdminPharmacyCard(pharmacy: Pharmacy) {
    var isVerified by remember { mutableStateOf(pharmacy.isOpen) }
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).background(PharmacyAccent.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.LocalPharmacy, contentDescription = null, tint = PharmacyAccent, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(pharmacy.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(pharmacy.ownerName, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Text("License: ${pharmacy.licenseNumber}", style = MaterialTheme.typography.bodySmall, color = TextHint)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = StatusDelivered.copy(alpha = 0.1f), shape = RoundedCornerShape(6.dp)) {
                        Text("⭐ ${pharmacy.rating}", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                    Surface(color = AdminAccent.copy(alpha = 0.1f), shape = RoundedCornerShape(6.dp)) {
                        Text("${pharmacy.totalOrders} orders", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (isVerified) "Active" else "Suspended", style = MaterialTheme.typography.labelSmall, color = if (isVerified) StatusDelivered else StatusCancelled)
                    Spacer(Modifier.width(8.dp))
                    Switch(
                        checked = isVerified,
                        onCheckedChange = { isVerified = it },
                        modifier = Modifier.scale(0.85f)
                    )
                }
            }
        }
    }
}

// ===== RIDER MANAGEMENT =====

@Composable
fun RiderManagementScreen(onBack: () -> Unit) {
    val riders = listOf(
        MockData.riderInfo,
        MockData.riderInfo.copy(id = "r2", name = "Kiran Reddy", phone = "+91 92345 67890", status = RiderStatus.OFFLINE, totalDeliveries = 156, rating = 4.6f),
        MockData.riderInfo.copy(id = "r3", name = "Divya Menon", phone = "+91 93456 78901", status = RiderStatus.ON_DELIVERY, totalDeliveries = 524, rating = 4.9f)
    )

    Scaffold(
        topBar = {
            GradientTopBar("Rider Management", subtitle = "${riders.size} riders", accentColor = AdminAccent, onBackClick = onBack)
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(padding)
        ) {
            items(riders) { rider ->
                AdminRiderCard(rider = rider)
            }
        }
    }
}

@Composable
private fun AdminRiderCard(rider: Rider) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(RiderAccent.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(rider.name.firstOrNull()?.uppercase() ?: "R", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = RiderAccent)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(rider.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(rider.phone, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text("${rider.vehicleType}  •  ${rider.vehicleNumber}", style = MaterialTheme.typography.bodySmall, color = TextHint)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("⭐ ${rider.rating}", style = MaterialTheme.typography.labelSmall)
                    Text("•", color = TextHint)
                    Text("${rider.totalDeliveries} deliveries", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }
            Surface(
                color = when (rider.status) {
                    RiderStatus.ONLINE -> StatusDelivered.copy(alpha = 0.12f)
                    RiderStatus.ON_DELIVERY -> StatusPending.copy(alpha = 0.12f)
                    RiderStatus.OFFLINE -> DividerColor
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    rider.status.name.replace("_", " "),
                    style = MaterialTheme.typography.labelSmall,
                    color = when (rider.status) {
                        RiderStatus.ONLINE -> StatusDelivered
                        RiderStatus.ON_DELIVERY -> StatusPending
                        RiderStatus.OFFLINE -> TextHint
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ===== PRESCRIPTION REVIEW =====

@Composable
fun AdminPrescriptionReviewScreen(onBack: () -> Unit) {
    val orders = MockData.generateSampleOrders().filter { it.requiresPrescription }

    Scaffold(
        topBar = {
            GradientTopBar("Prescription Review", subtitle = "${orders.size} pending", accentColor = AdminAccent, onBackClick = onBack)
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(padding)
        ) {
            items(orders) { order ->
                AdminPrescriptionCard(order = order)
            }
        }
    }
}

@Composable
private fun AdminPrescriptionCard(order: Order) {
    var isApproved by remember { mutableStateOf(order.prescriptionVerified) }

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(order.id, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AdminAccent)
                Surface(
                    color = (if (isApproved) StatusDelivered else StatusPending).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        if (isApproved) "Verified" else "Pending Review",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isApproved) StatusDelivered else StatusPending,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Customer: ${order.customerName}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text("Pharmacy: ${order.pharmacyName}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text("Date: ${order.createdAt.toShortDate()}", style = MaterialTheme.typography.bodySmall, color = TextHint)

            if (!isApproved) {
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCancelled),
                        border = BorderStroke(1.dp, StatusCancelled),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) { Text("Reject", style = MaterialTheme.typography.labelSmall) }
                    Button(
                        onClick = { isApproved = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AdminAccent),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) { Text("Approve", style = MaterialTheme.typography.labelSmall) }
                }
            }
        }
    }
}

// ===== DISPUTE HANDLING =====

@Composable
fun DisputeHandlingScreen(onBack: () -> Unit) {
    val disputes = listOf(
        Dispute("D001", "ORD-2024-0001", "Arjun Mehta", "Wrong Item", "Received Crocin instead of Combiflam", "OPEN"),
        Dispute("D002", "ORD-2024-0003", "Preethi Iyer", "Damaged Package", "Medicines were damaged on arrival", "OPEN"),
        Dispute("D003", "ORD-2024-0098", "Vijay Kumar", "Payment Issue", "Double charged for the order", "RESOLVED")
    )

    Scaffold(
        topBar = {
            GradientTopBar("Dispute Handling", subtitle = "${disputes.count { it.status == "OPEN" }} open", accentColor = AdminAccent, onBackClick = onBack)
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(padding)
        ) {
            items(disputes) { dispute ->
                DisputeCard(dispute = dispute)
            }
        }
    }
}

@Composable
private fun DisputeCard(dispute: Dispute) {
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        border = if (dispute.status == "OPEN") BorderStroke(1.dp, StatusCancelled.copy(alpha = 0.4f)) else null
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(dispute.id, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AdminAccent)
                    Text(dispute.orderId, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = (if (dispute.status == "OPEN") StatusCancelled else StatusDelivered).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            dispute.status,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (dispute.status == "OPEN") StatusCancelled else StatusDelivered,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null, tint = TextHint)
                }
            }

            if (expanded) {
                Spacer(Modifier.height(10.dp))
                Divider(color = DividerColor)
                Spacer(Modifier.height(10.dp))
                Text("Customer: ${dispute.raisedBy}", style = MaterialTheme.typography.bodySmall)
                Text("Reason: ${dispute.reason}", style = MaterialTheme.typography.bodySmall)
                Text("Description: ${dispute.description}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(Modifier.height(10.dp))

                if (dispute.status == "OPEN") {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Admin Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) { Text("Refund") }
                        Button(
                            onClick = {},
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AdminAccent),
                            contentPadding = PaddingValues(vertical = 10.dp)
                        ) { Text("Resolve") }
                    }
                }
            }
        }
    }
}

// ===== REPORTS =====

@Composable
fun ReportsScreen(onBack: () -> Unit) {
    val report = MockData.adminReport

    Scaffold(
        topBar = {
            GradientTopBar("Analytics & Reports", accentColor = AdminAccent, onBackClick = onBack)
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
            // Revenue hero card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.horizontalGradient(listOf(AdminAccent, PharmaTeal)))
                        .padding(20.dp)
                ) {
                    Column {
                        Text("Total Platform Revenue", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                        Text(report.totalRevenue.toRupees(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.height(4.dp))
                        Text("↑ 18.5% from last month", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Total Orders", "${report.totalOrders}", Icons.Filled.Receipt, AdminAccent, Modifier.weight(1f))
                StatCard("Active Orders", "${report.activeOrders}", Icons.Filled.Pending, StatusPending, Modifier.weight(1f))
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard("Total Users", "${report.totalUsers}", Icons.Filled.People, PharmacyAccent, Modifier.weight(1f))
                StatCard("Avg Order Value", "₹142", Icons.Filled.TrendingUp, RiderAccent, Modifier.weight(1f))
            }

            Spacer(Modifier.height(20.dp))

            // Placeholder chart area
            Text("Order Trends (Last 7 Days)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))

            Card(shape = RoundedCornerShape(14.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val values = listOf(42, 58, 75, 63, 89, 110, 95)
                    val maxVal = values.max()

                    Row(
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEachIndexed { i, day ->
                            val fraction = values[i].toFloat() / maxVal
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height((100 * fraction).dp)
                                        .background(AdminAccent.copy(alpha = 0.7f + 0.3f * fraction), RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        days.forEach { day ->
                            Text(day, style = MaterialTheme.typography.labelSmall, color = TextHint, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Top Performing Pharmacies", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))

            MockData.pharmacies.sortedByDescending { it.totalOrders }.forEach { pharmacy ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(10.dp)) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(pharmacy.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Text("${pharmacy.totalOrders} orders", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Spacer(Modifier.width(8.dp))
                        Text("⭐ ${pharmacy.rating}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

// ===== ADMIN SETTINGS =====

@Composable
fun AdminSettingsScreen(
    onRolesPermissions: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            GradientTopBar("Admin Settings", accentColor = AdminAccent, onBackClick = onBack)
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
            listOf(
                Triple(Icons.Filled.Security, "Roles & Permissions", onRolesPermissions),
                Triple(Icons.Filled.Notifications, "Push Notification Config", {}),
                Triple(Icons.Filled.CurrencyRupee, "Delivery Fee Settings", {}),
                Triple(Icons.Filled.VerifiedUser, "KYC Policy Settings", {}),
                Triple(Icons.Filled.Policy, "Platform Policies", {}),
                Triple(Icons.Filled.BugReport, "System Logs", {})
            ).forEach { (icon, label, action) ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable(onClick = action),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, contentDescription = null, tint = AdminAccent, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(14.dp))
                        Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextHint, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

// ===== ROLES & PERMISSIONS =====

@Composable
fun RolesPermissionsScreen(onBack: () -> Unit) {
    val roles = mapOf(
        "Admin" to listOf("Manage Users", "Manage Pharmacies", "Manage Riders", "View Reports", "Configure Settings", "Handle Disputes"),
        "Pharmacy" to listOf("Manage Own Inventory", "Process Orders", "Verify Prescriptions", "View Own Revenue"),
        "Rider" to listOf("Accept Orders", "Update Delivery Status", "View Own Earnings"),
        "Customer" to listOf("Place Orders", "Upload Prescriptions", "Track Orders", "Manage Profile")
    )

    Scaffold(
        topBar = {
            GradientTopBar("Roles & Permissions", subtitle = "Mock configuration", accentColor = AdminAccent, onBackClick = onBack)
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(padding)
        ) {
            items(roles.entries.toList()) { (role, permissions) ->
                RolePermissionCard(roleName = role, permissions = permissions)
            }
        }
    }
}

@Composable
private fun RolePermissionCard(roleName: String, permissions: List<String>) {
    val color = when (roleName) {
        "Admin" -> AdminAccent
        "Pharmacy" -> PharmacyAccent
        "Rider" -> RiderAccent
        else -> CustomerAccent
    }

    Card(shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Security, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text(roleName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
            }
            Spacer(Modifier.height(10.dp))
            permissions.forEach { perm ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 3.dp)) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = color.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(perm, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        }
    }
}
