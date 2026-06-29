package hirify.analytics.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import hirify.analytics.ui.i18n.LocalAppStrings

class AboutScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val strings = LocalAppStrings.current
        
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(strings.appName) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = strings.appName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                // Typically you use a build config for the version
                Text(
                    text = "Version v${hirify.analytics.core.BuildConfig.APP_VERSION}",
                    fontSize = 16.sp
                )
                
                Text(
                    text = strings.freeSoftware,
                    fontSize = 16.sp
                )
                
                Text(
                    text = strings.builtWith,
                    fontSize = 16.sp
                )
                
                Text(
                    text = strings.sourceCodeAvailable + "https://github.com/VladLerkin/hirify-analytics",
                    fontSize = 16.sp
                )
                
                Text(
                    text = "${strings.author}domfindus@gmail.com",
                    fontSize = 16.sp
                )
            }
        }
    }
}
