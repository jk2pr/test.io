package com.hoppers.tawk.profile.screens

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.os.Parcelable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import coil.compose.rememberAsyncImagePainter
import com.hoppers.tawk.components.ComposeLocalWrapper
import com.hoppers.tawk.components.LocalOrientationMode
import com.hoppers.tawk.components.LocalSnackBarHostState
import com.hoppers.tawk.components.OfflineError
import com.hoppers.tawk.components.Page
import com.hoppers.tawk.components.Textarea
import com.hoppers.tawk.core.local.UserEntity
import com.hoppers.tawk.core.state.UiState
import com.hoppers.tawk.home.data.User
import com.hoppers.tawk.home.mappers.toUserEntity
import com.hoppers.tawk.profile.data.UserProfile
import com.hoppers.tawk.profile.viewmodels.ProfileViewModel
import com.tusharhow.connext.helper.connectivityStatus
import com.tusharhow.connext.models.ConnectionStatus
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import org.koin.core.parameter.parametersOf

/**
 * A data class representing the profile screen, which is also a composable screen and Parcelable.
 *
 * @property user The user whose profile is being displayed.
 */
@Parcelize
data class ProfileScreen(val user: User) : Screen, Parcelable {
    @Composable
    override fun Content() {
        val userProfileViewModel =
            koinScreenModel<ProfileViewModel>(parameters = { parametersOf(user) })
        val connectionStatus by connectivityStatus()
        if (connectionStatus is ConnectionStatus.Disconnected)
            OfflineError(modifier = Modifier.fillMaxSize())
        else {
            val scope = rememberCoroutineScope()
            val host = LocalSnackBarHostState.current
            val onNoteSave: (UserEntity) -> Unit = { userProfileViewModel.updateUser(it) }
            var title by remember { mutableStateOf("") }
            Page(title = { Text(text = title) }) {
                when (
                    val result = userProfileViewModel.uiState.collectAsState().value) {
                    is UiState.Content ->
                        if (result.data is UserProfile) {
                            title = result.data.name.orEmpty()
                            Form(userProfile = result.data, user = user, onNoteSave = onNoteSave)
                        }

                    is UiState.Empty -> {}
                    is UiState.Loading -> CircularProgressIndicator()
                    is UiState.Error -> OfflineError()
                }
                when (val result = userProfileViewModel.savingState.collectAsState().value) {
                    is UiState.Empty -> {}
                    is UiState.Content ->
                        LaunchedEffect(key1 = result) {
                            scope.launch {
                                host.showSnackbar(
                                    "Note for ${user.login} Saving",
                                    duration = SnackbarDuration.Short,
                                    actionLabel = "Ok"
                                )
                            }
                        }

                    is UiState.Error -> LaunchedEffect(key1 = result) {
                        scope.launch {
                            host.showSnackbar(
                                "fail to save, please try again",
                                duration = SnackbarDuration.Short,
                                actionLabel = "Ok"
                            )
                        }
                    }

                    is UiState.Loading -> {}
                }
            }
        }
    }
}
/**
 * A composable function displaying the form for user profile.
 *
 * @param userProfile The user's profile information.
 * @param user The user object.
 * @param onNoteSave A lambda function to save the note.
 */
@Composable
fun Form(userProfile: UserProfile, user: User, onNoteSave: (UserEntity) -> Unit = {}) {

    val orientation = LocalOrientationMode.current
    val aroundPadding = 8.dp

    when (orientation) {
        ORIENTATION_LANDSCAPE ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = aroundPadding)
            ) {
                ImageSection(userProfile, modifier = Modifier.size(144.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    SharedUIElements(userProfile, onNoteSave, user)
                }

            }

        ORIENTATION_PORTRAIT ->
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = aroundPadding)
                    .verticalScroll(rememberScrollState())
            )
            {
                ImageSection(
                    userProfile = userProfile, modifier = Modifier
                        .fillMaxWidth()
                        .height(144.dp)
                )
                SharedUIElements(
                    userProfile = userProfile,
                    onNoteSave = onNoteSave,
                    user = user
                )
            }
    }

}

@Composable
private fun ImageSection(userProfile: UserProfile, modifier: Modifier) {
    Image(
        contentScale = ContentScale.Crop,
        painter = rememberAsyncImagePainter(userProfile.avatarUrl),
        contentDescription = "User Avatar",
        alignment = Alignment.Center,
        modifier = modifier
    )
}

@Composable
private fun SharedUIElements(
    userProfile: UserProfile,
    onNoteSave: (UserEntity) -> Unit,
    user: User
) {

    val noteText = rememberSaveable { mutableStateOf("") }
    LaunchedEffect(key1 = userProfile) { noteText.value = user.note }

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        TextButton(onClick = {}) {
            Text(text = "Follower: ${userProfile.followers}")
        }
        TextButton(onClick = {}) {
            Text(text = "Following: ${userProfile.following}")
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Name: ${userProfile.name.orEmpty()}")
            Text(text = "Company: ${userProfile.company.orEmpty()}")
            Text(text = "Blog: ${userProfile.blog.orEmpty()}")
            Text(text = "Bio: ${userProfile.bio.orEmpty()}")
            Text(text = "Location: ${userProfile.location.orEmpty()}")
        }
    }
    Column {
        Text(text = "Note:\n")
        Textarea(text = noteText.value, onTextChange = { noteText.value = it })
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                user.note = noteText.value
                onNoteSave(user.toUserEntity())
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Save")
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
fun ProfileScreenPreview() {
    ComposeLocalWrapper(
        content = { Form(userProfile = UserProfile(name = "PAgal"), user = User()) },
    )

}

