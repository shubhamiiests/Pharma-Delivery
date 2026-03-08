package com.pharmadelivery.ui.auth.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.pharmadelivery.data.models.UserRole
import com.pharmadelivery.ui.common.*
import com.pharmadelivery.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ===== ONBOARDING =====

private data class OnboardPage(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val color: Color
)

@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    val pages = listOf(
        OnboardPage(
            Icons.Filled.LocalPharmacy,
            "Order Medicines Online",
            "Browse thousands of medicines from trusted pharmacies near you and get them delivered at your door.",
            PharmaGreen
        ),
        OnboardPage(
            Icons.Filled.CameraAlt,
            "Upload Prescription",
            "Simply photograph your doctor's prescription and we'll take care of finding the right medicines for you.",
            PharmaTeal
        ),
        OnboardPage(
            Icons.Filled.DeliveryDining,
            "Fast Doorstep Delivery",
            "Our verified delivery partners ensure your medicines reach you quickly, safely, and reliably.",
            CustomerAccent
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                OnboardingPage(page = pages[page])
            }

            // Dots + navigation
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(pages.size) { index ->
                        val width by animateDpAsState(
                            targetValue = if (pagerState.currentPage == index) 28.dp else 8.dp,
                            label = "dot_width"
                        )
                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(
                                    if (pagerState.currentPage == index)
                                        pages[pagerState.currentPage].color
                                    else
                                        pages[pagerState.currentPage].color.copy(alpha = 0.3f)
                                )
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                if (pagerState.currentPage == pages.lastIndex) {
                    PharmaButton(
                        text = "Get Started",
                        onClick = onGetStarted,
                        icon = Icons.Filled.ArrowForward
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onGetStarted) {
                            Text("Skip", color = TextSecondary)
                        }
                        Button(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            shape = CircleShape,
                            modifier = Modifier.size(56.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Filled.ArrowForward, contentDescription = "Next")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPage(page: OnboardPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .background(page.color.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .background(page.color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = page.color
                )
            }
        }

        Spacer(Modifier.height(48.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
            lineHeight = 26.sp
        )
    }
}

// ===== ROLE SELECTION =====

private data class RoleCard(
    val role: UserRole,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun RoleSelectionScreen(onRoleSelected: (UserRole) -> Unit) {
    val roles = listOf(
        RoleCard(UserRole.CUSTOMER, "Customer", "Order medicines, upload prescriptions", Icons.Filled.Person, CustomerAccent),
        RoleCard(UserRole.PHARMACY, "Pharmacy", "Manage orders, inventory & prescriptions", Icons.Filled.LocalPharmacy, PharmacyAccent),
        RoleCard(UserRole.RIDER, "Delivery Partner", "Accept and deliver orders to customers", Icons.Filled.DeliveryDining, RiderAccent),
        RoleCard(UserRole.ADMIN, "Admin", "Monitor platform, manage disputes", Icons.Filled.AdminPanelSettings, AdminAccent)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(PharmaGreen, PharmaGreenDark))
                )
                .padding(top = 60.dp, bottom = 40.dp)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Filled.MedicalServices,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(52.dp)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "PharmaDelivery",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Choose your role to continue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Who are you?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(Modifier.height(16.dp))

        roles.forEach { roleCard ->
            RoleCardItem(
                roleCard = roleCard,
                onClick = { onRoleSelected(roleCard.role) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun RoleCardItem(
    roleCard: RoleCard,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(roleCard.color.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = roleCard.icon,
                    contentDescription = null,
                    tint = roleCard.color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = roleCard.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = roleCard.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = roleCard.color
            )
        }
    }
}

// ===== LOGIN =====

@Composable
fun LoginScreen(
    role: UserRole,
    isOffline: Boolean,
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit,
    onForgotPassword: () -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val (accentColor, roleLabel) = when (role) {
        UserRole.CUSTOMER -> Pair(CustomerAccent, "Customer")
        UserRole.PHARMACY -> Pair(PharmacyAccent, "Pharmacy")
        UserRole.RIDER -> Pair(RiderAccent, "Delivery Partner")
        UserRole.ADMIN -> Pair(AdminAccent, "Admin")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(accentColor, accentColor.copy(alpha = 0.85f))))
                .padding(top = 48.dp, bottom = 36.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.Start).padding(start = 8.dp)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "Welcome Back",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Sign in as $roleLabel",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        if (isOffline) {
            NoInternetBanner()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            PharmaTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = ""
                },
                label = "Email Address",
                placeholder = "you@example.com",
                leadingIcon = Icons.Filled.Email,
                isError = emailError.isNotEmpty(),
                errorMessage = emailError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(16.dp))

            PharmaTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Filled.Lock,
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Hide" else "Show"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(Modifier.height(8.dp))

            TextButton(
                onClick = onForgotPassword,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Forgot Password?", color = accentColor)
            }

            Spacer(Modifier.height(24.dp))

            PharmaButton(
                text = "Sign In",
                loading = isLoading,
                enabled = email.isNotBlank() && password.isNotBlank(),
                onClick = {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        emailError = "Please enter a valid email"
                        return@PharmaButton
                    }
                    scope.launch {
                        isLoading = true
                        delay(1200) // simulate network
                        isLoading = false
                        onLoginSuccess()
                    }
                }
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Don't have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                TextButton(onClick = onSignupClick) {
                    Text("Sign Up", color = accentColor, fontWeight = FontWeight.Bold)
                }
            }

            // Demo hint
            Spacer(Modifier.height(16.dp))
            Surface(
                color = accentColor.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Info,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Demo: Enter any email & password to continue",
                        style = MaterialTheme.typography.bodySmall,
                        color = accentColor
                    )
                }
            }
        }
    }
}

