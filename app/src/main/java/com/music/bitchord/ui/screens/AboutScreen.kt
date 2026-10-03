package com.music.bitchord.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.music.bitchord.BuildConfig
import com.music.bitchord.R
import com.music.bitchord.data.AppUpdateChecker
import kotlinx.coroutines.launch

/**
 * About Velvette Music: branding, the running version, the developer's
 * socials and a manual update check.
 */
@Composable
fun AboutScreen(
    contentPadding: PaddingValues,
    onUpdateFound: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var checking by remember { mutableStateOf(false) }

    fun openUrl(url: String) {
        runCatching {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    fun checkForUpdates() {
        if (checking) return
        checking = true
        scope.launch {
            runCatching { AppUpdateChecker.check() }
            checking = false
            if (AppUpdateChecker.available.value != null) {
                onUpdateFound()
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.about_up_to_date),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(contentPadding),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 8.dp),
        ) {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(26.dp)),
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.about_developer, "RAJIV RAI"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }

        SettingsGroup(header = stringResource(R.string.about_connect)) {
            SettingsRow(
                title = "YouTube",
                subtitle = "@rajivlive143",
                onClick = { openUrl("https://youtube.com/@rajivlive143") },
            )
            SettingsRow(
                title = "YouTube",
                subtitle = "@rajivshortsff143",
                onClick = { openUrl("https://youtube.com/@rajivshortsff143") },
            )
            SettingsRow(
                title = "Instagram",
                subtitle = "@Rajivrai.07",
                onClick = { openUrl("https://instagram.com/Rajivrai.07") },
            )
        }

        SettingsGroup(header = stringResource(R.string.about_updates)) {
            SettingsRow(
                title = stringResource(R.string.about_check_updates),
                subtitle = stringResource(R.string.about_check_updates_subtitle),
                trailing = {
                    if (checking) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                        )
                    }
                },
                onClick = { checkForUpdates() },
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}
