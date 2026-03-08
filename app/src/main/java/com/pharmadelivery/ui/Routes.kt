package com.pharmadelivery.ui

// Sealed class hierarchy for navigation routes keeps everything in one place.
// Typos in route strings are a common source of crashes, so centralizing this matters.

sealed class Route(val path: String) {

    // Root
    object Splash : Route("splash")
    object Onboarding : Route("onboarding")
    object RoleSelect : Route("role_select")

    // Auth (shared pattern, role-specific params)
    object Login : Route("login/{role}") {
        fun of(role: String) = "login/$role"
    }
    object Signup : Route("signup/{role}") {
        fun of(role: String) = "signup/$role"
    }
    object OtpVerify : Route("otp/{phone}") {
        fun of(phone: String) = "otp/$phone"
    }
    object ForgotPassword : Route("forgot_password")

    // Customer
    object CustomerHome : Route("customer/home")
    object BrowseShops : Route("customer/shops")
    object ShopDetail : Route("customer/shop/{shopId}") {
        fun of(shopId: String) = "customer/shop/$shopId"
    }
    object SearchMedicines : Route("customer/search")
    object UploadPrescription : Route("customer/upload_prescription")
    object Cart : Route("customer/cart")
    object AddressSelection : Route("customer/address")
    object AddAddress : Route("customer/address/add")
    object Checkout : Route("customer/checkout")
    object PaymentResult : Route("customer/payment_result/{success}") {
        fun of(success: Boolean) = "customer/payment_result/$success"
    }
    object OrderHistory : Route("customer/orders")
    object OrderDetail : Route("customer/order/{orderId}") {
        fun of(orderId: String) = "customer/order/$orderId"
    }
    object OrderTracking : Route("customer/track/{orderId}") {
        fun of(orderId: String) = "customer/track/$orderId"
    }
    object CustomerProfile : Route("customer/profile")
    object EditProfile : Route("customer/profile/edit")
    object CustomerSupport : Route("customer/support")
    object CustomerSettings : Route("customer/settings")

    // Pharmacy
    object PharmacyDashboard : Route("pharmacy/dashboard")
    object IncomingOrders : Route("pharmacy/orders/incoming")
    object PrescriptionVerify : Route("pharmacy/prescription/{orderId}") {
        fun of(orderId: String) = "pharmacy/prescription/$orderId"
    }
    object OrderProcessing : Route("pharmacy/order/process/{orderId}") {
        fun of(orderId: String) = "pharmacy/order/process/$orderId"
    }
    object MedicineManagement : Route("pharmacy/medicines")
    object AddEditMedicine : Route("pharmacy/medicines/edit/{medicineId}") {
        fun of(id: String = "new") = "pharmacy/medicines/edit/$id"
    }
    object StockUpdate : Route("pharmacy/stock")
    object PharmacyProfile : Route("pharmacy/profile")
    object PharmacyKYC : Route("pharmacy/kyc")
    object PharmacySupport : Route("pharmacy/support")

    // Rider
    object RiderDashboard : Route("rider/dashboard")
    object AvailablePickups : Route("rider/pickups")
    object ActiveDelivery : Route("rider/delivery/{orderId}") {
        fun of(orderId: String) = "rider/delivery/$orderId"
    }
    object DeliveryNavigation : Route("rider/navigate/{orderId}") {
        fun of(orderId: String) = "rider/navigate/$orderId"
    }
    object ProofOfDelivery : Route("rider/pod/{orderId}") {
        fun of(orderId: String) = "rider/pod/$orderId"
    }
    object RiderProfile : Route("rider/profile")
    object RiderDocuments : Route("rider/documents")
    object RiderSupport : Route("rider/support")
    object RiderEarnings : Route("rider/earnings")

    // Admin
    object AdminDashboard : Route("admin/dashboard")
    object UserManagement : Route("admin/users")
    object ShopManagement : Route("admin/shops")
    object RiderManagement : Route("admin/riders")
    object PrescriptionReview : Route("admin/prescriptions")
    object DisputeHandling : Route("admin/disputes")
    object Reports : Route("admin/reports")
    object AdminSettings : Route("admin/settings")
    object RolesPermissions : Route("admin/roles")
}
