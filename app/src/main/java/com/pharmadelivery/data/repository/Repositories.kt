package com.pharmadelivery.data.repository

import com.pharmadelivery.data.local.dao.OrderDao
import com.pharmadelivery.data.local.dao.SessionDao
import com.pharmadelivery.data.local.entities.OrderEntity
import com.pharmadelivery.data.local.entities.SessionEntity
import com.pharmadelivery.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Handles who's currently logged in. Persists across app restarts (point 7 requirement).
@Singleton
class SessionRepository @Inject constructor(
    private val sessionDao: SessionDao
) {
    suspend fun saveSession(user: User) {
        sessionDao.saveSession(
            SessionEntity(
                userId = user.id,
                role = user.role,
                name = user.name,
                email = user.email,
                phone = user.phone,
                profileImageUrl = user.profileImageUrl
            )
        )
    }

    suspend fun getActiveSession(): User? {
        val entity = sessionDao.getActiveSession() ?: return null
        return User(
            id = entity.userId,
            role = entity.role,
            name = entity.name,
            email = entity.email,
            phone = entity.phone,
            profileImageUrl = entity.profileImageUrl,
            isVerified = true
        )
    }

    suspend fun clearSession() = sessionDao.clearSession()

    suspend fun isLoggedIn(): Boolean = sessionDao.getActiveSession() != null
}

@Singleton
class OrderRepository @Inject constructor(
    private val orderDao: OrderDao
) {
    fun getCustomerOrders(customerId: String): Flow<List<Order>> =
        orderDao.getOrdersByCustomer(customerId).map { list ->
            list.map { it.toDomain() }
        }

    fun getPharmacyOrders(pharmacyId: String): Flow<List<Order>> =
        orderDao.getOrdersByPharmacy(pharmacyId).map { list ->
            list.map { it.toDomain() }
        }

    fun getRiderOrders(riderId: String): Flow<List<Order>> =
        orderDao.getOrdersByRider(riderId).map { list ->
            list.map { it.toDomain() }
        }

    fun getAllOrders(): Flow<List<Order>> =
        orderDao.getAllOrdersFlow().map { list ->
            list.map { it.toDomain() }
        }

    suspend fun placeOrder(order: Order) {
        orderDao.insertOrder(order.toEntity())
    }

    suspend fun updateOrderStatus(orderId: String, status: OrderStatus) {
        orderDao.updateOrderStatus(orderId, status, System.currentTimeMillis())
    }

    suspend fun getOrderById(id: String): Order? =
        orderDao.getOrderById(id)?.toDomain()

    // Seed sample data for demo so the app feels populated
    suspend fun seedSampleOrders() {
        MockData.generateSampleOrders().forEach { order ->
            orderDao.insertOrder(order.toEntity())
        }
    }

    private fun Order.toEntity(): OrderEntity = OrderEntity(
        id = id,
        customerId = customerId,
        customerName = customerName,
        pharmacyId = pharmacyId,
        pharmacyName = pharmacyName,
        riderId = riderId,
        riderName = riderName,
        itemsJson = items.joinToString("|") { "${it.medicine.id}:${it.quantity}" },
        deliveryAddressJson = "${deliveryAddress.street},${deliveryAddress.city}",
        subtotal = subtotal,
        deliveryFee = deliveryFee,
        discount = discount,
        totalAmount = totalAmount,
        status = status,
        paymentMethod = paymentMethod.name,
        paymentStatus = paymentStatus.name,
        prescriptionUrl = prescriptionUrl,
        requiresPrescription = requiresPrescription,
        prescriptionVerified = prescriptionVerified,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
        estimatedDelivery = estimatedDelivery,
        trackingUpdatesJson = trackingUpdates.joinToString("|") { "${it.status.name}:${it.message}:${it.timestamp}" }
    )

    private fun OrderEntity.toDomain(): Order {
        // Reconstruct cart items from stored IDs (simplified for demo)
        val medicineMap = MockData.medicines.associateBy { it.id }
        val parsedItems = itemsJson.split("|").mapNotNull { entry ->
            val parts = entry.split(":")
            if (parts.size >= 2) {
                val medicine = medicineMap[parts[0]]
                val qty = parts[1].toIntOrNull() ?: 1
                medicine?.let { CartItem(it, qty) }
            } else null
        }

        val addrParts = deliveryAddressJson.split(",")
        val parsedAddress = Address(
            street = addrParts.getOrElse(0) { "" },
            city = addrParts.getOrElse(1) { "" }
        )

        val parsedTracking = trackingUpdatesJson.split("|").mapNotNull { entry ->
            val parts = entry.split(":")
            if (parts.size >= 3) {
                try {
                    TrackingUpdate(
                        status = OrderStatus.valueOf(parts[0]),
                        message = parts[1],
                        timestamp = parts[2].toLong()
                    )
                } catch (e: Exception) { null }
            } else null
        }

        return Order(
            id = id,
            customerId = customerId,
            customerName = customerName,
            pharmacyId = pharmacyId,
            pharmacyName = pharmacyName,
            riderId = riderId,
            riderName = riderName,
            items = parsedItems,
            deliveryAddress = parsedAddress,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            discount = discount,
            totalAmount = totalAmount,
            status = status,
            paymentMethod = PaymentMethod.values().firstOrNull { it.name == paymentMethod } ?: PaymentMethod.CASH_ON_DELIVERY,
            paymentStatus = PaymentStatus.values().firstOrNull { it.name == paymentStatus } ?: PaymentStatus.PENDING,
            prescriptionUrl = prescriptionUrl,
            requiresPrescription = requiresPrescription,
            prescriptionVerified = prescriptionVerified,
            notes = notes,
            createdAt = createdAt,
            updatedAt = updatedAt,
            estimatedDelivery = estimatedDelivery,
            trackingUpdates = parsedTracking
        )
    }
}
