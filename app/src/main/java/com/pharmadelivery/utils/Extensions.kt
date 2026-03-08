package com.pharmadelivery.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.pharmadelivery.data.models.OrderStatus

fun Long.toReadableDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Long.toShortDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

fun Double.toRupees(): String = "₹%.2f".format(this)

fun OrderStatus.displayName(): String = when (this) {
    OrderStatus.PENDING -> "Order Placed"
    OrderStatus.PRESCRIPTION_REQUIRED -> "Prescription Required"
    OrderStatus.PRESCRIPTION_VERIFIED -> "Prescription Verified"
    OrderStatus.CONFIRMED -> "Order Confirmed"
    OrderStatus.PREPARING -> "Preparing"
    OrderStatus.READY_FOR_PICKUP -> "Ready for Pickup"
    OrderStatus.RIDER_ASSIGNED -> "Rider Assigned"
    OrderStatus.OUT_FOR_DELIVERY -> "Out for Delivery"
    OrderStatus.DELIVERED -> "Delivered"
    OrderStatus.CANCELLED -> "Cancelled"
    OrderStatus.DISPUTED -> "Under Dispute"
}

// Converts between 0 and 1 representing progress in the delivery flow
fun OrderStatus.toProgress(): Float = when (this) {
    OrderStatus.PENDING -> 0.1f
    OrderStatus.PRESCRIPTION_REQUIRED -> 0.2f
    OrderStatus.PRESCRIPTION_VERIFIED -> 0.3f
    OrderStatus.CONFIRMED -> 0.4f
    OrderStatus.PREPARING -> 0.5f
    OrderStatus.READY_FOR_PICKUP -> 0.6f
    OrderStatus.RIDER_ASSIGNED -> 0.7f
    OrderStatus.OUT_FOR_DELIVERY -> 0.85f
    OrderStatus.DELIVERED -> 1.0f
    else -> 0.1f
}

fun String.maskPhoneNumber(): String {
    return if (length >= 10) {
        replaceRange(length - 7, length - 2, "*****")
    } else this
}
