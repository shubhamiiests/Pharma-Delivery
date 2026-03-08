package com.pharmadelivery.data.repository

import com.pharmadelivery.data.models.*

// All the fake-but-believable data that makes the demo feel real.
// In prod this whole file gets deleted and replaced with API calls.
object MockData {

    val pharmacies = listOf(
        Pharmacy(
            id = "pharmacy_001",
            name = "MedPlus Pharmacy",
            ownerName = "Rajesh Kumar",
            email = "rajesh@medplus.com",
            phone = "+91 98765 43210",
            address = Address(
                street = "12, MG Road",
                city = "Bangalore",
                state = "Karnataka",
                pincode = "560001"
            ),
            licenseNumber = "KA-PHARM-2021-01234",
            rating = 4.7f,
            totalOrders = 1240,
            isOpen = true,
            estimatedDeliveryTime = "25-35 min",
            categories = listOf("General", "Cardiac", "Diabetic", "Paediatric", "Vitamins")
        ),
        Pharmacy(
            id = "pharmacy_002",
            name = "Apollo Pharmacy",
            ownerName = "Priya Sharma",
            email = "priya@apollo.com",
            phone = "+91 98765 11111",
            address = Address(
                street = "45, Brigade Road",
                city = "Bangalore",
                state = "Karnataka",
                pincode = "560001"
            ),
            licenseNumber = "KA-PHARM-2020-00567",
            rating = 4.9f,
            totalOrders = 3200,
            isOpen = true,
            estimatedDeliveryTime = "30-45 min",
            categories = listOf("General", "Ayurvedic", "Surgical", "Homeopathy")
        ),
        Pharmacy(
            id = "pharmacy_003",
            name = "HealthFirst Store",
            ownerName = "Venkat Rao",
            email = "venkat@healthfirst.com",
            phone = "+91 97777 55555",
            address = Address(
                street = "78, Koramangala",
                city = "Bangalore",
                state = "Karnataka",
                pincode = "560034"
            ),
            licenseNumber = "KA-PHARM-2022-09876",
            rating = 4.4f,
            totalOrders = 780,
            isOpen = false,
            estimatedDeliveryTime = "40-55 min",
            categories = listOf("General", "Vitamins", "Skincare")
        )
    )