// ===== SIGNUP =====

@Composable
fun SignupScreen(
    role: UserRole,
    onSignupSuccess: (String) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val accentColor = when (role) {
        UserRole.CUSTOMER -> CustomerAccent
        UserRole.PHARMACY -> PharmacyAccent
        UserRole.RIDER -> RiderAccent
        UserRole.ADMIN -> AdminAccent
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        GradientTopBar(
            title = "Create Account",
            subtitle = "Join as ${role.name.lowercase().replaceFirstChar { it.uppercase() }}",
            accentColor = accentColor,
            onBackClick = onBack
        )

        Column(modifier = Modifier.padding(24.dp)) {
            Spacer(Modifier.height(8.dp))

            PharmaTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                leadingIcon = Icons.Filled.Person
            )
            Spacer(Modifier.height(14.dp))
            PharmaTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                leadingIcon = Icons.Filled.Email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(Modifier.height(14.dp))
            PharmaTextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone Number",
                leadingIcon = Icons.Filled.Phone,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(Modifier.height(14.dp))
            PharmaTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                leadingIcon = Icons.Filled.Lock,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(Modifier.height(28.dp))

            PharmaButton(
                text = "Create Account",
                loading = isLoading,
                enabled = name.isNotBlank() && email.isNotBlank() && phone.isNotBlank() && password.isNotBlank(),
                onClick = {
                    scope.launch {
                        isLoading = true
                        delay(1000)
                        isLoading = false
                        onSignupSuccess(phone)
                    }
                }
            )
        }
    }
}

// ===== OTP VERIFICATION =====

@Composable
fun OtpScreen(
    phone: String,
    onVerified: () -> Unit,
    onBack: () -> Unit
) {
    var otp by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(30) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .size(88.dp)
                .background(PharmaGreen.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Message,
                contentDescription = null,
                tint = PharmaGreen,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "Verify Your Phone",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            "We've sent an OTP to\n${phone.maskOtp()}",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = TextSecondary
        )

        Spacer(Modifier.height(40.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = { if (it.length <= 6) otp = it },
            label = { Text("Enter 6-digit OTP") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                textAlign = TextAlign.Center,
                letterSpacing = 8.sp
            )
        )

        Spacer(Modifier.height(24.dp))

        if (timeLeft > 0) {
            Text(
                "Resend OTP in ${timeLeft}s",
                style = MaterialTheme.typography.bodySmall,
                color = TextHint
            )
        } else {
            TextButton(onClick = { timeLeft = 30 }) {
                Text("Resend OTP", color = PharmaGreen)
            }
        }

        Spacer(Modifier.height(32.dp))

        PharmaButton(
            text = "Verify & Continue",
            loading = isLoading,
            enabled = otp.length == 6,
            onClick = {
                scope.launch {
                    isLoading = true
                    delay(1000)
                    isLoading = false
                    onVerified()
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onBack) {
            Icon(Icons.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("Go Back")
        }
    }
}

private fun String.maskOtp(): String {
    return if (length >= 10) {
        "+91 ${take(2)}***${takeLast(2)}"
    } else this
}

// ===== FORGOT PASSWORD =====

@Composable
fun ForgotPasswordScreen(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var isSent by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        AnimatedContent(targetState = isSent, label = "forgot_state") { sent ->
            if (sent) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.MarkEmailRead,
                        contentDescription = null,
                        tint = PharmaGreen,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Email Sent!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Check your inbox for the password reset link.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(32.dp))
                    PharmaButton("Back to Login", onClick = onBack)
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.LockReset,
                        contentDescription = null,
                        tint = PharmaGreen,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Forgot Password?",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Enter your email address and we'll send you a reset link.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(32.dp))
                    PharmaTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        leadingIcon = Icons.Filled.Email
                    )
                    Spacer(Modifier.height(24.dp))
                    PharmaButton(
                        text = "Send Reset Link",
                        loading = isLoading,
                        enabled = email.isNotBlank(),
                        onClick = {
                            scope.launch {
                                isLoading = true
                                delay(1200)
                                isLoading = false
                                isSent = true
                            }
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                    TextButton(onClick = onBack) {
                        Text("Back to Login", color = TextSecondary)
                    }
                }
            }
        }
    }
}
