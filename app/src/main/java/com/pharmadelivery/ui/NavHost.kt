package com.pharmadelivery.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pharmadelivery.data.models.User
import com.pharmadelivery.data.models.UserRole
import com.pharmadelivery.ui.admin.screens.*
import com.pharmadelivery.ui.auth.screens.*
import com.pharmadelivery.ui.customer.screens.*
import com.pharmadelivery.ui.pharmacy.screens.*
import com.pharmadelivery.ui.rider.screens.*

@Composable
fun PharmaNavHost(
    startDestination: String,
    isOffline: Boolean,
    currentUser: User? = null,
    onLogin: (UserRole) -> Unit,
    onLogout: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // ---- Onboarding & Auth ----

        composable(Route.Onboarding.path) {
            OnboardingScreen(
                onGetStarted = { navController.navigate(Route.RoleSelect.path) }
            )
        }

        composable(Route.RoleSelect.path) {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    navController.navigate(Route.Login.of(role.name))
                }
            )
        }

        composable(
            route = Route.Login.path,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStack ->
            val role = UserRole.valueOf(backStack.arguments?.getString("role") ?: "CUSTOMER")
            LoginScreen(
                role = role,
                isOffline = isOffline,
                onLoginSuccess = { onLogin(role) },
                onSignupClick = { navController.navigate(Route.Signup.of(role.name)) },
                onForgotPassword = { navController.navigate(Route.ForgotPassword.path) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Route.Signup.path,
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStack ->
            val role = UserRole.valueOf(backStack.arguments?.getString("role") ?: "CUSTOMER")
            SignupScreen(
                role = role,
                onSignupSuccess = { phone ->
                    navController.navigate(Route.OtpVerify.of(phone))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Route.OtpVerify.path,
            arguments = listOf(navArgument("phone") { type = NavType.StringType })
        ) { backStack ->
            val phone = backStack.arguments?.getString("phone") ?: ""
            OtpScreen(
                phone = phone,
                onVerified = { onLogin(UserRole.CUSTOMER) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.ForgotPassword.path) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }

        // ---- Customer ----

        composable(Route.CustomerHome.path) {
            CustomerHomeScreen(
                isOffline = isOffline,
                onBrowseShops = { navController.navigate(Route.BrowseShops.path) },
                onSearch = { navController.navigate(Route.SearchMedicines.path) },
                onUploadPrescription = { navController.navigate(Route.UploadPrescription.path) },
                onOrderHistory = { navController.navigate(Route.OrderHistory.path) },
                onProfile = { navController.navigate(Route.CustomerProfile.path) },
                onCategoryClick = { navController.navigate(Route.SearchMedicines.path) }
            )
        }

        composable(Route.BrowseShops.path) {
            BrowseShopsScreen(
                onShopClick = { shopId -> navController.navigate(Route.ShopDetail.of(shopId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Route.ShopDetail.path,
            arguments = listOf(navArgument("shopId") { type = NavType.StringType })
        ) { backStack ->
            val shopId = backStack.arguments?.getString("shopId") ?: ""
            ShopDetailScreen(
                shopId = shopId,
                onBack = { navController.popBackStack() },
                onCartClick = { navController.navigate(Route.Cart.path) }
            )
        }

        composable(Route.SearchMedicines.path) {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onAddToCart = {}
            )
        }

        composable(Route.UploadPrescription.path) {
            UploadPrescriptionScreen(
                onBack = { navController.popBackStack() },
                onUploaded = { navController.navigate(Route.Cart.path) }
            )
        }

        composable(Route.Cart.path) {
            CartScreen(
                onBack = { navController.popBackStack() },
                onCheckout = { navController.navigate(Route.AddressSelection.path) }
            )
        }

        composable(Route.AddressSelection.path) {
            AddressScreen(
                onBack = { navController.popBackStack() },
                onAddAddress = { navController.navigate(Route.AddAddress.path) },
                onAddressSelected = { navController.navigate(Route.Checkout.path) }
            )
        }

        composable(Route.AddAddress.path) {
            AddAddressScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.Checkout.path) {
            CheckoutScreen(
                onBack = { navController.popBackStack() },
                onOrderPlaced = { success ->
                    navController.navigate(Route.PaymentResult.of(success)) {
                        popUpTo(Route.CustomerHome.path)
                    }
                }
            )
        }

        composable(
            route = Route.PaymentResult.path,
            arguments = listOf(navArgument("success") { type = NavType.BoolType })
        ) { backStack ->
            val success = backStack.arguments?.getBoolean("success") ?: true
            PaymentResultScreen(
                success = success,
                onViewOrder = { navController.navigate(Route.OrderHistory.path) },
                onBackToHome = {
                    navController.navigate(Route.CustomerHome.path) {
                        popUpTo(Route.CustomerHome.path) { inclusive = true }
                    }
                }
            )
        }

        composable(Route.OrderHistory.path) {
            OrderHistoryScreen(
                customerId = currentUser?.id ?: "",
                onBack = { navController.popBackStack() },
                onOrderClick = { orderId -> navController.navigate(Route.OrderDetail.of(orderId)) }
            )
        }

        composable(
            route = Route.OrderDetail.path,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStack ->
            val orderId = backStack.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(
                orderId = orderId,
                onBack = { navController.popBackStack() },
                onTrackOrder = { navController.navigate(Route.OrderTracking.of(orderId)) }
            )
        }

        composable(
            route = Route.OrderTracking.path,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStack ->
            val orderId = backStack.arguments?.getString("orderId") ?: ""
            OrderTrackingScreen(
                orderId = orderId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.CustomerProfile.path) {
            CustomerProfileScreen(
                user = currentUser,
                onEditProfile = { navController.navigate(Route.EditProfile.path) },
                onOrderHistory = { navController.navigate(Route.OrderHistory.path) },
                onSupport = { navController.navigate(Route.CustomerSupport.path) },
                onSettings = { navController.navigate(Route.CustomerSettings.path) },
                onLogout = onLogout
            )
        }

        composable(Route.EditProfile.path) {
            EditProfileScreen(
                user = currentUser,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.CustomerSupport.path) {
            SupportScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.CustomerSettings.path) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        // ---- Pharmacy ----

        composable(Route.PharmacyDashboard.path) {
            PharmacyDashboardScreen(
                pharmacy = currentUser,
                isOffline = isOffline,
                onIncomingOrders = { navController.navigate(Route.IncomingOrders.path) },
                onMedicines = { navController.navigate(Route.MedicineManagement.path) },
                onProfile = { navController.navigate(Route.PharmacyProfile.path) },
                onLogout = onLogout
            )
        }

        composable(Route.IncomingOrders.path) {
            IncomingOrdersScreen(
                pharmacyId = currentUser?.id ?: "pharmacy_001",
                onBack = { navController.popBackStack() },
                onOrderClick = { orderId -> navController.navigate(Route.OrderProcessing.of(orderId)) },
                onVerifyPrescription = { orderId -> navController.navigate(Route.PrescriptionVerify.of(orderId)) }
            )
        }

        composable(
            route = Route.PrescriptionVerify.path,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStack ->
            val orderId = backStack.arguments?.getString("orderId") ?: ""
            PrescriptionVerifyScreen(
                orderId = orderId,
                onBack = { navController.popBackStack() },
                onVerified = { navController.popBackStack() }
            )
        }

        composable(
            route = Route.OrderProcessing.path,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStack ->
            val orderId = backStack.arguments?.getString("orderId") ?: ""
            OrderProcessingScreen(
                orderId = orderId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.MedicineManagement.path) {
            MedicineManagementScreen(
                pharmacyId = currentUser?.id ?: "pharmacy_001",
                onBack = { navController.popBackStack() },
                onAddMedicine = { navController.navigate(Route.AddEditMedicine.of()) },
                onEditMedicine = { id -> navController.navigate(Route.AddEditMedicine.of(id)) }
            )
        }

        composable(
            route = Route.AddEditMedicine.path,
            arguments = listOf(navArgument("medicineId") { type = NavType.StringType })
        ) { backStack ->
            val medId = backStack.arguments?.getString("medicineId") ?: "new"
            AddEditMedicineScreen(
                medicineId = medId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.PharmacyProfile.path) {
            PharmacyProfileScreen(
                user = currentUser,
                onKYC = { navController.navigate(Route.PharmacyKYC.path) },
                onSupport = { navController.navigate(Route.PharmacySupport.path) },
                onLogout = onLogout,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.PharmacyKYC.path) {
            PharmacyKYCScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.PharmacySupport.path) {
            SupportScreen(onBack = { navController.popBackStack() })
        }

        // ---- Rider ----

        composable(Route.RiderDashboard.path) {
            RiderDashboardScreen(
                rider = currentUser,
                isOffline = isOffline,
                onAvailablePickups = { navController.navigate(Route.AvailablePickups.path) },
                onEarnings = { navController.navigate(Route.RiderEarnings.path) },
                onProfile = { navController.navigate(Route.RiderProfile.path) },
                onLogout = onLogout
            )
        }

        composable(Route.AvailablePickups.path) {
            AvailablePickupsScreen(
                onBack = { navController.popBackStack() },
                onAcceptOrder = { orderId -> navController.navigate(Route.ActiveDelivery.of(orderId)) }
            )
        }

        composable(
            route = Route.ActiveDelivery.path,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStack ->
            val orderId = backStack.arguments?.getString("orderId") ?: ""
            ActiveDeliveryScreen(
                orderId = orderId,
                onNavigate = { navController.navigate(Route.DeliveryNavigation.of(orderId)) },
                onProofOfDelivery = { navController.navigate(Route.ProofOfDelivery.of(orderId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Route.DeliveryNavigation.path,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStack ->
            val orderId = backStack.arguments?.getString("orderId") ?: ""
            DeliveryNavigationScreen(
                orderId = orderId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Route.ProofOfDelivery.path,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStack ->
            val orderId = backStack.arguments?.getString("orderId") ?: ""
            ProofOfDeliveryScreen(
                orderId = orderId,
                onDelivered = {
                    navController.navigate(Route.RiderDashboard.path) {
                        popUpTo(Route.RiderDashboard.path) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.RiderEarnings.path) {
            RiderEarningsScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.RiderProfile.path) {
            RiderProfileScreen(
                user = currentUser,
                onDocuments = { navController.navigate(Route.RiderDocuments.path) },
                onSupport = { navController.navigate(Route.RiderSupport.path) },
                onLogout = onLogout,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.RiderDocuments.path) {
            RiderDocumentsScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.RiderSupport.path) {
            SupportScreen(onBack = { navController.popBackStack() })
        }

        // ---- Admin ----

        composable(Route.AdminDashboard.path) {
            AdminDashboardScreen(
                isOffline = isOffline,
                onUsers = { navController.navigate(Route.UserManagement.path) },
                onShops = { navController.navigate(Route.ShopManagement.path) },
                onRiders = { navController.navigate(Route.RiderManagement.path) },
                onPrescriptions = { navController.navigate(Route.PrescriptionReview.path) },
                onDisputes = { navController.navigate(Route.DisputeHandling.path) },
                onReports = { navController.navigate(Route.Reports.path) },
                onSettings = { navController.navigate(Route.AdminSettings.path) },
                onLogout = onLogout
            )
        }

        composable(Route.UserManagement.path) {
            UserManagementScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.ShopManagement.path) {
            ShopManagementScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.RiderManagement.path) {
            RiderManagementScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.PrescriptionReview.path) {
            AdminPrescriptionReviewScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.DisputeHandling.path) {
            DisputeHandlingScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.Reports.path) {
            ReportsScreen(onBack = { navController.popBackStack() })
        }

        composable(Route.AdminSettings.path) {
            AdminSettingsScreen(
                onRolesPermissions = { navController.navigate(Route.RolesPermissions.path) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Route.RolesPermissions.path) {
            RolesPermissionsScreen(onBack = { navController.popBackStack() })
        }
    }
}
