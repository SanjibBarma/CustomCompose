package com.example.customcompose.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.customcompose.MyApplication.Companion.appSessionManager
import com.example.customcompose.MyApplication.Companion.loginViewModel
import com.example.customcompose.R
import com.example.customcompose.helper.CommonUtils.getAppVersionCode
import com.example.customcompose.helper.CommonUtils.getDeviceInfo
import com.example.customcompose.helper.UIState
import com.example.customcompose.model.DownloadModel
import com.example.customcompose.navigation.Screen
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    var username by remember { mutableStateOf(appSessionManager.getUsername() ?: "") }
    var password by remember { mutableStateOf(appSessionManager.getPassword() ?: "") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(appSessionManager.isRemembered()) }
    val gson = Gson()

    val loginState = loginViewModel.loginData.observeAsState(initial = UIState.Loading)
    val userInfoDataState = loginViewModel.userData.observeAsState(initial = UIState.Loading)
    val campaignListDataState = loginViewModel.campaignListData.observeAsState(initial = UIState.Loading)
    val surveyDataState = loginViewModel.surveyData.observeAsState(initial = UIState.Loading)

    val time = SimpleDateFormat("yyyy", Locale.ENGLISH)
    val crrYear = time.format(Date())

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Progress tracking states
    var loginStepDone by remember { mutableStateOf(false) }
    var userInfoStepDone by remember { mutableStateOf(false) }
    var campaignListStepDone by remember { mutableStateOf(false) }
    var surveyStepDone by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    val lazyListState = rememberLazyListState()
    val downloadImageList = mutableListOf<DownloadModel>()

    var downloadProgress by remember { mutableStateOf(0f) } // Progress as a fraction (0.0 to 1.0)
    var isDownloading by remember { mutableStateOf(false) }
    var isDownloadComplete by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val signInInfoMap = HashMap<String, Any>().apply {
        put("password", password)
        put("username", username)
        put("platform", 116)
        put("deviceInfo", getDeviceInfo(context))
    }

    Scaffold { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(30.dp),
                    textStyle = TextStyle(fontSize = 16.sp),
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(30.dp),
                    textStyle = TextStyle(fontSize = 16.sp),
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
                            appSessionManager.setRemembered(it)
                        },
                        modifier = Modifier.scale(.8f)
                    )
                    Text(text = "Remember password", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()

                        loginViewModel.resetAllStates()
                        loginStepDone = false
                        userInfoStepDone = false
                        campaignListStepDone = false
                        surveyStepDone = false
                        progress = 0f
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
                                    appSessionManager.saveLoginData(username, password)
                                } else {
                                    appSessionManager.clearLoginData()
                                }

                                val _signInInfoMap = gson.toJson(signInInfoMap)
                                println("SignInInfoMap: $_signInInfoMap")

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
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .clickable(enabled = false) { }
                        .padding(start = 16.dp),
                ) {
//                    LoadingAnimation()

                    LazyColumn(state = lazyListState) {
                        item{
                            ShowProgress(progress, "Api Info")
                        }

//                        if (progress == 100f){
//                            isDownloadComplete = false
//                            item{
//                                LaunchedEffect(Unit) {
//                                    if (downloadModels.isNotEmpty()) {
//                                        isDownloading = true
//                                        isDownloadComplete = false
//
//                                        scope.launch(Dispatchers.IO) {
//                                            downloadModels.forEachIndexed { index, model ->
//                                                val cacheFile = File(context.cacheDir, model.url.split("/").last())
//
//                                                if (cacheFile.exists()) {
//                                                    withContext(Dispatchers.Main) {
//                                                        downloadProgress = ((index + 1) * 100f) / downloadModels.size
//                                                    }
//                                                } else {
//                                                    val fullUrl = model.url
//                                                    val response = mediaService.downloadFile(fullUrl)
//
//                                                    if (response.isSuccessful) {
//                                                        val inputStream: InputStream = response.body()?.byteStream() ?: return@forEachIndexed
//                                                        val fileOutputStream = FileOutputStream(cacheFile)
//
//                                                        val totalSize = response.body()?.contentLength() ?: 0
//                                                        var downloadedSize = 0L
//
//                                                        val buffer = ByteArray(8192)
//                                                        var bytesRead: Int
//
//                                                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
//                                                            fileOutputStream.write(buffer, 0, bytesRead)
//                                                            downloadedSize += bytesRead
//                                                            withContext(Dispatchers.Main) {
//                                                                downloadProgress = ((index + downloadedSize.toFloat() / totalSize.toFloat()) / downloadModels.size) * 100f
//                                                            }
//                                                        }
//
//                                                        fileOutputStream.flush()
//                                                        fileOutputStream.close()
//                                                    } else {
//                                                        withContext(Dispatchers.Main) {
//                                                            println("Download failed for ${model.url}")
//                                                        }
//                                                    }
//                                                }
//
//                                                withContext(Dispatchers.Main) {
//                                                    downloadProgress = ((index + 1) * 100f) / downloadModels.size
//                                                }
//                                            }
//
//                                            withContext(Dispatchers.Main) {
//                                                Toast.makeText(context, "Download Complete!", Toast.LENGTH_SHORT).show()
//                                                isDownloadComplete = true
//                                            }
//                                            isDownloading = false
//                                        }
//                                    }
//                                }
//                                ShowProgress(downloadProgress, isDownloadComplete)
//                            }
//                        }
                    }
                }
            }

            //login response state
            when (val state = loginState.value) {
                is UIState.Error -> {
                    Toasty.error(context, state.exception.message ?: "Login failed", Toasty.LENGTH_SHORT).show()
                    isLoading = false
                }
                is UIState.Loading -> {
                }
                is UIState.Success -> {
                    if (!loginStepDone) {
                        loginStepDone = true
                        progress += 25
                    }

                    println("userSignInData: ${state.data.data.id}")
                    state.data.data.token?.let {token ->
                        appSessionManager.setSessionToken(token)
                    }
                    appSessionManager.setBrId(state.data.data.id.toString())
                }
            }

            //userinfo response state
            when(val state = userInfoDataState.value){
                is UIState.Error ->{
                    Toasty.error(context, state.exception.message ?: "Failed to get userinfo!", Toasty.LENGTH_SHORT).show()
                    isLoading = false
                }
                is UIState.Loading -> {}
                is UIState.Success -> {
                    if (!userInfoStepDone) {
                        userInfoStepDone = true
                        progress += 25
                    }

                    val time = SimpleDateFormat("yyyy:MM:dd:HH:mm:ss")
                    val crrTime = time.format(Date())

                    state.data.data?.let { userData ->
                        appSessionManager.createMerchantLoginSession(
                            agencyName = userData[0].agency_name,
                            orgName = userData[0].org_name,
                            designation = userData[0].desigantion,
                            Location = userData[0].locations,
                            user_image = userData[0].user_image,
                            crrTime = crrTime
                        )
                    }
                }
            }

            //campaign list response state
            when(val state = campaignListDataState.value){
                is UIState.Error -> {
                    Toasty.error(context, state.exception.message ?: "Failed to get campaign list!", Toasty.LENGTH_SHORT).show()
                    isLoading = false
                }
                is UIState.Loading -> {}
                is UIState.Success -> {
                    if (!campaignListStepDone) {
                        campaignListStepDone = true
                        progress += 25
                    }
                    if (state.data.data.isNullOrEmpty()){
                        isLoading = false
                        Toasty.error(context, "No campaign assign!", Toasty.LENGTH_SHORT).show()
                    }
                }
            }

            //survey data response state
            when(val state = surveyDataState.value){
                is UIState.Error -> {
                    Toasty.error(context, state.exception.message ?: "Failed to get survey data!", Toasty.LENGTH_SHORT).show()
                    isLoading = false
                }
                is UIState.Loading -> {}
                is UIState.Success -> {

                    if (!surveyStepDone) {
                        surveyStepDone = true
                        progress += 25
                    }

                    LaunchedEffect (Unit){
                        delay(500)
                        navController.navigate(Screen.DashboardScreen.route)
                        isLoading = false
                    }

                    if (state.data.data[0].image != null && state.data.data[0].image?.size!! > 0) {
                        // Iterate over images and add them to the imageList
                        for (images in state.data.data[0].image!!) {
                            val imageType = DownloadModel(
                                type = "Image",
                                url = images
                            )
                            downloadImageList.add(imageType)
                        }
                    }

                    println("Image_List: ${gson.toJson(downloadImageList)}")
                }
            }
        }
    }
}

@Composable
fun ShowProgress(progress: Float, title: String) {
    Spacer(modifier = Modifier.height(32.dp))
    Row (
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(60.dp)
        ) {
            CircularProgressIndicator(
                progress = 1f,
                color = Color.LightGray,
                strokeWidth = 8.dp,
                modifier = Modifier.fillMaxSize()
            )
            CircularProgressIndicator(
                progress = progress / 100f,
                color = Color.Blue,
                strokeWidth = 8.dp,
                modifier = Modifier.fillMaxSize()
            )
            if (progress == 100f) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Download Complete",
                    tint = Color.Blue,
                    modifier = Modifier.size(40.dp)
                )
            } else {
                Text(
                    text = "${progress.toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(title)
    }
}

