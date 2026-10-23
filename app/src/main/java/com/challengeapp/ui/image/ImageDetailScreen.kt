package com.challengeapp.ui.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import coil3.compose.AsyncImage
import com.challengeapp.R
import com.challengeapp.ui.theme.ChallengeAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageDetailScreen(
    title: String,
    src: String,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = stringResource(R.string.back))
                    }
                },
            )
        },
    ) { padding ->
        AsyncImage(
            model = src,
            contentDescription = title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White),
        )
    }
}

@Preview
@Composable
private fun ImageDetailScreenPreview() {
    ChallengeAppTheme {
        ImageDetailScreen(title = "Welcome Image", src = "", onBack = {})
    }
}
