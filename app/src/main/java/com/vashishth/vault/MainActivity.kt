package com.vashishth.vault

import android.Manifest
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Storage
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vashishth.vault.Crypto.Crypto
import com.vashishth.vault.db.password
import com.vashishth.vault.screens.MainViewModel
import com.vashishth.vault.ui.theme.VAULTTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VAULTTheme {
                MainContent(mainActivity = this@MainActivity)
            }
        }
    }
}



@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainContent (viewModel: MainViewModel = hiltViewModel(),mainActivity: FragmentActivity) {
    val context = LocalContext.current
    var pointer = remember {
        mutableStateOf(false)
    }
    var db = FirebaseFirestore.getInstance()

    Biometric.authenticate(
        mainActivity,
        title = "Biometric Authentication",
        subtitle = "Authenticate to proceed",
        description = "Authentication is must",
        negativeText = "Cancel",
        onSuccess = {
                Toast.makeText(
                    context,
                    "Authenticated successfully",
                    Toast.LENGTH_SHORT
                )
                    .show()
            pointer.value = true

        },
        onError = {errorCode,errorString->
                Toast.makeText(
                    context,
                    "Authentication error: $errorCode, $errorString",
                    Toast.LENGTH_SHORT
                )
                    .show()

        },
        onFailed = {
                Toast.makeText(
                    context,
                    "Authentication failed",
                    Toast.LENGTH_SHORT
                )
                    .show()

        }
    )
    if (pointer.value) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column {
                Column(Modifier.fillMaxHeight(0.7f)) {
                    allPass(viewModel)
                }
                Spacer(modifier = Modifier.border(4.dp, Color.LightGray))
                addPassword(viewModel)
            }
        }
    }
    else{
        Surface(modifier = Modifier
            .fillMaxSize()
            .background(Color.White)) {
        }
    }
}


@SuppressLint("CoroutineCreationDuringComposition", "SuspiciousIndentation")
@Composable
fun allPass(viewModel:MainViewModel){
    val db = Firebase.firestore

    val passwordList = viewModel.passList.collectAsState().value
    val context = LocalContext.current
    val passwordList1 = remember{
        mutableStateListOf<password?>()
    }

    val coroutineScope = rememberCoroutineScope()

    coroutineScope.launch {
        Essentials.passCollection.get()
            .addOnSuccessListener { allPassword ->
            allPassword.forEach {
                val data = it.data
                val appName = data["appName"]
                val password = data["password"]

                val response = password(appName = appName.toString(), password = password.toString())

                    if(!passwordList1.contains(response)){
                        passwordList1.add(response)
                    }else{

                    }

                }
            }
            .addOnFailureListener {
                Toast.makeText(context,"Failed To Load Data",Toast.LENGTH_SHORT).show()
            }
    }
    Surface(
        Modifier
            .fillMaxHeight()
    ) {
        if(passwordList1.isEmpty()){
                Text(modifier = Modifier.fillMaxWidth(1f).padding(30.dp), textAlign = TextAlign.Center,text = "You haven't saved any password")

        }else{
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(passwordList1){
                    if (it != null) {
                        passView(password = it)
                    }
                }
            }
        }

    }
}


@Composable
fun passView(password: password){
    Surface(
        Modifier
            .padding(horizontal = 10.dp, vertical = 5.dp),
        elevation = 2.dp,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(0.1.dp, Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier
                        .background(Color.LightGray, RoundedCornerShape(10.dp))
                        .padding(2.dp)
                        .size(35.dp),
                    text = password.appName.first().uppercase(),
                    textAlign = TextAlign.Center,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    password.appName.uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 23.sp,
                    color = Color(0xFF021a44)
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(modifier = Modifier
                    .size(23.dp)
                    .padding(end = 5.dp),imageVector = Icons.Filled.Password, contentDescription = "call Icon", tint = Color(0xFF053588)
                )
//                Crypto().decrypt(password.password)
                Text(text = Crypto().decrypt(password.password), fontSize = 13.sp, color = Color(0xFF053588))
            }
        }

    }
}


