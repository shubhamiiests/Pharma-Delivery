package com.pharmadelivery.ui.pharmacy.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.pharmadelivery.data.models.*
import com.pharmadelivery.data.repository.MockData
import com.pharmadelivery.ui.common.*
import com.pharmadelivery.ui.theme.*
import com.pharmadelivery.utils.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ===== PHARMACY DASHBOARD =====

@Composable
fun PharmacyDashboardScreen(
    pharmacy: User?,
    isOffline: Boolean,
    onIncomingOrders: () -> Unit,
    onMedicines: () -> Unit,
    onProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val orders = MockData.generateSampleOrders()
    val pendingPrescriptions = orders.count { it.requiresPrescription && !it.prescriptionVerified }
    val revenue = orders.filter { it.status == OrderStatus.DELIVERED }.sumOf { it.totalAmount }

    Scaffold(
        bottomBar = {
            NavigationBar {
                listOf(
                    Triple(Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "Dashboard"),
                    Triple(Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag, "Orders"),
                    Triple(Icons.Filled.Inventory, Icons.Outlined.Inventory2, "Medicines"),
                    Triple(Icons.Filled.Person, Icons.Outlined.Person, "Profile")
                ).forEachIndexed { i, (filledIcon, outlinedIcon, label) ->
                    NavigationBarItem(
                        selected = i == 0,
                        onClick = {
                            when (i) {
                                1 -> onIncomingOrders()
                                2 -> onMedicines()
                                3 -> onProfile()
                            }
                        },
                        icon = { Icon(if (i == 0) filledIcon else outlinedIcon, contentDescription = label) },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(indicatorColor = PharmacyAccent.copy(alpha = 0.15f))
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(PharmacyAccent, PharmacyAccent.copy(alpha = 0.8f))))
                    .padding(top = 48.dp, bottom = 24.dp, start = 20.dp, end = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Good Morning", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                        Text(pharmacy?.name ?: "Pharmacy", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(Color(0xFF76FF03), CircleShape))
                            Spacer(Modifier.width(6.dp))
                            Text("Shop is Open", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.9f))
                        }
                    }
                    IconButton(
                        onClick = onProfile,
                        modifier = Modifier.size(44.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
                    ) {
                        Icon(Icons.Filled.Store, contentDescription = null, tint = Color.White)
                    }
                }
            }

            if (isOffline) NoInternetBanner()

            Column(modifier = Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Orders Today", "${orders.size}", Icons.Filled.ShoppingBag, PharmacyAccent, Modifier.weight(1f))
                    StatCard("Pending Rx", "$pendingPrescriptions", Icons.Filled.DocumentScanner, StatusPending, Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("Revenue Today", revenue.toRupees(), Icons.Filled.CurrencyRupee, StatusDelivered, Modifier.weight(1f))
                    StatCard("In Stock", "${MockData.medicines.count { it.stockStatus == StockStatus.IN_STOCK }}", Icons.Filled.Inventory, PharmaTeal, Modifier.weight(1f))
                }

                Spacer(Modifier.height(20.dp))
                Text("Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PharmacyActionCard(Icons.Filled.ShoppingBag, "Incoming Orders", orders.size, PharmacyAccent, onIncomingOrders, Modifier.weight(1f))
                    PharmacyActionCard(Icons.Filled.DocumentScanner, "Verify Prescriptions", pendingPrescriptions, StatusPending, onIncomingOrders, Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PharmacyActionCard(Icons.Filled.Inventory, "Medicines", 0, PharmaTeal, onMedicines, Modifier.weight(1f))
                    PharmacyActionCard(Icons.Filled.BarChart, "Analytics", 0, AdminAccent, {}, Modifier.weight(1f))
                }

                Spacer(Modifier.height(20.dp))
                Text("Recent Orders", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                orders.take(3).forEach { order ->
                    PharmacyOrderItem(order = order, onProcessClick = onIncomingOrders)
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun PharmacyActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    badge: Int,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier.size(40.dp).background(color.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
                }
                if (badge > 0) {
                    Surface(color = color, shape = CircleShape) {
                        Text(
                            "$badge",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = color)
        }
    }
}

@Composable
private fun PharmacyOrderItem(order: Order, onProcessClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onProcessClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(42.dp).background(PharmacyAccent.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Receipt, contentDescription = null, tint = PharmacyAccent, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(order.id, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("${order.customerName}  •  ${order.items.size} items", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                OrderStatusChip(order.status)
                Spacer(Modifier.height(4.dp))
                Text(order.totalAmount.toRupees(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ===== INCOMING ORDERS =====

@Composable
fun IncomingOrdersScreen(
    pharmacyId: String,
    onBack: () -> Unit,
    onOrderClick: (String) -> Unit,
    onVerifyPrescription: (String) -> Unit
) {
    val orders = MockData.generateSampleOrders()
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Pending", "Preparing", "Prescription Needed")

    val filtered = when (selectedFilter) {
        "Pending" -> orders.filter { it.status == OrderStatus.PENDING }
        "Preparing" -> orders.filter { it.status == OrderStatus.PREPARING }
        "Prescription Needed" -> orders.filter { it.requiresPrescription && !it.prescriptionVerified }
        else -> orders
    }

    Scaffold(
        topBar = {
            GradientTopBar(
                title = "Incoming Orders",
                subtitle = "${orders.size} total orders",
                accentColor = PharmacyAccent,
                onBackClick = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) }
                    )
                }
            }

            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(Icons.Filled.Inbox, "No Orders", "No orders match this filter")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filtered) { order ->
                        IncomingOrderCard(
                            order = order,
                            onProcess = { onOrderClick(order.id) },
                            onVerify = { onVerifyPrescription(order.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IncomingOrderCard(
    order: Order,
    onProcess: () -> Unit,
    onVerify: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(order.id, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(order.customerName, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                OrderStatusChip(order.status)
            }

            Spacer(Modifier.height(10.dp))
            Divider(color = DividerColor)
            Spacer(Modifier.height(10.dp))

            order.items.take(2).forEach { item ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${item.medicine.name} x${item.quantity}", style = MaterialTheme.typography.bodySmall)
                    Text(item.totalPrice.toRupees(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
            }
            if (order.items.size > 2) {
                Text("+${order.items.size - 2} more items", style = MaterialTheme.typography.bodySmall, color = TextHint)
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total: ${order.totalAmount.toRupees()}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PharmacyAccent)

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (order.requiresPrescription && !order.prescriptionVerified) {
                        OutlinedButton(
                            onClick = onVerify,
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusPending),
                            border = BorderStroke(1.dp, StatusPending)
                        ) {
                            Text("Verify Rx", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Button(
                        onClick = onProcess,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PharmacyAccent)
                    ) {
                        Text("Process", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

// ===== PRESCRIPTION VERIFICATION =====

@Composable
fun PrescriptionVerifyScreen(
    orderId: String,
    onBack: () -> Unit,
    onVerified: () -> Unit
) {
    var notes by remember { mutableStateOf("") }
    var isApproving by remember { mutableStateOf(false) }
    var isRejecting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            GradientTopBar("Verify Prescription", subtitle = orderId, accentColor = PharmacyAccent, onBackClick = onBack)
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
            // Prescription image placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.DocumentScanner, contentDescription = null, tint = PharmacyAccent, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Prescription Image", style = MaterialTheme.typography.titleSmall, color = PharmacyAccent)
                    Text("Tap to zoom", style = MaterialTheme.typography.bodySmall, color = TextHint)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Order summary
            Card(shape = RoundedCornerShape(14.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Order Items Requiring Prescription", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    listOf("Azithromycin 500mg x1", "Metformin 500mg x2").forEach { item ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.MedicalServices, contentDescription = null, tint = PharmacyAccent, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(item, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Checklist
            val checks = remember {
                mutableStateListOf(
                    false to "Doctor's signature visible",
                    false to "Date is within 3 months",
                    false to "Patient name matches",
                    false to "Medicine names are legible"
                )
            }

            Card(shape = RoundedCornerShape(14.dp)) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Verification Checklist", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    checks.forEachIndexed { index, (checked, label) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable {
                                checks[index] = !checked to label
                            }.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { checks[index] = it to label },
                                colors = CheckboxDefaults.colors(checkedColor = PharmacyAccent)
                            )
                            Text(label, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                placeholder = { Text("Add pharmacist notes...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                minLines = 3
            )

            Spacer(Modifier.height(20.dp))

            val allChecked = checks.all { it.first }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            isRejecting = true
                            delay(800)
                            isRejecting = false
                            onVerified()
                        }
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = StatusCancelled),
                    border = BorderStroke(1.dp, StatusCancelled)
                ) {
                    if (isRejecting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = StatusCancelled, strokeWidth = 2.dp)
                    } else {
                        Text("Reject")
                    }
                }
                Button(
                    onClick = {
                        scope.launch {
                            isApproving = true
                            delay(1000)
                            isApproving = false
                            onVerified()
                        }
                    },
                    enabled = allChecked,
                    modifier = Modifier.weight(2f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PharmacyAccent)
                ) {
                    if (isApproving) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Approve Prescription")
                    }
                }
            }

            if (!allChecked) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Complete all checklist items to approve",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextHint,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ===== ORDER PROCESSING =====

@Composable
fun OrderProcessingScreen(orderId: String, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    var currentStatus by remember { mutableStateOf(OrderStatus.CONFIRMED) }
    var isUpdating by remember { mutableStateOf(false) }

    val statusFlow = listOf(
        OrderStatus.CONFIRMED,
        OrderStatus.PREPARING,
        OrderStatus.READY_FOR_PICKUP,
        OrderStatus.RIDER_ASSIGNED
    )

    Scaffold(
        topBar = {
            GradientTopBar("Process Order", subtitle = orderId, accentColor = PharmacyAccent, onBackClick = onBack)
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
            // Current status card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PharmacyAccent.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Current Status", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Spacer(Modifier.height(8.dp))
                    OrderStatusChip(currentStatus)
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { currentStatus.toProgress() },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = PharmacyAccent
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Status update buttons
            Text("Update Status", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))

            statusFlow.forEach { status ->
                val isDone = statusFlow.indexOf(currentStatus) >= statusFlow.indexOf(status)
                val isCurrent = currentStatus == status
                val isNext = statusFlow.indexOf(status) == statusFlow.indexOf(currentStatus) + 1

                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isCurrent) BorderStroke(2.dp, PharmacyAccent) else null,
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isDone -> PharmacyAccent.copy(alpha = 0.08f)
                            else -> MaterialTheme.colorScheme.surface
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(28.dp).background(
                                if (isDone) PharmacyAccent else DividerColor,
                                CircleShape
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone) Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Text(
                            status.displayName(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isDone) PharmacyAccent else TextSecondary,
                            modifier = Modifier.weight(1f)
                        )
                        if (isNext) {
                            Button(
                                onClick = {
                                    scope.launch {
                                        isUpdating = true
                                        delay(800)
                                        currentStatus = status
                                        isUpdating = false
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PharmacyAccent),
                                enabled = !isUpdating
                            ) {
                                Text("Mark", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ===== MEDICINE MANAGEMENT =====

@Composable
fun MedicineManagementScreen(
    pharmacyId: String,
    onBack: () -> Unit,
    onAddMedicine: () -> Unit,
    onEditMedicine: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val medicines = MockData.medicines.filter { searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        topBar = {
            GradientTopBar(
                title = "Medicine Inventory",
                subtitle = "${MockData.medicines.size} items",
                accentColor = PharmacyAccent,
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = onAddMedicine) {
                        Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddMedicine,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Add Medicine") },
                containerColor = PharmacyAccent
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search medicines...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Stock summary
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("In Stock", MockData.medicines.count { it.stockStatus == StockStatus.IN_STOCK }, StatusDelivered),
                    Triple("Low Stock", MockData.medicines.count { it.stockStatus == StockStatus.LOW_STOCK }, StatusPending),
                    Triple("Out of Stock", MockData.medicines.count { it.stockStatus == StockStatus.OUT_OF_STOCK }, StatusCancelled)
                ).forEach { (label, count, color) ->
                    Surface(
                        color = color.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("$count", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
                            Text(label, style = MaterialTheme.typography.labelSmall, color = color, textAlign = TextAlign.Center)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(medicines) { medicine ->
                    InventoryMedicineCard(
                        medicine = medicine,
                        onEdit = { onEditMedicine(medicine.id) }
                    )
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun InventoryMedicineCard(medicine: Medicine, onEdit: () -> Unit) {
    val stockColor = when (medicine.stockStatus) {
        StockStatus.IN_STOCK -> StatusDelivered
        StockStatus.LOW_STOCK -> StatusPending
        StockStatus.OUT_OF_STOCK -> StatusCancelled
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(PharmacyAccent.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.MedicalServices, contentDescription = null, tint = PharmacyAccent, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(medicine.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(medicine.genericName, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(medicine.price.toRupees(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = PharmacyAccent)
                    Surface(color = stockColor.copy(alpha = 0.12f), shape = RoundedCornerShape(4.dp)) {
                        Text(
                            medicine.stockStatus.name.replace("_", " "),
                            style = MaterialTheme.typography.labelSmall,
                            color = stockColor,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Qty: ${medicine.stockCount}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(Modifier.height(4.dp))
                IconButton(onClick = onEdit, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = PharmacyAccent, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

// ===== ADD / EDIT MEDICINE =====

@Composable
fun AddEditMedicineScreen(medicineId: String, onBack: () -> Unit) {
    val existing = MockData.medicines.find { it.id == medicineId }
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var genericName by remember { mutableStateOf(existing?.genericName ?: "") }
    var price by remember { mutableStateOf(existing?.price?.toString() ?: "") }
    var mrp by remember { mutableStateOf(existing?.mrp?.toString() ?: "") }
    var stock by remember { mutableStateOf(existing?.stockCount?.toString() ?: "") }
    var requiresPrescription by remember { mutableStateOf(existing?.requiresPrescription ?: false) }
    var category by remember { mutableStateOf(existing?.category ?: "General") }
    var isSaving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            GradientTopBar(
                title = if (medicineId == "new") "Add Medicine" else "Edit Medicine",
                accentColor = PharmacyAccent,
                onBackClick = onBack
            )
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
            PharmaTextField(value = name, onValueChange = { name = it }, label = "Medicine Name", leadingIcon = Icons.Filled.MedicalServices)
            Spacer(Modifier.height(12.dp))
            PharmaTextField(value = genericName, onValueChange = { genericName = it }, label = "Generic Name")
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PharmaTextField(value = price, onValueChange = { price = it }, label = "Price (₹)", modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
                PharmaTextField(value = mrp, onValueChange = { mrp = it }, label = "MRP (₹)", modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            }
            Spacer(Modifier.height(12.dp))
            PharmaTextField(value = stock, onValueChange = { stock = it }, label = "Stock Quantity", leadingIcon = Icons.Filled.Inventory, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            Spacer(Modifier.height(12.dp))

            Text("Category", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(MockData.categories) { cat ->
                    FilterChip(selected = category == cat, onClick = { category = cat }, label = { Text(cat, style = MaterialTheme.typography.labelSmall) })
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Requires Prescription", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    Text("Customers must upload a valid Rx", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Switch(checked = requiresPrescription, onCheckedChange = { requiresPrescription = it })
            }

            Spacer(Modifier.height(24.dp))

            PharmaButton(
                text = if (medicineId == "new") "Add Medicine" else "Save Changes",
                loading = isSaving,
                enabled = name.isNotBlank() && price.isNotBlank(),
                onClick = {
                    scope.launch {
                        isSaving = true
                        delay(1000)
                        isSaving = false
                        onBack()
                    }
                }
            )
        }
    }
}

// ===== PHARMACY PROFILE =====

@Composable
fun PharmacyProfileScreen(
    user: User?,
    onKYC: () -> Unit,
    onSupport: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    var showLogout by remember { mutableStateOf(false) }
    if (showLogout) {
        AlertDialog(
            onDismissRequest = { showLogout = false },
            title = { Text("Log Out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = { TextButton(onClick = onLogout) { Text("Log Out", color = StatusCancelled) } },
            dismissButton = { TextButton(onClick = { showLogout = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            GradientTopBar("Shop Profile", accentColor = PharmacyAccent, onBackClick = onBack)
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
                color = PharmacyAccent.copy(alpha = 0.08f)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(80.dp).background(PharmacyAccent.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.LocalPharmacy, contentDescription = null, tint = PharmacyAccent, modifier = Modifier.size(40.dp))
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(user?.name ?: "MedPlus Pharmacy", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(user?.email ?: "pharmacy@example.com", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Surface(color = StatusDelivered.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                                Text(" 4.7 Rating", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        Surface(color = PharmacyAccent.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                            Text("Verified", style = MaterialTheme.typography.labelSmall, color = PharmacyAccent, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ProfileMenuItemPharmacy(icon = Icons.Filled.Edit, label = "Edit Shop Details", onClick = {})
                ProfileMenuItemPharmacy(icon = Icons.Filled.AccountBalance, label = "Bank & KYC Details", onClick = onKYC)
                ProfileMenuItemPharmacy(icon = Icons.Filled.Schedule, label = "Shop Hours", onClick = {})
                ProfileMenuItemPharmacy(icon = Icons.Filled.Headset, label = "Support", onClick = onSupport)
                ProfileMenuItemPharmacy(icon = Icons.Filled.Logout, label = "Log Out", onClick = { showLogout = true }, isDestructive = true)
            }
        }
    }
}

@Composable
private fun ProfileMenuItemPharmacy(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = if (isDestructive) StatusCancelled else PharmacyAccent, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(16.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, color = if (isDestructive) StatusCancelled else MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextHint, modifier = Modifier.size(18.dp))
        }
    }
}

// ===== PHARMACY KYC =====

@Composable
fun PharmacyKYCScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            GradientTopBar("Bank & KYC Details", accentColor = PharmacyAccent, onBackClick = onBack)
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
                    Icon(Icons.Filled.Info, contentDescription = null, tint = StatusPending, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("KYC verification is required for payouts. This is a mock screen.", style = MaterialTheme.typography.bodySmall, color = StatusPending)
                }
            }

            Spacer(Modifier.height(16.dp))

            listOf(
                "PAN Card" to "Uploaded ✓",
                "Pharmacy License" to "Verified ✓",
                "GST Certificate" to "Pending",
                "Bank Account" to "Linked ✓",
                "FSSAI License" to "Not Uploaded"
            ).forEach { (doc, status) ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), shape = RoundedCornerShape(10.dp)) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Article, contentDescription = null, tint = PharmacyAccent, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(doc, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text(
                            status,
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                status.contains("✓") -> StatusDelivered
                                status == "Pending" -> StatusPending
                                else -> StatusCancelled
                            }
                        )
                    }
                }
            }
        }
    }
}
