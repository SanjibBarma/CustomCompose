package com.example.customcompose.views

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.customcompose.R
import com.example.customcompose.common_utils.CommonUtils.getAppVersionCode
import com.example.customcompose.common_utils.CommonUtils.getDeviceInfo
import com.example.customcompose.helper.SharedPrefHelper
import com.example.customcompose.helper.UIState
import com.example.customcompose.viewmodel.LoginViewModel
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(loginViewModel: LoginViewModel) {

    val context = LocalContext.current
    val sharedPrefHelper = remember { SharedPrefHelper(context) }

    var username by remember { mutableStateOf(sharedPrefHelper.getUsername() ?: "") }
    var password by remember { mutableStateOf(sharedPrefHelper.getPassword() ?: "") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(sharedPrefHelper.isRemembered()) }

    val loginState = loginViewModel.loginData.observeAsState(initial = UIState.Loading)

    val time = SimpleDateFormat("yyyy", Locale.ENGLISH)
    val crrYear = time.format(Date())

    val signInInfoMap = HashMap<String, Any>().apply {
        put("password", password)
        put("username", username)
        put("platform", 116)
        put("deviceInfo", getDeviceInfo(context))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .imePadding(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_ecrm_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .height(150.dp)
                    .width(200.dp)
                    .padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                "Login",
                modifier = Modifier,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("Username", fontSize = 16.sp) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                singleLine = true,
                shape = RoundedCornerShape(30.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Username Icon") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", fontSize = 16.sp) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                singleLine = true,
                shape = RoundedCornerShape(30.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = Color.White,
                    focusedBorderColor = Color.Gray,
                    unfocusedBorderColor = Color.Gray
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Password Icon") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = "Toggle Password Visibility"
                        )
                    }
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = {
                        rememberMe = it
                        sharedPrefHelper.setRemembered(it)
                    },
                    modifier = Modifier.scale(.8f)
                )
                Text(text = "Remember password", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    isLoading = true
                    if (username.isEmpty() || password.isEmpty()) {
                        Toasty.warning(context, "Invalid username or password", Toasty.LENGTH_SHORT).show()
                        isLoading = false
                    } else {
                        if (!username.contains("@ecrm-")) {
                            Toasty.warning(context, "Username must contain '@ecrm-'", Toasty.LENGTH_SHORT).show()
                            isLoading = false
                        } else {
                            if (rememberMe) {
                                sharedPrefHelper.saveLoginData(username, password)
                            } else {
                                sharedPrefHelper.clearLoginData()
                            }

                            //val gson = Gson()
                            //val _signInInfoMap = gson.toJson(signInInfoMap)
                            //println("SignInInfoMap: $_signInInfoMap")

                            loginViewModel.getLoginInfo(signInInfoMap)
                        }
                    }
                }
            ) {
                Text(
                    text = "Log In",
                    modifier = Modifier.padding(start = 32.dp, end = 32.dp),
                    fontSize = 18.sp
                )
            }


            Column(
                modifier = Modifier.padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Prism Ecrm app v.${getAppVersionCode(context)}", fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "© V2 Technologies Ltd-$crrYear", fontSize = 12.sp)
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        when (val state = loginState.value) {
            is UIState.Error -> {
                Toasty.warning(context, state.exception.message ?: "Login failed", Toasty.LENGTH_SHORT).show()
                isLoading = false
            }
            is UIState.Loading -> {
                //
            }
            is UIState.Success -> {
                isLoading = false

            }
        }
    }
}