@Composable
fun addPassword(viewModel: MainViewModel){
    var appName =  remember{
        mutableStateOf("")
    }
    var password =  remember{
        mutableStateOf("")
    }
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(corner = CornerSize(10.dp)),
            value = appName.value,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = "Name Icon"
                )
            },
            //trailingIcon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
            onValueChange = {
                appName.value = it
            },
            label = { Text(text = "Site Name") },
            placeholder = { Text(text = "Enter Site Name") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            shape = RoundedCornerShape(corner = CornerSize(10.dp)),
            value = password.value,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Password,
                    contentDescription = "Name Icon"
                )
            },
            //trailingIcon = { Icon(imageVector = Icons.Default.Add, contentDescription = null) },
            onValueChange = {
                password.value = it
            },
            label = { Text(text = "Password") },
            placeholder = { Text(text = "Enter Password") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
        AddBtn(shape = RoundedCornerShape(10.dp) ,
            onClick = {
            if(!appName.value.isNullOrBlank() && !password.value.isNullOrBlank()) {
                viewModel.insertLogo(
                    password = password(
                        appName = appName.value,
                        password = password.value
                    )
                )
                addDataToFirebase(appName.value,Crypto().encrypt(password.value),context)
            }else{
                Toast.makeText(context,"Please enter the values",Toast.LENGTH_SHORT).show()
            }
        }, title = "Save")
    }
}

@Composable
fun AddBtn(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    title:String,
    backGroundColor: Color = Color(0xFFBBE2FA),
    elevation: Dp = 0.dp,
    fontSize: TextUnit = 20.sp,
    borderStroke: BorderStroke = BorderStroke(1.dp, Color(0xFF0a5eef)),
    shape: Shape = RoundedCornerShape(corner = CornerSize(35.dp))
){
    Surface(modifier = modifier
        .padding(all = 16.dp)//15.dp
        .height(60.dp)
        .width(160.dp)
        .clickable { onClick.invoke() },
        shape = shape,
        border = borderStroke,
        color = backGroundColor) {
        Row(Modifier.padding(5.dp),verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Text(text = title, modifier = modifier.padding(0.dp), fontSize = fontSize, textAlign = TextAlign.Center, color = Color(0xFF47107e))
        }
    }
}

fun addDataToFirebase(
    websiteName: String, password: String, context: Context
) {
    // on below line creating an instance of firebase firestore.
    val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // creating a collection reference for our Firebase Firestore database.
    val dbPass: CollectionReference = db.collection("password")

    // adding our data to our courses object class.
    val passwords = password(websiteName, password)

    // below method is use to add data to Firebase Firestore
    // after the data addition is successful
    dbPass.add(passwords).addOnSuccessListener {
        // we are displaying a success toast message.
        Toast.makeText(
            context, "Your Password has been added", Toast.LENGTH_SHORT
        ).show()

    }.addOnFailureListener { e ->
        // this method is called when the data addition process is failed.
        // displaying a toast message when data addition is failed.
        Toast.makeText(context, "Fail to add password \n$e", Toast.LENGTH_SHORT).show()
    }

}

