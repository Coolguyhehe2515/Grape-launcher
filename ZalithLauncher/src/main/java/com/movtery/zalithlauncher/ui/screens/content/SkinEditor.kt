/*
 * Grape Launcher
 * Based on Zalith Launcher 2.
 * Licensed under GPL-3.0.
 */

package com.movtery.zalithlauncher.ui.screens.content

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.webkit.WebViewAssetLoader
import com.movtery.zalithlauncher.game.account.Account
import com.movtery.zalithlauncher.game.account.AccountsManager
import java.io.ByteArrayOutputStream

@Composable
fun SkinChooserDialog(
    account: Account,
    onDismiss: () -> Unit,
    onMakeSkin: () -> Unit,
    onImportSkin: (android.net.Uri) -> Unit
) {
    val picker = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.OpenDocument()
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
                Text("Choose Skin", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Choose how you want to change your Minecraft skin.",
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
                    onClick = { picker.launch(arrayOf("image/png")) }
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
    val assetLoader = remember(context) {
        WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(context))
            .build()
    }

    val initialSkin = remember(account) {
        runCatching {
            val bitmap = if (account.hasSkinFile) {
                BitmapFactory.decodeFile(account.getSkinFile().absolutePath)
            } else {
                context.assets.open("steve.png").use(BitmapFactory::decodeStream)
            }
            val normalized = Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(normalized)
            if (bitmap != null) {
                canvas.drawBitmap(
                    bitmap,
                    null,
                    android.graphics.Rect(0, 0, 64, 64),
                    null
                )
                if (bitmap !== normalized) bitmap.recycle()
            }
            val output = ByteArrayOutputStream()
            normalized.compress(Bitmap.CompressFormat.PNG, 100, output)
            normalized.recycle()
            Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
        }.getOrDefault("")
    }

    val colors = listOf(
        "#000000", "#FFFFFF", "#FF0000", "#FF9800", "#FFFF00",
        "#00C853", "#00BCD4", "#2979FF", "#9C27B0", "#FF4081"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Skin Editor", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Paint directly on the 3D model. Drag to rotate, then paint a visible face.",
                    style = MaterialTheme.typography.bodySmall
                )

                val bridge = remember(account) {
                    SkinEditorBridge(account) { onDismiss() }
                }

                val webView = remember(context) {
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.allowFileAccess = false
                        settings.allowContentAccess = true
                        overScrollMode = WebView.OVER_SCROLL_NEVER
                        addJavascriptInterface(bridge, "GrapeSkin")
                        webViewClient = object : WebViewClient() {
                            override fun shouldInterceptRequest(
                                view: WebView,
                                request: WebResourceRequest
                            ): WebResourceResponse? {
                                return assetLoader.shouldInterceptRequest(request.url)
                            }
                        }
                        loadUrl("https://appassets.androidplatform.net/assets/skin_editor.html")
                    }
                }

                DisposableEffect(webView) {
                    onDispose {
                        webView.stopLoading()
                        webView.destroy()
                    }
                }

                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(390.dp),
                    factory = { webView },
                    update = {
                        it.evaluateJavascript(
                            "loadEditorSkin('data:image/png;base64,$initialSkin')",
                            null
                        )
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    colors.forEach { hex ->
                        val color = Color(android.graphics.Color.parseColor(hex))
                        Box(
                            modifier = Modifier
                                .size(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(color, CircleShape)
                                    .clickable {
                                        webView.evaluateJavascript("setPaintColor('$hex')", null)
                                    }
                            )
                        }
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
                            webView.evaluateJavascript("exportSkin()", null)
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

private class SkinEditorBridge(
    private val account: Account,
    private val onSaved: () -> Unit
) {
    @JavascriptInterface
    fun saveSkin(dataUrl: String) {
        runCatching {
            val encoded = dataUrl.substringAfter("base64,", dataUrl)
            val bytes = Base64.decode(encoded, Base64.DEFAULT)
            account.getSkinFile().parentFile?.mkdirs()
            account.getSkinFile().writeBytes(bytes)
            AccountsManager.refreshWardrobe()
        }
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            onSaved()
        }
    }
}
