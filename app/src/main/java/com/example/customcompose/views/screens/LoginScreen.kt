package com.example.customcompose.views.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.customcompose.MyApplication.Companion.mediaService
import com.example.customcompose.R
import com.example.customcompose.helper.CommonUtils.calculateMD5
import com.example.customcompose.helper.CommonUtils.getAppVersionCode
import com.example.customcompose.helper.CommonUtils.getDeviceInfo
import com.example.customcompose.helper.UIState
import com.example.customcompose.model.DownloadModel
import com.example.customcompose.navigation.Screen
import com.example.customcompose.views.compose.helper_compose.KeepScreenOnEffect
import com.example.customcompose.views.compose.helper_compose.ShowProgress
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    KeepScreenOnEffect()
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
    var apiCount by remember { mutableIntStateOf(0) }
    var downloadImagesCount by remember { mutableIntStateOf(0) }
    var downloadVideoCount by remember { mutableIntStateOf(0) }
    val lazyListState = rememberLazyListState()

    val downloadImageList = mutableListOf<DownloadModel>()
    val downloadVideoList = mutableListOf<DownloadModel>()
    var imageDownloadProgress by remember { mutableStateOf(0f) }
    var videoDownloadProgress by remember { mutableStateOf(0f) }
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
                                imageVector = if (!passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
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
                        downloadImageList.clear()
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
                            ShowProgress(progress, "Api Info", apiCount, 4)
                        }

                        if (progress == 100f){
                            if (downloadImageList.size > 0){
                                item{
                                    LaunchedEffect(downloadImageList.size) {
                                        if (downloadImageList.isNotEmpty()) {
                                            withContext(Dispatchers.IO) {
                                                downloadImageList.forEachIndexed { index, imageUrl ->
//                                                    println("Downloading_image: ${imageUrl.url.substringAfterLast("/")}")
                                                    val cacheFile = File(context.cacheDir, imageUrl.url.substringAfterLast("/"))
                                                    if (downloadImagesCount >= downloadImageList.size){
                                                        downloadImagesCount = downloadImageList.size
                                                    }else{
                                                        downloadImagesCount ++
                                                    }
                                                    if (cacheFile.exists()) {
                                                        withContext(Dispatchers.Main) {
                                                            imageDownloadProgress = ((index + 1) * 100f) / downloadImageList.size
                                                        }
                                                    } else {
                                                        val fullUrl = imageUrl.url
                                                        val response = mediaService.downloadFile(fullUrl)

                                                        if (response.isSuccessful) {
                                                            val inputStream: InputStream = response.body()?.byteStream() ?: return@forEachIndexed
                                                            val fileOutputStream = FileOutputStream(cacheFile)

                                                            val totalSize = response.body()?.contentLength() ?: 0
                                                            var downloadedSize = 0L

                                                            val buffer = ByteArray(8192)
                                                            var bytesRead: Int

                                                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                                                fileOutputStream.write(buffer, 0, bytesRead)
                                                                downloadedSize += bytesRead
                                                                withContext(Dispatchers.Main) {
                                                                    imageDownloadProgress = ((index + downloadedSize.toFloat() / totalSize.toFloat()) / downloadImageList.size) * 100f
                                                                }
                                                            }

                                                            fileOutputStream.flush()
                                                            fileOutputStream.close()
                                                        } else {
                                                            withContext(Dispatchers.Main) {
                                                                println("Download failed for ${imageUrl.url}")
                                                            }
                                                        }
                                                    }

                                                    withContext(Dispatchers.Main) {
                                                        imageDownloadProgress = ((index + 1) * 100f) / downloadImageList.size
                                                    }
                                                }


                                                withContext(Dispatchers.Main) {
                                                    if (downloadVideoList.size == 0){
                                                        navController.navigate(Screen.DashboardScreen.route)
                                                        appSessionManager.setCampaignId("")
                                                    }
                                                }
                                            }

                                        }
                                    }
                                    ShowProgress(imageDownloadProgress, downloadImageList[0].type, downloadImagesCount, downloadImageList.size)
                                }
                            }else{
                                if (downloadVideoList.isNullOrEmpty()){
                                    navController.navigate(Screen.DashboardScreen.route)
                                    appSessionManager.setCampaignId("")
                                }else{
                                    imageDownloadProgress = 100f
                                }
                            }

                            println("imageDownloadProgress is: ${imageDownloadProgress}")
                            println("imageDownloadProgress is: ${downloadVideoList.size}")
                            if (downloadVideoList.size > 0 && imageDownloadProgress == 100f){

                                item{
                                    LaunchedEffect(downloadVideoList.size) {
                                        if (downloadVideoList.isNotEmpty()) {
                                            scope.launch(Dispatchers.IO) {
                                                downloadVideoList.forEachIndexed { index, videoInfo ->
                                                    val cacheFile = File(context.cacheDir, videoInfo.url.substringAfterLast("/"))
                                                    downloadVideoCount += 1
                                                    if (cacheFile.exists()) {
                                                        if (calculateMD5(cacheFile) != videoInfo.md5) {
                                                            withContext(Dispatchers.Main) {
                                                                Toasty.warning(context, "MD5 not matching. Please try again!", Toasty.LENGTH_SHORT).show()
                                                                isLoading = false
                                                            }
                                                            return@launch
                                                        }

                                                        withContext(Dispatchers.Main) {
                                                            videoDownloadProgress = ((index + 1) * 100f) / downloadVideoList.size
                                                        }
                                                    } else {
                                                        val fullUrl = videoInfo.url
                                                        val response = mediaService.downloadFile(fullUrl)

                                                        if (response.isSuccessful) {
                                                            val inputStream: InputStream = response.body()?.byteStream() ?: return@forEachIndexed
                                                            val fileOutputStream = FileOutputStream(cacheFile)

                                                            val totalSize = response.body()?.contentLength() ?: 0
                                                            var downloadedSize = 0L

                                                            val buffer = ByteArray(8192)
                                                            var bytesRead: Int

                                                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                                                fileOutputStream.write(buffer, 0, bytesRead)
                                                                downloadedSize += bytesRead

                                                                withContext(Dispatchers.Main) {
                                                                    videoDownloadProgress = ((index + downloadedSize.toFloat() / totalSize.toFloat()) / downloadVideoList.size) * 100f
                                                                }
                                                            }

                                                            fileOutputStream.flush()
                                                            fileOutputStream.close()

                                                            if (calculateMD5(cacheFile) != videoInfo.md5) {
                                                                withContext(Dispatchers.Main) {
                                                                    Toasty.warning(context, "MD5 not matching. Please try again!", Toasty.LENGTH_SHORT).show()
                                                                    isLoading = false
                                                                }
                                                                return@launch
                                                            }

                                                            withContext(Dispatchers.Main) {
                                                                println("Downloaded file path: ${cacheFile.absolutePath}")
                                                                println("Downloaded file Md5: ${calculateMD5(File(cacheFile.absolutePath))}")
                                                            }

                                                        } else {
                                                            withContext(Dispatchers.Main) {
                                                                println("Download failed for ${videoInfo.url}")
                                                            }
                                                        }
                                                    }

                                                    withContext(Dispatchers.Main) {
                                                        videoDownloadProgress = ((index + 1) * 100f) / downloadVideoList.size
                                                    }
                                                }

                                                withContext(Dispatchers.Main) {
                                                    delay(500)
                                                    navController.navigate(Screen.DashboardScreen.route)
                                                    appSessionManager.setCampaignId("")
                                                }
                                            }
                                        }
                                    }
                                    ShowProgress(videoDownloadProgress, downloadVideoList[0].type, downloadVideoCount, downloadVideoList.size)
                                }
                            }
                        }
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
                        apiCount += 1
                    }

                    println("userSignInData: ${state.data.data.id}")
                    state.data.data.token?.let {token ->
                        appSessionManager.setSessionToken(token)
                    }
                    state.data.data.username?.let { appSessionManager.setBrId(it) }
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
                        apiCount += 1
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
                        apiCount += 1
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
                        apiCount += 1
                    }

                    if (state.data.data[0].image != null && state.data.data[0].image?.size!! > 0) {
                        val existingUrls = downloadImageList.map { it.url }.toSet()
                        state.data.data[0].image?.let { images ->
                            for (image in images) {
                                if (!existingUrls.contains(image)) {
                                    downloadImageList.add(DownloadModel(type = "Images", url = image))
//                                    println("downloadImageList_Path: $image            downloadImageList: ${downloadImageList.size}")
                                }
                            }
                        }
                    }


                    if (state.data.data[0].video != null && state.data.data[0].video?.size!! > 0) {
                        for (video in state.data.data[0].video!!) {
//                            println("generatedMD5 server: ${video.md5}")
                            val videoType = DownloadModel(
                                type = "Videos",
                                url = video.name,
                                md5 = video.md5
                            )
                            downloadVideoList.add(videoType)
                        }
                    }
                }
            }
        }
    }
}
