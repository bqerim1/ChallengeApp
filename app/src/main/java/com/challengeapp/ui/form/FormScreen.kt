package com.challengeapp.ui.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.challengeapp.R
import com.challengeapp.domain.model.ChoiceQuestion
import com.challengeapp.domain.model.ImageQuestion
import com.challengeapp.domain.model.Page
import com.challengeapp.domain.model.Response
import com.challengeapp.domain.model.Section
import com.challengeapp.domain.model.TextQuestion
import com.challengeapp.ui.theme.ChallengeAppTheme

@Composable
fun FormScreen(
    onImageClick: (ImageQuestion) -> Unit,
    viewModel: FormViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    FormContent(state, onResponseClick = viewModel::onResponseClick, onImageClick = onImageClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormContent(
    state: FormUiState,
    onResponseClick: (ChoiceQuestion, Long) -> Unit,
    onImageClick: (ImageQuestion) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) },
    ) { padding ->
        if (state.rows.isEmpty() && state.isRefreshing) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                val loadingDescription = stringResource(R.string.loading_form)
                CircularProgressIndicator(Modifier.semantics { contentDescription = loadingDescription })
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding() + 24.dp,
                ),
            ) {
                items(
                    items = state.rows,
                    key = { it.item.id },
                    contentType = { it.item::class },
                ) { row -> FormRowItem(row, onResponseClick, onImageClick) }
            }
        }
    }
}

@Composable
private fun FormRowItem(
    row: FormRow,
    onResponseClick: (ChoiceQuestion, Long) -> Unit,
    onImageClick: (ImageQuestion) -> Unit,
) {
    val item = row.item
    val barColor = MaterialTheme.colorScheme.tertiary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (item is Page) 16.dp else 0.dp)
            .drawBehind { drawRect(barColor, size = Size(4.dp.toPx(), size.height)) }
            .padding(start = 16.dp + 16.dp * row.depth, end = 16.dp, top = 6.dp, bottom = 6.dp),
    ) {
        when (item) {
            is Page -> Title(
                item.title,
                item.id,
                MaterialTheme.typography.headlineMedium,
                barColor,
            )
            is Section -> Title(
                item.title,
                item.id,
                sectionStyle(row.depth),
                MaterialTheme.colorScheme.primary,
            )
            is TextQuestion -> Title(item.content, item.id, questionStyle())
            is ImageQuestion -> ImageQuestionItem(item, onImageClick)
            is ChoiceQuestion -> ChoiceQuestionItem(item, row.selectedResponseIds, onResponseClick)
        }
    }
}

// Top-level sections start at titleLarge and shrink per nesting level, staying above questions.
@Composable
private fun sectionStyle(depth: Int): TextStyle {
    val size = (22 - 3 * (depth - 1)).coerceAtLeast(16)
    return MaterialTheme.typography.titleLarge.copy(fontSize = size.sp, lineHeight = (size + 6).sp)
}

@Composable
private fun questionStyle(): TextStyle = MaterialTheme.typography.bodyMedium

@Composable
private fun Title(
    text: String,
    id: Long,
    style: TextStyle,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text, style = style, color = color, modifier = Modifier.weight(1f, fill = false))
        IdBadge(id)
    }
}

@Composable
private fun IdBadge(id: Long) {
    Text(
        text = stringResource(R.string.item_id, id),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp),
    )
}

@Composable
private fun ImageQuestionItem(
    item: ImageQuestion,
    onImageClick: (ImageQuestion) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        AsyncImage(
            model = item.src,
            contentDescription = item.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(8.dp))
                // Logos in the form are transparent PNGs, often dark, so give them a light backing.
                .background(Color.White)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                .clickable(onClickLabel = stringResource(R.string.open_image)) { onImageClick(item) },
        )
        Title(item.title, item.id, questionStyle(), MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ChoiceQuestionItem(
    item: ChoiceQuestion,
    selectedResponseIds: Set<Long>,
    onResponseClick: (ChoiceQuestion, Long) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Title(item.content, item.id, questionStyle())
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item.responses.forEach { response ->
                FilterChip(
                    selected = response.id in selectedResponseIds,
                    onClick = { onResponseClick(item, response.id) },
                    label = { ResponseLabel(response) },
                )
            }
        }
        val count = selectedResponseIds.size
        val selection = if (count == 0) {
            stringResource(R.string.choice_none_selected)
        } else {
            pluralStringResource(R.plurals.choice_selected_count, count, count)
        }
        val mode = stringResource(
            if (item.multipleSelection) R.string.choice_mode_multiple else R.string.choice_mode_single,
        )
        Text(
            text = stringResource(R.string.choice_hint, selection, mode),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ResponseLabel(response: Response) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(response.label)
        response.score?.let {
            Text(it.toString(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FormContentPreview() {
    val pages = listOf(
        Page(
            1, "Main Page", listOf(
                Section(
                    2, "Chapter 1", listOf(
                        TextQuestion(3, "This is the first chapter."),
                        Section(4, "Subsection 1.1", listOf(TextQuestion(5, "Nested text"))),
                        ChoiceQuestion(6, "Pick one", false, listOf(Response(7, "Yes", 1), Response(8, "No", null))),
                    ),
                ),
            ),
        ),
    )
    ChallengeAppTheme {
        FormContent(FormUiState(rows = pages.toRows(mapOf(6L to setOf(7L)))), onResponseClick = { _, _ -> }, onImageClick = {})
    }
}
