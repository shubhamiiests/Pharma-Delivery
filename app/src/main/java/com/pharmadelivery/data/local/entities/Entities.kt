package com.pharmadelivery.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pharmadelivery.data.models.OrderStatus
import com.pharmadelivery.data.models.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val profileImageUrl: String,
    val isVerified: Boolean,
    val createdAt: Long
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val customerId: String,
    val customerName: String,
    val pharmacyId: String,
    val pharmacyName: String,
    val riderId: String,
    val riderName: String,
    // We store the full order JSON because cart items are complex;
    // in a real app we'd normalize this but for demo purposes this is fine
    val itemsJson: String,
    val deliveryAddressJson: String,
    val subtotal: Double,
    val deliveryFee: Double,
    val discount: Double,
    val totalAmount: Double,
    val status: OrderStatus,
    val paymentMethod: String,
    val paymentStatus: String,
    val prescriptionUrl: String,
    val requiresPrescription: Boolean,
    val prescriptionVerified: Boolean,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long,
    val estimatedDelivery: String,
    val trackingUpdatesJson: String
)

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey val id: String,
    val name: String,
    val genericName: String,
    val brand: String,
    val category: String,
    val description: String,
    val price: Double,
    val mrp: Double,
    val imageUrl: String,
    val requiresPrescription: Boolean,
    val stockStatus: String,
    val stockCount: Int,
    val expiryDate: String,
    val pharmacyId: String
)

@Entity(tableName = "session")
data class SessionEntity(
    @PrimaryKey val key: String = "active_session",
    val userId: String,
    val role: UserRole,
    val name: String,
    val email: String,
    val phone: String,
    val profileImageUrl: String,
    val loginTimestamp: Long = System.currentTimeMillis()
)
