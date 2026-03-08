package com.pharmadelivery.ui.customer.screens

import androidx.compose.animation.*
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

// ===== CART =====

@Composable
fun CartScreen(
    onBack: () -> Unit,
    onCheckout: () -> Unit
) {
    // Pre-populate cart for demo
    val cartItems = remember {
        mutableStateListOf(
            CartItem(MockData.medicines[0], 2),
            CartItem(MockData.medicines[5], 1),
            CartItem(MockData.medicines[4], 1)
        )
    }

    val subtotal = cartItems.sumOf { it.totalPrice }
    val delivery = 30.0
    val total = subtotal + delivery

    Scaffold(
        topBar = {
            GradientTopBar(
                title = "My Cart",
                subtitle = "${cartItems.size} items",
                accentColor = CustomerAccent,
                onBackClick = onBack
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                total.toRupees(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = CustomerAccent
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        PharmaButton("Proceed to Checkout", onClick = onCheckout, icon = Icons.Filled.ArrowForward)
                    }
                }
            }
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    icon = Icons.Filled.ShoppingCartCheckout,
                    title = "Cart is Empty",
                    subtitle = "Browse medicines and add them to your cart",
                    actionText = "Browse Medicines",
                    onAction = onBack
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(padding)
            ) {
                // Prescription required warning
                if (cartItems.any { it.medicine.requiresPrescription }) {
                    item {
                        Surface(
                            color = StatusPending.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Warning, contentDescription = null, tint = StatusPending, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Some items require a valid prescription. You'll be asked to upload it.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = StatusPending
                                )
                            }
                        }
                    }
                }

                items(cartItems) { item ->
                    CartItemRow(
                        item = item,
                        onQuantityChange = { newQty ->
                            val index = cartItems.indexOf(item)
                            if (newQty == 0) {
                                cartItems.removeAt(index)
                            } else {
                                cartItems[index] = item.copy(quantity = newQty)
                            }
                        }
                    )
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    Card(shape = RoundedCornerShape(14.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Bill Summary", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(12.dp))
                            Divider(color = DividerColor)
                            Spacer(Modifier.height(8.dp))
                            OrderSummaryRow("Subtotal", subtotal.toRupees())
                            OrderSummaryRow("Delivery Fee", delivery.toRupees())
                            OrderSummaryRow("Discount", "−₹0.00")
                            Spacer(Modifier.height(8.dp))
                            Divider(color = DividerColor)
                            Spacer(Modifier.height(8.dp))
                            OrderSummaryRow("Total Amount", total.toRupees(), isTotal = true)
                        }
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun CartItemRow(item: CartItem, onQuantityChange: (Int) -> Unit) {
    Card(shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(PharmaGreen.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.MedicalServices, contentDescription = null, tint = PharmaGreen, modifier = Modifier.size(24.dp))
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(item.medicine.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(item.medicine.brand, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text(item.medicine.price.toRupees(), style = MaterialTheme.typography.bodySmall, color = CustomerAccent, fontWeight = FontWeight.Bold)
            }

            // Qty stepper
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                IconButton(
                    onClick = { onQuantityChange(item.quantity - 1) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                }
                Text(
                    "${item.quantity}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(
                    onClick = { onQuantityChange(item.quantity + 1) },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ===== ADDRESS SELECTION =====

@Composable
fun AddressScreen(
    onBack: () -> Unit,
    onAddAddress: () -> Unit,
    onAddressSelected: () -> Unit
) {
    var selectedAddress by remember { mutableStateOf(MockData.sampleAddresses.first().id) }

    Scaffold(
        topBar = {
            GradientTopBar("Delivery Address", accentColor = CustomerAccent, onBackClick = onBack)
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                PharmaButton(
                    "Deliver to Selected Address",
                    onClick = onAddressSelected,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp)
        ) {
            MockData.sampleAddresses.forEach { address ->
                AddressCard(
                    address = address,
                    isSelected = selectedAddress == address.id,
                    onClick = { selectedAddress = address.id }
                )
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(8.dp))

            OutlinedButton(
                onClick = onAddAddress,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add New Address")
            }
        }
    }
}

@Composable
private fun AddressCard(address: Address, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        border = if (isSelected) BorderStroke(2.dp, CustomerAccent) else null,
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = CustomerAccent)
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(address.label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    if (address.isDefault) {
                        Spacer(Modifier.width(8.dp))
                        Surface(color = CustomerAccent.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                            Text("Default", style = MaterialTheme.typography.labelSmall, color = CustomerAccent, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(address.street, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text("${address.city}, ${address.state} - ${address.pincode}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                if (address.landmark.isNotBlank()) {
                    Text("Near: ${address.landmark}", style = MaterialTheme.typography.bodySmall, color = TextHint)
                }
            }
        }
    }
}

@Composable
fun AddAddressScreen(onBack: () -> Unit) {
    var label by remember { mutableStateOf("Home") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            GradientTopBar("Add New Address", accentColor = CustomerAccent, onBackClick = onBack)
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
            // Label selector
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf("Home", "Office", "Other").forEach { lbl ->
                    FilterChip(
                        selected = label == lbl,
                        onClick = { label = lbl },
                        label = { Text(lbl) }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            PharmaTextField(value = street, onValueChange = { street = it }, label = "Street / Flat Number", leadingIcon = Icons.Filled.Home)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PharmaTextField(value = city, onValueChange = { city = it }, label = "City", modifier = Modifier.weight(1f))
                PharmaTextField(value = pincode, onValueChange = { pincode = it }, label = "Pincode", modifier = Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            PharmaTextField(value = landmark, onValueChange = { landmark = it }, label = "Landmark (optional)", leadingIcon = Icons.Filled.Place)
            Spacer(Modifier.height(24.dp))
            PharmaButton("Save Address", onClick = onBack, icon = Icons.Filled.Check)
        }
    }
}

// ===== CHECKOUT =====

@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onOrderPlaced: (Boolean) -> Unit
) {
    var selectedPayment by remember { mutableStateOf(PaymentMethod.UPI) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            GradientTopBar("Checkout", accentColor = CustomerAccent, onBackClick = onBack)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Delivery address
                SectionCard(title = "Delivering To") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null, tint = CustomerAccent, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Home", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(MockData.sampleAddresses.first().street, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text("Bangalore, Karnataka - 560038", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Order summary
                SectionCard(title = "Order Summary") {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        MockData.medicines.take(2).forEach { med ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(med.name, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                Text(med.price.toRupees(), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Divider(color = DividerColor, modifier = Modifier.padding(vertical = 4.dp))
                        OrderSummaryRow("Subtotal", "₹131.00")
                        OrderSummaryRow("Delivery Fee", "₹30.00")
                        OrderSummaryRow("Discount", "−₹0.00")
                        Divider(color = DividerColor, modifier = Modifier.padding(vertical = 4.dp))
                        OrderSummaryRow("Total", "₹161.00", isTotal = true)
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Payment method
                SectionCard(title = "Payment Method") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PaymentMethod.values().forEach { method ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedPayment = method }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedPayment == method,
                                    onClick = { selectedPayment = method },
                                    colors = RadioButtonDefaults.colors(selectedColor = CustomerAccent)
                                )
                                Spacer(Modifier.width(8.dp))
                                Icon(
                                    when (method) {
                                        PaymentMethod.UPI -> Icons.Filled.QrCode
                                        PaymentMethod.CARD -> Icons.Filled.CreditCard
                                        PaymentMethod.CASH_ON_DELIVERY -> Icons.Filled.Money
                                        PaymentMethod.WALLET -> Icons.Filled.AccountBalanceWallet
                                    },
                                    contentDescription = null,
                                    tint = if (selectedPayment == method) CustomerAccent else TextHint,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    when (method) {
                                        PaymentMethod.UPI -> "UPI / Net Banking"
                                        PaymentMethod.CARD -> "Credit / Debit Card"
                                        PaymentMethod.CASH_ON_DELIVERY -> "Cash on Delivery"
                                        PaymentMethod.WALLET -> "Wallet"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (selectedPayment == method) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                PharmaButton(
                    text = "Place Order  •  ₹161.00",
                    loading = isLoading,
                    onClick = {
                        scope.launch {
                            isLoading = true
                            delay(1800)
                            isLoading = false
                            onOrderPlaced(true)
                        }
                    }
                )

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

// ===== PAYMENT RESULT =====

@Composable
fun PaymentResultScreen(
    success: Boolean,
    onViewOrder: () -> Unit,
    onBackToHome: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    (if (success) StatusDelivered else StatusCancelled).copy(alpha = 0.1f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (success) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                contentDescription = null,
                tint = if (success) StatusDelivered else StatusCancelled,
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            if (success) "Order Placed Successfully!" else "Payment Failed",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            if (success) "Your order has been placed. The pharmacy will confirm and prepare your medicines shortly."
            else "Something went wrong with your payment. Please try again.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = TextSecondary
        )

        if (success) {
            Spacer(Modifier.height(12.dp))
            Surface(color = PharmaGreen.copy(alpha = 0.08f), shape = RoundedCornerShape(10.dp)) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Tag, contentDescription = null, tint = PharmaGreen, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Order ID: ORD-2024-0004", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = PharmaGreen)
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        if (success) {
            PharmaButton("Track Your Order", onClick = onViewOrder, icon = Icons.Filled.TrackChanges)
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onBackToHome,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Back to Home")
            }
        } else {
            PharmaButton("Retry Payment", onClick = onBackToHome)
        }
    }
}

// ===== ORDER HISTORY =====

@Composable
fun OrderHistoryScreen(
    customerId: String,
    onBack: () -> Unit,
    onOrderClick: (String) -> Unit
) {
    val orders = MockData.generateSampleOrders()

    Scaffold(
        topBar = {
            GradientTopBar(
                title = "My Orders",
                subtitle = "${orders.size} orders",
                accentColor = CustomerAccent,
                onBackClick = onBack
            )
        }
    ) { padding ->
        if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState(icon = Icons.Filled.Receipt, title = "No Orders Yet", subtitle = "Your order history will appear here")
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(padding)
            ) {
                items(orders) { order ->
                    OrderHistoryCard(order = order, onClick = { onOrderClick(order.id) })
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryCard(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    order.id,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = CustomerAccent
                )
                OrderStatusChip(order.status)
            }

            Spacer(Modifier.height(8.dp))

            Text(order.pharmacyName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(
                "${order.items.size} item(s)  •  ${order.createdAt.toShortDate()}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    order.totalAmount.toRupees(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("View Details", style = MaterialTheme.typography.labelSmall, color = CustomerAccent)
                    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = CustomerAccent, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ===== ORDER DETAIL =====

@Composable
fun OrderDetailScreen(
    orderId: String,
    onBack: () -> Unit,
    onTrackOrder: () -> Unit
) {
    val order = MockData.generateSampleOrders().find { it.id == orderId }
        ?: MockData.generateSampleOrders().first()

    Scaffold(
        topBar = {
            GradientTopBar("Order Details", accentColor = CustomerAccent, onBackClick = onBack)
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
            // Header card
            Card(shape = RoundedCornerShape(14.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(order.id, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(order.createdAt.toReadableDate(), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        OrderStatusChip(order.status)
                    }

                    if (order.status != OrderStatus.DELIVERED && order.status != OrderStatus.CANCELLED) {
                        Spacer(Modifier.height(12.dp))
                        PharmaButton(
                            "Track Order",
                            onClick = onTrackOrder,
                            icon = Icons.Filled.TrackChanges,
                            modifier = Modifier
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Items
            SectionCard("Ordered Items") {
                order.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.medicine.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text("Qty: ${item.quantity}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        Text(item.totalPrice.toRupees(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    Divider(color = DividerColor, modifier = Modifier.padding(vertical = 6.dp))
                }
                OrderSummaryRow("Subtotal", order.subtotal.toRupees())
                OrderSummaryRow("Delivery Fee", order.deliveryFee.toRupees())
                Divider(color = DividerColor, modifier = Modifier.padding(vertical = 6.dp))
                OrderSummaryRow("Total", order.totalAmount.toRupees(), isTotal = true)
            }

            Spacer(Modifier.height(12.dp))

            // Delivery info
            SectionCard("Delivery Information") {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = CustomerAccent, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Delivery Address", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(order.deliveryAddress.street, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Payment, contentDescription = null, tint = CustomerAccent, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("Payment", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("${order.paymentMethod.name.replace("_", " ")}  •  ${order.paymentStatus.name}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ===== ORDER TRACKING =====

@Composable
fun OrderTrackingScreen(orderId: String, onBack: () -> Unit) {
    val order = MockData.generateSampleOrders().find { it.id == orderId }
        ?: MockData.generateSampleOrders().first()

    Scaffold(
        topBar = {
            GradientTopBar("Track Order", subtitle = order.id, accentColor = CustomerAccent, onBackClick = onBack)
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
            // Progress bar
            LinearProgressIndicator(
                progress = { order.status.toProgress() },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                color = CustomerAccent,
                trackColor = CustomerAccent.copy(alpha = 0.2f)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "${(order.status.toProgress() * 100).toInt()}% Complete  •  Est: ${order.estimatedDelivery}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Spacer(Modifier.height(20.dp))

            // Mock map placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Map, contentDescription = null, tint = TextHint, modifier = Modifier.size(48.dp))
                    Text("Live tracking map", style = MaterialTheme.typography.bodySmall, color = TextHint)
                    Text("(Available in production build)", style = MaterialTheme.typography.labelSmall, color = TextHint)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Rider info
            if (order.riderName.isNotBlank()) {
                Card(shape = RoundedCornerShape(14.dp)) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(RiderAccent.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.DeliveryDining, contentDescription = null, tint = RiderAccent, modifier = Modifier.size(24.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Your Rider", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text(order.riderName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("⭐ 4.8  •  KA 05 HB 9988", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                        IconButton(onClick = {}) {
                            Icon(Icons.Filled.Call, contentDescription = "Call rider", tint = PharmaGreen)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Timeline
            Card(shape = RoundedCornerShape(14.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Order Timeline", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))

                    order.trackingUpdates.forEachIndexed { index, update ->
                        TrackingTimelineItem(
                            update = update,
                            isFirst = index == 0,
                            isLast = index == order.trackingUpdates.lastIndex,
                            isCompleted = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackingTimelineItem(
    update: TrackingUpdate,
    isFirst: Boolean,
    isLast: Boolean,
    isCompleted: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(if (isCompleted) PharmaGreen else DividerColor, CircleShape)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(if (isCompleted) PharmaGreen.copy(alpha = 0.4f) else DividerColor)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.padding(bottom = if (!isLast) 8.dp else 0.dp)) {
            Text(
                update.message,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isLast) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                update.timestamp.toReadableDate(),
                style = MaterialTheme.typography.bodySmall,
                color = TextHint
            )
        }
    }
}

// ===== PROFILE =====

@Composable
fun CustomerProfileScreen(
    user: User?,
    onEditProfile: () -> Unit,
    onOrderHistory: () -> Unit,
    onSupport: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val u = user ?: MockData.currentCustomer
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out") },
            text = { Text("Are you sure you want to log out?") },
            confirmButton = {
                TextButton(onClick = onLogout) { Text("Log Out", color = StatusCancelled) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        bottomBar = {
            CustomerBottomBar(selectedTab = 3, onHome = {}, onOrders = onOrderHistory, onSearch = {}, onProfile = {})
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(CustomerAccent, CustomerAccent.copy(alpha = 0.7f))))
                    .padding(top = 48.dp, bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.White.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            u.name.firstOrNull()?.uppercase() ?: "A",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(u.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(u.email, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    if (u.isVerified) {
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Verified, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                            Text("  Verified Account", style = MaterialTheme.typography.labelSmall, color = Color.White)
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ProfileMenuItem(icon = Icons.Filled.Edit, label = "Edit Profile", onClick = onEditProfile)
                ProfileMenuItem(icon = Icons.Filled.Receipt, label = "Order History", onClick = onOrderHistory)
                ProfileMenuItem(icon = Icons.Filled.LocationOn, label = "Saved Addresses", onClick = {})
                ProfileMenuItem(icon = Icons.Filled.Notifications, label = "Notifications", onClick = {})
                ProfileMenuItem(icon = Icons.Filled.Headset, label = "Support & Help", onClick = onSupport)
                ProfileMenuItem(icon = Icons.Filled.Settings, label = "Settings", onClick = onSettings)
                ProfileMenuItem(icon = Icons.Filled.Logout, label = "Log Out", onClick = { showLogoutDialog = true }, isDestructive = true)
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isDestructive) StatusCancelled else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (isDestructive) StatusCancelled else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextHint, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
fun EditProfileScreen(user: User?, onBack: () -> Unit) {
    val u = user ?: MockData.currentCustomer
    var name by remember { mutableStateOf(u.name) }
    var email by remember { mutableStateOf(u.email) }
    var phone by remember { mutableStateOf(u.phone) }
    var isSaving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            GradientTopBar("Edit Profile", accentColor = CustomerAccent, onBackClick = onBack)
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
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(CustomerAccent.copy(alpha = 0.15f), CircleShape)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Text(u.name.firstOrNull()?.uppercase() ?: "A", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = CustomerAccent)
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(CustomerAccent, CircleShape)
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }

            Spacer(Modifier.height(24.dp))
            PharmaTextField(value = name, onValueChange = { name = it }, label = "Full Name", leadingIcon = Icons.Filled.Person)
            Spacer(Modifier.height(14.dp))
            PharmaTextField(value = email, onValueChange = { email = it }, label = "Email", leadingIcon = Icons.Filled.Email)
            Spacer(Modifier.height(14.dp))
            PharmaTextField(value = phone, onValueChange = { phone = it }, label = "Phone", leadingIcon = Icons.Filled.Phone)
            Spacer(Modifier.height(24.dp))
            PharmaButton("Save Changes", loading = isSaving, onClick = {
                scope.launch {
                    isSaving = true; delay(1000); isSaving = false; onBack()
                }
            })
        }
    }
}

@Composable
fun SupportScreen(onBack: () -> Unit) {
    val topics = listOf("Track my order", "Cancel order", "Return / Refund", "Wrong medicine delivered", "Payment issue", "Other")

    Scaffold(
        topBar = {
            GradientTopBar("Support & Help", accentColor = CustomerAccent, onBackClick = onBack)
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
            Surface(color = PharmaGreen.copy(alpha = 0.08f), shape = RoundedCornerShape(14.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.SupportAgent, contentDescription = null, tint = PharmaGreen, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Need Help?", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("Our support team is available 24/7", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Common Issues", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            topics.forEach { topic ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable {}, shape = RoundedCornerShape(10.dp)) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.HelpOutline, contentDescription = null, tint = PharmaGreen, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(topic, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextHint, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }
    var locationEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            GradientTopBar("Settings", accentColor = CustomerAccent, onBackClick = onBack)
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
            Text("Preferences", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(Modifier.height(8.dp))

            SettingsToggle("Push Notifications", Icons.Filled.Notifications, notificationsEnabled) { notificationsEnabled = it }
            SettingsToggle("Dark Mode", Icons.Filled.DarkMode, darkModeEnabled) { darkModeEnabled = it }
            SettingsToggle("Share Location", Icons.Filled.LocationOn, locationEnabled) { locationEnabled = it }

            Spacer(Modifier.height(16.dp))
            Text("About", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(Modifier.height(8.dp))

            Card(shape = RoundedCornerShape(12.dp)) {
                Column {
                    listOf("Privacy Policy", "Terms of Service", "App Version 1.0.0").forEachIndexed { i, item ->
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(item, style = MaterialTheme.typography.bodyMedium)
                            if (i < 2) Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextHint, modifier = Modifier.size(18.dp))
                        }
                        if (i < 2) Divider(color = DividerColor, modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsToggle(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            Switch(checked = value, onCheckedChange = onToggle)
        }
    }
}
