package com.pharmadelivery.ui.customer.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
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

// ===== CUSTOMER HOME =====

@Composable
fun CustomerHomeScreen(
    isOffline: Boolean,
    onBrowseShops: () -> Unit,
    onSearch: () -> Unit,
    onUploadPrescription: () -> Unit,
    onOrderHistory: () -> Unit,
    onProfile: () -> Unit,
    onCategoryClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val user = MockData.currentCustomer

    Scaffold(
        bottomBar = {
            CustomerBottomBar(
                selectedTab = 0,
                onHome = {},
                onOrders = onOrderHistory,
                onSearch = onSearch,
                onProfile = onProfile
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(bottom = padding.calculateBottomPadding())
        ) {
            // Top gradient header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(listOf(CustomerAccent, CustomerAccent.copy(blue = 0.95f)))
                    )
                    .padding(top = 48.dp, bottom = 28.dp, start = 20.dp, end = 20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                "Good morning,",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                user.name.substringBefore(" ") + " 👋",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        IconButton(
                            onClick = onProfile,
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(Icons.Filled.Person, contentDescription = "Profile", tint = Color.White)
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Delivery address chip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Delivering to: Koramangala, Bangalore",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            maxLines = 1
                        )
                        Icon(
                            Icons.Filled.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            if (isOffline) {
                NoInternetBanner()
            }

            // Search bar (tappable, opens search screen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .offset(y = (-20).dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    shadowElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onSearch)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = null,
                            tint = TextHint,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Search medicines, health products...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextHint
                        )
                    }
                }
            }

            // Quick actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    icon = Icons.Filled.CameraAlt,
                    label = "Upload\nPrescription",
                    color = PharmaGreen,
                    onClick = onUploadPrescription,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Filled.Store,
                    label = "Browse\nPharmacies",
                    color = PharmaTeal,
                    onClick = onBrowseShops,
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    icon = Icons.Filled.LocalOffer,
                    label = "Today's\nOffers",
                    color = RiderAccent,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(8.dp))

            // Promotional banner
            PromoBanner()

            // Categories
            SectionHeader(title = "Shop by Category", actionText = "View All", onActionClick = onCategoryClick)

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(MockData.categories) { category ->
                    CategoryChip(category = category, onClick = onCategoryClick)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Nearby pharmacies
            SectionHeader(title = "Nearby Pharmacies", actionText = "See All", onActionClick = onBrowseShops)

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(MockData.pharmacies) { pharmacy ->
                    PharmacyCard(pharmacy = pharmacy, onClick = onBrowseShops)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Popular medicines
            SectionHeader(title = "Popular Medicines", actionText = "View All")

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MockData.medicines.take(4).forEach { medicine ->
                    MedicineCard(
                        name = medicine.name,
                        genericName = medicine.genericName,
                        price = medicine.price,
                        mrp = medicine.mrp,
                        requiresPrescription = medicine.requiresPrescription,
                        stockStatus = medicine.stockStatus,
                        onAddToCart = {}
                    )
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .aspectRatio(0.9f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
private fun PromoBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(listOf(PharmaGreen, PharmaTeal))
                )
                .padding(16.dp)
        ) {
            Column {
                Text(
                    "First Order Offer",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    "20% OFF",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "on your first order. Use code PHARMA20",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            Icon(
                Icons.Filled.MedicalServices,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun CategoryChip(category: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.LocalHospital,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(6.dp))
            Text(category, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun PharmacyCard(pharmacy: Pharmacy, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(PharmaGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.LocalPharmacy,
                    contentDescription = null,
                    tint = PharmaGreen,
                    modifier = Modifier.size(40.dp)
                )
                if (!pharmacy.isOpen) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "CLOSED",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    pharmacy.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        " ${pharmacy.rating}  •  ${pharmacy.estimatedDeliveryTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

// ===== BROWSE SHOPS =====

@Composable
fun BrowseShopsScreen(
    onShopClick: (String) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = MockData.pharmacies.filter {
        searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            GradientTopBar(
                title = "Pharmacies Near You",
                subtitle = "Bangalore, Karnataka",
                accentColor = CustomerAccent,
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
            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search pharmacies...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (filtered.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.SearchOff,
                    title = "No Pharmacies Found",
                    subtitle = "Try a different search term"
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered) { pharmacy ->
                        PharmacyListItem(pharmacy = pharmacy, onClick = { onShopClick(pharmacy.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun PharmacyListItem(pharmacy: Pharmacy, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(PharmaGreen.copy(alpha = 0.1f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.LocalPharmacy, contentDescription = null, tint = PharmaGreen, modifier = Modifier.size(30.dp))
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        pharmacy.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    if (pharmacy.isOpen) {
                        Surface(color = StatusDelivered.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                            Text(
                                "Open",
                                style = MaterialTheme.typography.labelSmall,
                                color = StatusDelivered,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else {
                        Surface(color = StatusCancelled.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                            Text(
                                "Closed",
                                style = MaterialTheme.typography.labelSmall,
                                color = StatusCancelled,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    pharmacy.address.street,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                        Text(" ${pharmacy.rating}", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = TextHint, modifier = Modifier.size(14.dp))
                        Text(" ${pharmacy.estimatedDeliveryTime}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    }
                }
            }

            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TextHint)
        }
    }
}

// ===== SHOP DETAIL =====

@Composable
fun ShopDetailScreen(
    shopId: String,
    onBack: () -> Unit,
    onCartClick: () -> Unit
) {
    val pharmacy = MockData.pharmacies.find { it.id == shopId } ?: MockData.pharmacies.first()
    val medicines = MockData.medicines.filter { it.pharmacyId == shopId || shopId == "pharmacy_001" }
    var cartCount by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredMedicines = if (selectedCategory == "All") medicines
    else medicines.filter { it.category == selectedCategory }

    Scaffold(
        topBar = {
            GradientTopBar(
                title = pharmacy.name,
                subtitle = "${pharmacy.estimatedDeliveryTime}  •  ★ ${pharmacy.rating}",
                accentColor = CustomerAccent,
                onBackClick = onBack,
                actions = {
                    if (cartCount > 0) {
                        BadgedBox(badge = { Badge { Text(cartCount.toString()) } }) {
                            IconButton(onClick = onCartClick) {
                                Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart", tint = Color.White)
                            }
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            // Category filter
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == "All",
                        onClick = { selectedCategory = "All" },
                        label = { Text("All") }
                    )
                }
                items(MockData.categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) }
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredMedicines) { medicine ->
                    MedicineCard(
                        name = medicine.name,
                        genericName = medicine.genericName,
                        price = medicine.price,
                        mrp = medicine.mrp,
                        requiresPrescription = medicine.requiresPrescription,
                        stockStatus = medicine.stockStatus,
                        onAddToCart = { cartCount++ }
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

// ===== SEARCH =====

@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onAddToCart: (Medicine) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val results = remember(query) {
        if (query.isBlank()) MockData.medicines
        else MockData.medicines.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.genericName.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Surface(shadowElevation = 4.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search medicines...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(20.dp)) },
                    trailingIcon = if (query.isNotBlank()) {
                        { IconButton(onClick = { query = "" }) { Icon(Icons.Filled.Clear, contentDescription = "Clear") } }
                    } else null,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                Spacer(Modifier.width(8.dp))
            }
        }

        if (query.isBlank()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Popular Categories", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.height(280.dp)
                ) {
                    items(MockData.categories) { cat ->
                        Surface(
                            onClick = { query = cat },
                            shape = RoundedCornerShape(12.dp),
                            color = PharmaGreen.copy(alpha = 0.08f),
                            modifier = Modifier.aspectRatio(1.2f)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Icon(Icons.Filled.MedicalServices, contentDescription = null, tint = PharmaGreen, modifier = Modifier.size(22.dp))
                                Spacer(Modifier.height(4.dp))
                                Text(cat, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, maxLines = 2)
                            }
                        }
                    }
                }
            }
        } else {
            if (results.isEmpty()) {
                EmptyState(
                    icon = Icons.Filled.SearchOff,
                    title = "No Results Found",
                    subtitle = "Try searching with a different term"
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            "${results.size} result(s) for \"$query\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    items(results) { medicine ->
                        MedicineCard(
                            name = medicine.name,
                            genericName = medicine.genericName,
                            price = medicine.price,
                            mrp = medicine.mrp,
                            requiresPrescription = medicine.requiresPrescription,
                            stockStatus = medicine.stockStatus,
                            onAddToCart = { onAddToCart(medicine) }
                        )
                    }
                }
            }
        }
    }
}

// ===== UPLOAD PRESCRIPTION =====

@Composable
fun UploadPrescriptionScreen(
    onBack: () -> Unit,
    onUploaded: () -> Unit
) {
    var hasImage by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            GradientTopBar(
                title = "Upload Prescription",
                accentColor = CustomerAccent,
                onBackClick = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // Upload zone
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { hasImage = true },
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    2.dp,
                    if (hasImage) PharmaGreen else DividerColor.copy(alpha = 0.8f)
                ),
                color = if (hasImage) PharmaGreen.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (hasImage) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = PharmaGreen, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(8.dp))
                        Text("Prescription Selected", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = PharmaGreen)
                        Text("prescription_image.jpg", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    } else {
                        Icon(Icons.Filled.CloudUpload, contentDescription = null, tint = TextHint, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("Tap to upload prescription", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text("JPG, PNG or PDF up to 5MB", style = MaterialTheme.typography.bodySmall, color = TextHint)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { hasImage = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Camera")
                }
                OutlinedButton(
                    onClick = { hasImage = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Gallery")
                }
            }

            Spacer(Modifier.height(24.dp))

            // Tips card
            Surface(
                color = StatusPending.copy(alpha = 0.08f),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Lightbulb, contentDescription = null, tint = StatusPending, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Tips for a clear prescription", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = StatusPending)
                    }
                    Spacer(Modifier.height(8.dp))
                    listOf(
                        "Ensure the doctor's name and date are visible",
                        "Make sure all medicine names are legible",
                        "Photograph in good lighting",
                        "Prescription should not be older than 6 months"
                    ).forEachIndexed { i, tip ->
                        Text("${i + 1}. $tip", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            PharmaButton(
                text = "Upload & Continue",
                loading = isUploading,
                enabled = hasImage,
                onClick = {
                    scope.launch {
                        isUploading = true
                        delay(1500)
                        isUploading = false
                        onUploaded()
                    }
                }
            )
        }
    }
}

// ===== BOTTOM BAR =====

@Composable
fun CustomerBottomBar(
    selectedTab: Int,
    onHome: () -> Unit,
    onOrders: () -> Unit,
    onSearch: () -> Unit,
    onProfile: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = onHome,
            icon = {
                Icon(
                    if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = onSearch,
            icon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            label = { Text("Search") }
        )
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = onOrders,
            icon = {
                Icon(
                    if (selectedTab == 2) Icons.Filled.Receipt else Icons.Outlined.Receipt,
                    contentDescription = "Orders"
                )
            },
            label = { Text("Orders") }
        )
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = onProfile,
            icon = {
                Icon(
                    if (selectedTab == 3) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile") }
        )
    }
}