//
//private val authenticationCallback: BiometricPrompt.AuthenticationCallback =
//    @RequiresApi(Build.VERSION_CODES.P)
//    object : BiometricPrompt.AuthenticationCallback() {
//        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
//            super.onAuthenticationSucceeded(result)
//            Toast.makeText(this@MainActivity, "Authentication Succeeded", Toast.LENGTH_SHORT).show()
//        }
//
//        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
//            super.onAuthenticationError(errorCode, errString)
//            Toast.makeText(this@MainActivity, "Authentication Error code: $errorCode", Toast.LENGTH_SHORT).show()
//        }
//
//        override fun onAuthenticationFailed() {
//            super.onAuthenticationFailed()
//        }
//
//        override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
//            super.onAuthenticationHelp(helpCode, helpString)
//        }
//    }
//
//@RequiresApi(Build.VERSION_CODES.M)
//private fun checkBiometricSupport(): Boolean {
//    val keyGuardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
//
//    if (!keyGuardManager.isDeviceSecure) {
//        return true
//    }
//    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
//        return false
//    }
//
//    return packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
//}
//
//@RequiresApi(Build.VERSION_CODES.Q)
//private fun launchBiometric() {
//    if (checkBiometricSupport()) {
//        val biometricPrompt = BiometricPrompt.Builder(this)
//            .apply {
//                setTitle("VAULT")
//                setSubtitle("Please Login to get access")
//                setDescription("A secured password manager for you")
//                setConfirmationRequired(false)
//                setNegativeButton("Use app password", mainExecutor, { _, _, ->
//                    Toast.makeText(this@MainActivity, "Authentication Cancelled", Toast.LENGTH_SHORT).show()
//                })
//            }.build()
//
//        biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)
//    }
//}
//
//private fun getCancellationSignal(): CancellationSignal {
//    cancellationSignal = CancellationSignal()
//    cancellationSignal?.setOnCancelListener {
//        Toast.makeText(this, "Authentication Cancelled Signal", Toast.LENGTH_SHORT).show()
//    }
//
//    return cancellationSignal as CancellationSignal
//}
//
//@Composable
//fun BioMetricScreen(
//    onClick: () -> Unit
//) {
//    val context = LocalContext.current
//    val emailVal = remember { mutableStateOf("") }
//    val passwordVal = remember { mutableStateOf("") }
//    val passwordVisibility = remember { mutableStateOf(false) }
//    val checked = remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(50.dp)
//                .background(Color(0xFFF5FFF8)),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = "BioMetric FingerPrint",
//                color = Color.White,
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold
//            )
//        }
//
//        Column(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Image(
//                painter = painterResource(id = R.drawable.login_img),
//                contentDescription = "Login image",
//                modifier = Modifier
//                    .width(250.dp)
//                    .height(250.dp),
//                contentScale = ContentScale.Fit
//            )
//
//            Spacer(modifier = Modifier.padding(10.dp))
//
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 15.dp, end = 15.dp)
//                    .clip(RoundedCornerShape(20.dp))
//                    .background(Color.LightGray),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                Text(
//                    text = "Sign In",
//                    fontSize = 30.sp,
//                    fontWeight = FontWeight.Bold
//                )
//
//                Spacer(modifier = Modifier.padding(15.dp))
//
//                OutlinedTextField(
//                    value = emailVal.value,
//                    onValueChange = { emailVal.value = it },
//                    label = { Text(text = "Email Address") },
//                    placeholder = { Text(text = "Email Address") },
//                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = "Email") },
//                    singleLine = true,
//                    modifier = Modifier.fillMaxWidth(0.8f)
//                )
//
//                OutlinedTextField(value = passwordVal.value,
//                    onValueChange = {passwordVal.value = it},
//                    trailingIcon = {
//                        IconButton(onClick = { passwordVisibility.value = !passwordVisibility.value }) {
//                            Icon(
//                                painter = painterResource(id = R.drawable.password_eye),
//                                contentDescription = "password eye",
//                                tint = if (passwordVisibility.value) Color(0xFFF5FFF8) else Color.Gray
//                            )
//                        }
//                    },
//                    label = {Text(text = "Password")},
//                    placeholder = { Text(text = "Password") },
//                    leadingIcon = {  Icon(Icons.Filled.Lock, contentDescription = "Password") },
//                    singleLine = true,
//                    visualTransformation = if (passwordVisibility.value)
//                        VisualTransformation.None else PasswordVisualTransformation(),
//                    modifier = Modifier.fillMaxWidth(0.8f)
//
//                )
//
//                Spacer(modifier = Modifier.padding(10.dp))
//
//                Button(
//                    onClick = {
//                        if (emailVal.value.isEmpty()) {
//                            Toast.makeText(context, "Please enter email address!", Toast.LENGTH_SHORT).show()
//                        } else if (passwordVal.value.isEmpty()) {
//                            Toast.makeText(context, "Please enter password!", Toast.LENGTH_SHORT).show()
//                        } else {
//                            Toast.makeText(context, "Logged Successfully!", Toast.LENGTH_SHORT).show()
//                        }
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth(0.8f)
//                        .height(50.dp)
//                ) {
//                    Text(text = "Sign In", fontSize = 20.sp)
//                }
//
//                Spacer(modifier = Modifier.padding(20.dp))
//
//                Row(
//                    horizontalArrangement = Arrangement.spacedBy(20.dp)
//                ) {
//                    Text(text = "Enable Biometric Prompt", fontSize = 20.sp)
//
//                    Switch(
//                        checked = checked.value,
//                        onCheckedChange = {
//                            checked.value = it
//                            if (checked.value) {
//                                onClick()
//                            }
//                        }
//                    )
//                }
//
//                Spacer(modifier = Modifier.padding(10.dp))
//            }
//        }
//    }
//}

