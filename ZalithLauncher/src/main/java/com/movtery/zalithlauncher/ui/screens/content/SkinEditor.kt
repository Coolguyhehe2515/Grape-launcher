/*
 * Grape Launcher
 * Based on Zalith Launcher 2.
 * Licensed under GPL-3.0.
 */

package com.movtery.zalithlauncher.ui.screens.content

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.account.Account
import com.movtery.zalithlauncher.game.account.AccountsManager
import com.movtery.zalithlauncher.game.account.wardrobe.SkinModelType
import com.movtery.zalithlauncher.ui.components.SkinPreview3D
import java.io.File
import java.io.FileOutputStream
import kotlin.math.floor
import androidx.compose.ui.platform.LocalContext

@Composable
fun SkinChooserDialog(
    account: Account,
    onDismiss: () -> Unit,
    onMakeSkin: () -> Unit,
    onImportSkin: (android.net.Uri) -> Unit
) {
    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let(onImportSkin)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.padding(20.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Choose Skin",
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Choose how you want to change your Minecraft skin.",
                    style = MaterialTheme.typography.bodyMedium
                )

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        onDismiss()
                        onMakeSkin()
                    }
                ) {
                    Text("Make a Skin")
                }

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        picker.launch(arrayOf("image/png"))
                    }
                ) {
                    Text("Import Skin")
                }
            }
        }
    }
}

@Composable
fun SkinEditorDialog(
    account: Account,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val initialBitmap = remember(account) {
        runCatching {
            if (account.hasSkinFile) {
                BitmapFactory.decodeFile(account.getSkinFile().absolutePath)
            } else {
                context.assets.open("steve.png").use(BitmapFactory::decodeStream)
            }
        }.getOrNull()?.copy(Bitmap.Config.ARGB_8888, true)
            ?: Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
    }

    var bitmap by remember { mutableStateOf(initialBitmap) }
    var selectedColor by remember { mutableStateOf(Color(0xFFFFFFFF)) }

    val palette = listOf(
        Color.Black,
        Color.White,
        Color.Red,
        Color(0xFFFF9800),
        Color.Yellow,
        Color.Green,
        Color.Cyan,
        Color.Blue,
        Color(0xFF9C27B0),
        Color(0xFFFF69B4)
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Skin Editor",
                    style = MaterialTheme.typography.headlineSmall
                )

                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .background(Color.LightGray)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(selectedColor) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        val x = floor(offset.x / size.width * bitmap.width).toInt()
                                        val y = floor(offset.y / size.height * bitmap.height).toInt()
                                        if (x in 0 until bitmap.width && y in 0 until bitmap.height) {
                                            val edited = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                                            edited.setPixel(x, y, selectedColor.toArgb())
                                            bitmap = edited
                                        }
                                    },
                                    onDrag = { change, _ ->
                                        val x = floor(change.position.x / size.width * bitmap.width).toInt()
                                        val y = floor(change.position.y / size.height * bitmap.height).toInt()
                                        if (x in 0 until bitmap.width && y in 0 until bitmap.height) {
                                            val edited = bitmap.copy(Bitmap.Config.ARGB_8888, true)
                                            edited.setPixel(x, y, selectedColor.toArgb())
                                            bitmap = edited
                                        }
                                        change.consume()
                                    }
                                )
                            }
                    ) {
                        drawImage(
                            bitmap.asImageBitmap(),
                            dstSize = androidx.compose.ui.unit.IntSize(size.width.toInt(), size.height.toInt())
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    palette.forEach { color ->
                        Box(
                            modifier = Modifier
                                .padding(3.dp)
                                .size(26.dp)
                                .background(color, CircleShape)
                                .clickable { selectedColor = color }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            runCatching {
                                account.getSkinFile().parentFile?.mkdirs()
                                FileOutputStream(account.getSkinFile()).use {
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                                }
                                AccountsManager.refreshWardrobe()
                            }
                            onDismiss()
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
