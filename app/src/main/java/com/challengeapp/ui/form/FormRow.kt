package com.challengeapp.ui.form

import androidx.compose.runtime.Immutable
import com.challengeapp.domain.model.ChoiceQuestion
import com.challengeapp.domain.model.FormItem
import com.challengeapp.domain.model.Page
import com.challengeapp.domain.model.Section

@Immutable
data class FormRow(
    val item: FormItem,
    val depth: Int,
    val selectedResponseIds: Set<Long> = emptySet(),
)

// Flat rows keep the LazyColumn lazy for long forms; depth drives indent and font size.
fun List<Page>.toRows(selections: Map<Long, Set<Long>> = emptyMap()): List<FormRow> {
    val rows = mutableListOf<FormRow>()

    fun add(item: FormItem, depth: Int) {
        val selected = if (item is ChoiceQuestion) selections[item.id].orEmpty() else emptySet()
        rows += FormRow(item, depth, selected)
        val children = when (item) {
            is Page -> item.items
            is Section -> item.items
            else -> emptyList()
        }
        children.forEach { add(it, depth + 1) }
    }

    forEach { add(it, 0) }
    return rows
}
