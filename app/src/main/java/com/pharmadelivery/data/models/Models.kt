package com.pharmadelivery.data.models

import java.util.UUID

// ----- Enumerations -----

enum class UserRole { CUSTOMER, PHARMACY, RIDER, ADMIN }

enum class OrderStatus {
    PENDING,
    PRESCRIPTION_REQUIRED,
    PRESCRIPTION_VERIFIED,
    CONFIRMED,
    PREPARING,
    READY_FOR_PICKUP,
    RIDER_ASSIGNED,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
    DISPUTED
}

enum class PaymentMethod { CASH_ON_DELIVERY, UPI, CARD, WALLET }

enum class PaymentStatus { PENDING, PAID, FAILED, REFUNDED }

enum class RiderStatus { OFFLINE, ONLINE, ON_DELIVERY }

enum class StockStatus { IN_STOCK, LOW_STOCK, OUT_OF_STOCK }

// ----- Core Domain Models -----

data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val profileImageUrl: String = "",
    val isVerified: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class Address(
    val id: String = UUID.randomUUID().toString(),
    val label: String = "Home",
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val landmark: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isDefault: Boolean = false
)

data class Medicine(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val genericName: String = "",
    val brand: String = "",
    val category: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val mrp: Double = 0.0,
    val imageUrl: String = "",
    val requiresPrescription: Boolean = false,
    val stockStatus: StockStatus = StockStatus.IN_STOCK,
    val stockCount: Int = 0,
    val expiryDate: String = "",
    val pharmacyId: String = ""
)

data class CartItem(
    val medicine: Medicine,
    var quantity: Int = 1
) {
    val totalPrice: Double get() = medicine.price * quantity
}

data class Pharmacy(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val ownerName: String = "",
    val email: String = "",
    val phone: String = "",
    val address: Address = Address(),
    val licenseNumber: String = "",
    val rating: Float = 4.5f,
    val totalOrders: Int = 0,
    val isOpen: Boolean = true,
    val imageUrl: String = "",
    val deliveryRadius: Double = 5.0,
    val estimatedDeliveryTime: String = "30-45 min",
    val categories: List<String> = emptyList()
)

data class Rider(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val vehicleType: String = "Bike",
    val vehicleNumber: String = "",
    val licenseNumber: String = "",
    val status: RiderStatus = RiderStatus.OFFLINE,
    val rating: Float = 4.8f,
    val totalDeliveries: Int = 0,
    val earningsToday: Double = 0.0,
    val earningsTotal: Double = 0.0,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String = "",
    val isVerified: Boolean = false
)

data class Order(
    val id: String = UUID.randomUUID().toString(),
    val customerId: String = "",
    val customerName: String = "",
    val pharmacyId: String = "",
    val pharmacyName: String = "",
    val riderId: String = "",
    val riderName: String = "",
    val items: List<CartItem> = emptyList(),
    val deliveryAddress: Address = Address(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 30.0,
    val discount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val status: OrderStatus = OrderStatus.PENDING,
    val paymentMethod: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val prescriptionUrl: String = "",
    val requiresPrescription: Boolean = false,
    val prescriptionVerified: Boolean = false,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val estimatedDelivery: String = "30-45 min",
    val trackingUpdates: List<TrackingUpdate> = emptyList()
)

data class TrackingUpdate(
    val status: OrderStatus,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class Notification(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val message: String = "",
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val type: String = ""
)

data class Dispute(
    val id: String = UUID.randomUUID().toString(),
    val orderId: String = "",
    val raisedBy: String = "",
    val reason: String = "",
    val description: String = "",
    val status: String = "OPEN",
    val adminNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class Report(
    val totalOrders: Int = 0,
    val totalRevenue: Double = 0.0,
    val totalUsers: Int = 0,
    val totalPharmacies: Int = 0,
    val totalRiders: Int = 0,
    val activeOrders: Int = 0,
    val pendingPrescriptions: Int = 0,
    val openDisputes: Int = 0
)