    val medicines = listOf(
        Medicine(
            id = "med_001",
            name = "Crocin Advance 500mg",
            genericName = "Paracetamol",
            brand = "GSK",
            category = "Fever & Pain",
            description = "Effective relief from fever, headache, and mild to moderate pain.",
            price = 38.0,
            mrp = 45.0,
            requiresPrescription = false,
            stockStatus = StockStatus.IN_STOCK,
            stockCount = 150,
            expiryDate = "12/2026",
            pharmacyId = "pharmacy_001"
        ),
        Medicine(
            id = "med_002",
            name = "Azithromycin 500mg",
            genericName = "Azithromycin",
            brand = "Cipla",
            category = "Antibiotics",
            description = "Broad-spectrum antibiotic for bacterial infections.",
            price = 95.0,
            mrp = 110.0,
            requiresPrescription = true,
            stockStatus = StockStatus.IN_STOCK,
            stockCount = 60,
            expiryDate = "06/2026",
            pharmacyId = "pharmacy_001"
        ),
        Medicine(
            id = "med_003",
            name = "Telma 40",
            genericName = "Telmisartan",
            brand = "Glenmark",
            category = "Cardiac",
            description = "Used for treatment of high blood pressure (hypertension).",
            price = 130.0,
            mrp = 155.0,
            requiresPrescription = true,
            stockStatus = StockStatus.IN_STOCK,
            stockCount = 40,
            expiryDate = "09/2025",
            pharmacyId = "pharmacy_001"
        ),
        Medicine(
            id = "med_004",
            name = "Metformin 500mg",
            genericName = "Metformin HCl",
            brand = "Sun Pharma",
            category = "Diabetic",
            description = "Controls blood sugar levels in type 2 diabetes.",
            price = 25.0,
            mrp = 30.0,
            requiresPrescription = true,
            stockStatus = StockStatus.LOW_STOCK,
            stockCount = 12,
            expiryDate = "03/2026",
            pharmacyId = "pharmacy_001"
        ),
        Medicine(
            id = "med_005",
            name = "Vitamin D3 60K",
            genericName = "Cholecalciferol",
            brand = "Mankind",
            category = "Vitamins",
            description = "Weekly supplement for Vitamin D deficiency.",
            price = 55.0,
            mrp = 65.0,
            requiresPrescription = false,
            stockStatus = StockStatus.IN_STOCK,
            stockCount = 200,
            expiryDate = "01/2027",
            pharmacyId = "pharmacy_001"
        ),
        Medicine(
            id = "med_006",
            name = "Cetirizine 10mg",
            genericName = "Cetirizine HCl",
            brand = "Cipla",
            category = "Allergy",
            description = "Antihistamine for allergic rhinitis and urticaria.",
            price = 18.0,
            mrp = 22.0,
            requiresPrescription = false,
            stockStatus = StockStatus.IN_STOCK,
            stockCount = 300,
            expiryDate = "08/2026",
            pharmacyId = "pharmacy_001"
        ),
        Medicine(
            id = "med_007",
            name = "Pantoprazole 40mg",
            genericName = "Pantoprazole",
            brand = "Zydus",
            category = "Gastro",
            description = "Proton pump inhibitor for acid reflux and ulcers.",
            price = 42.0,
            mrp = 50.0,
            requiresPrescription = false,
            stockStatus = StockStatus.OUT_OF_STOCK,
            stockCount = 0,
            expiryDate = "11/2025",
            pharmacyId = "pharmacy_001"
        ),
        Medicine(
            id = "med_008",
            name = "Combiflam",
            genericName = "Ibuprofen + Paracetamol",
            brand = "Sanofi",
            category = "Fever & Pain",
            description = "Combination for pain, inflammation and fever.",
            price = 30.0,
            mrp = 35.0,
            requiresPrescription = false,
            stockStatus = StockStatus.IN_STOCK,
            stockCount = 180,
            expiryDate = "07/2026",
            pharmacyId = "pharmacy_001"
        )
    )

    val currentCustomer = User(
        id = "user_demo_001",
        name = "Arjun Mehta",
        email = "arjun@example.com",
        phone = "+91 99887 76655",
        role = UserRole.CUSTOMER,
        isVerified = true
    )

    val currentPharmacyOwner = User(
        id = "pharmacy_owner_001",
        name = "Rajesh Kumar",
        email = "rajesh@medplus.com",
        phone = "+91 98765 43210",
        role = UserRole.PHARMACY,
        isVerified = true
    )

    val currentRider = User(
        id = "rider_demo_001",
        name = "Suresh Nair",
        email = "suresh@rider.com",
        phone = "+91 91234 56789",
        role = UserRole.RIDER,
        isVerified = true
    )

    val currentAdmin = User(
        id = "admin_001",
        name = "Admin User",
        email = "admin@pharmadelivery.com",
        phone = "+91 80000 00001",
        role = UserRole.ADMIN,
        isVerified = true
    )

    val riderInfo = Rider(
        id = "rider_demo_001",
        name = "Suresh Nair",
        email = "suresh@rider.com",
        phone = "+91 91234 56789",
        vehicleType = "Bike",
        vehicleNumber = "KA 05 HB 9988",
        licenseNumber = "KA-DL-2019-00123456",
        status = RiderStatus.ONLINE,
        rating = 4.8f,
        totalDeliveries = 347,
        earningsToday = 480.0,
        earningsTotal = 84200.0,
        isVerified = true
    )

    val sampleAddresses = listOf(
        Address(
            id = "addr_001",
            label = "Home",
            street = "Flat 3B, Sunshine Apartments, 14th Cross",
            city = "Bangalore",
            state = "Karnataka",
            pincode = "560038",
            landmark = "Near Forum Mall",
            isDefault = true
        ),
        Address(
            id = "addr_002",
            label = "Office",
            street = "WeWork, 3rd Floor, Embassy Golf Links",
            city = "Bangalore",
            state = "Karnataka",
            pincode = "560071",
            landmark = "Opposite HSBC"
        )
    )

