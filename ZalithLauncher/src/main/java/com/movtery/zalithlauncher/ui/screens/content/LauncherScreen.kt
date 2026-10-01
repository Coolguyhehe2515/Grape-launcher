/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.account.AccountsManager
import com.movtery.zalithlauncher.game.account.wardrobe.SkinModelType
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.BackgroundCard
import com.movtery.zalithlauncher.ui.components.ScalingActionButton
import com.movtery.zalithlauncher.ui.components.SkinPreview3D
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.content.elements.PlayerFace
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionIconImage
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel

private const val GRAPE_VERSION = "1.12.2"

@Composable
fun LauncherScreen(
    backStackViewModel: ScreenBackStackViewModel,
    navigateToVersions: (Version) -> Unit,
    onLaunchGame: (Version?) -> Unit,
    onOpenLink: (String) -> Unit,
    startGuideOnce: (com.movtery.zalithlauncher.ui.guide.GuideKeys.Keys) -> Unit,
) {
    BaseScreen(
        screenKey = NormalNavKey.LauncherMain,
        currentKey = backStackViewModel.mainScreen.currentKey
    ) { isVisible ->
        val context = LocalContext.current
        val account by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()
        val refreshWardrobe by AccountsManager.refreshWardrobe.collectAsStateWithLifecycle()
        val versions by VersionsManager.versions.collectAsStateWithLifecycle()
        val isRefreshing by VersionsManager.isRefreshing.collectAsStateWithLifecycle()
        var showSkinChooser by remember { mutableStateOf(false) }
        var showSkinEditor by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            VersionsManager.refresh(
                tag = "GrapeLauncher",
                trySetVersion = GRAPE_VERSION
            )
        }

        val version = versions.firstOrNull { it.getVersionName() == GRAPE_VERSION }
        val canLaunch = !isRefreshing && version?.isValid() == true

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                BackgroundCard(
                    modifier = Modifier.clickable {
                        backStackViewModel.mainScreen.navigateTo(
                            screenKey = NormalNavKey.AccountManager(
                                com.movtery.zalithlauncher.game.account.FirstLoginMenu.NONE
                            )
                        )
                    },
                    shape = MaterialTheme.shapes.large
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (account != null) {
                            PlayerFace(
                                account = account,
                                avatarSize = 42.dp
                            )
                        } else {
                            Icon(
                                modifier = Modifier.size(42.dp),
                                painter = painterResource(R.drawable.ic_add),
                                contentDescription = null
                            )
                        }

                        Column {
                            Text(
                                text = account?.username
                                    ?: stringResource(R.string.account_add_new_account),
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1
                            )
                            Text(
                                text = stringResource(R.string.generic_account),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(230.dp)
                    .clickable(enabled = account != null) {
                        showSkinChooser = true
                    },
                contentAlignment = Alignment.Center
            ) {
                SkinPreview3D(
                    skinFile = account?.getSkinFile()?.takeIf { it.exists() },
                    capeFile = account?.getCapeFile()?.takeIf { it.exists() },
                    modelType = account?.skinModelType?.takeIf { it != SkinModelType.NONE },
                    modifier = Modifier.fillMaxSize(),
                    refreshKey = refreshWardrobe
                )
            }

            Text(
                text = account?.username?.let { "Skin" } ?: "Sign in to customize your skin",
                style = MaterialTheme.typography.labelLarge
            )

            Button(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                enabled = account != null,
                onClick = { showSkinChooser = true }
            ) {
                Text("Choose Skin")
            }

            Spacer(modifier = Modifier.weight(1f))

            VersionIconImage(
                version = version,
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = GRAPE_VERSION,
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = if (isRefreshing) {
                    stringResource(R.string.generic_loading)
                } else if (version?.isValid() == true) {
                    "Minecraft Java Edition"
                } else {
                    "1.12.2 is not installed"
                },
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            ScalingActionButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = canLaunch,
                onClick = {
                    version?.let(onLaunchGame)
                }
            ) {
                Text(
                    text = stringResource(R.string.main_launch_game),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        if (showSkinChooser && account != null) {
            SkinChooserDialog(
                account = account,
                onDismiss = { showSkinChooser = false },
                onMakeSkin = { showSkinEditor = true },
                onImportSkin = { uri ->
                    showSkinChooser = false
                    runCatching {
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            account.getSkinFile().parentFile?.mkdirs()
                            account.getSkinFile().outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        AccountsManager.refreshWardrobe()
                    }
                }
            )
        }

        if (showSkinEditor && account != null) {
            SkinEditorDialog(
                account = account,
                onDismiss = { showSkinEditor = false }
            )
        }
    }
}