    fun generateSampleOrders(): List<Order> {
        val addr = sampleAddresses.first()
        return listOf(
            Order(
                id = "ORD-2024-0001",
                customerId = "user_demo_001",
                customerName = "Arjun Mehta",
                pharmacyId = "pharmacy_001",
                pharmacyName = "MedPlus Pharmacy",
                riderId = "rider_demo_001",
                riderName = "Suresh Nair",
                items = listOf(
                    CartItem(medicines[0], 2),
                    CartItem(medicines[5], 1)
                ),
                deliveryAddress = addr,
                subtotal = 94.0,
                deliveryFee = 30.0,
                totalAmount = 124.0,
                status = OrderStatus.DELIVERED,
                paymentMethod = PaymentMethod.UPI,
                paymentStatus = PaymentStatus.PAID,
                createdAt = System.currentTimeMillis() - 86400000L,
                trackingUpdates = listOf(
                    TrackingUpdate(OrderStatus.PENDING, "Order placed"),
                    TrackingUpdate(OrderStatus.CONFIRMED, "Order confirmed by pharmacy"),
                    TrackingUpdate(OrderStatus.PREPARING, "Medicines being packed"),
                    TrackingUpdate(OrderStatus.RIDER_ASSIGNED, "Rider assigned"),
                    TrackingUpdate(OrderStatus.OUT_FOR_DELIVERY, "Out for delivery"),
                    TrackingUpdate(OrderStatus.DELIVERED, "Delivered successfully")
                )
            ),
            Order(
                id = "ORD-2024-0002",
                customerId = "user_demo_001",
                customerName = "Arjun Mehta",
                pharmacyId = "pharmacy_001",
                pharmacyName = "MedPlus Pharmacy",
                items = listOf(CartItem(medicines[1], 1)),
                deliveryAddress = addr,
                subtotal = 95.0,
                deliveryFee = 30.0,
                totalAmount = 125.0,
                status = OrderStatus.PRESCRIPTION_REQUIRED,
                requiresPrescription = true,
                prescriptionVerified = false,
                paymentMethod = PaymentMethod.CASH_ON_DELIVERY,
                paymentStatus = PaymentStatus.PENDING,
                createdAt = System.currentTimeMillis() - 3600000L
            ),
            Order(
                id = "ORD-2024-0003",
                customerId = "user_demo_002",
                customerName = "Preethi Iyer",
                pharmacyId = "pharmacy_001",
                pharmacyName = "MedPlus Pharmacy",
                riderId = "rider_demo_001",
                riderName = "Suresh Nair",
                items = listOf(
                    CartItem(medicines[2], 1),
                    CartItem(medicines[3], 2)
                ),
                deliveryAddress = Address(
                    street = "22, Indiranagar 100ft Road",
                    city = "Bangalore",
                    state = "Karnataka",
                    pincode = "560038"
                ),
                subtotal = 180.0,
                deliveryFee = 30.0,
                totalAmount = 210.0,
                status = OrderStatus.OUT_FOR_DELIVERY,
                requiresPrescription = true,
                prescriptionVerified = true,
                paymentMethod = PaymentMethod.UPI,
                paymentStatus = PaymentStatus.PAID,
                createdAt = System.currentTimeMillis() - 7200000L
            )
        )
    }

    val categories = listOf(
        "Fever & Pain",
        "Antibiotics",
        "Cardiac",
        "Diabetic",
        "Vitamins",
        "Allergy",
        "Gastro",
        "Skincare",
        "Surgical",
        "Ayurvedic",
        "Paediatric",
        "Eye Care"
    )

    val adminReport = Report(
        totalOrders = 4820,
        totalRevenue = 687500.0,
        totalUsers = 12450,
        totalPharmacies = 38,
        totalRiders = 65,
        activeOrders = 34,
        pendingPrescriptions = 12,
        openDisputes = 3
    )
}
