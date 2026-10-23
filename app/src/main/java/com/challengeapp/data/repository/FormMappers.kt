package com.challengeapp.data.repository

import com.challengeapp.data.local.FormItemEntity
import com.challengeapp.data.local.FormItemWithResponses
import com.challengeapp.data.local.ResponseEntity
import com.challengeapp.data.remote.ItemDto
import com.challengeapp.domain.model.ChoiceQuestion
import com.challengeapp.domain.model.FormItem
import com.challengeapp.domain.model.ImageQuestion
import com.challengeapp.domain.model.Page
import com.challengeapp.domain.model.Response
import com.challengeapp.domain.model.Section
import com.challengeapp.domain.model.TextQuestion

internal data class FormEntities(
    val items: List<FormItemEntity>,
    val responses: List<ResponseEntity>,
)

// Parents are added before their children so the parentId foreign key holds on insert.
internal fun List<ItemDto>.toEntities(): FormEntities {
    val items = mutableListOf<FormItemEntity>()
    val responses = mutableListOf<ResponseEntity>()

    fun add(dto: ItemDto, parentId: Long?, position: Int) {
        items += FormItemEntity(
            id = dto.id,
            parentId = parentId,
            position = position,
            type = dto.type,
            title = dto.title,
            content = dto.content,
            src = dto.src,
            multipleSelection = dto.responseSet?.multipleSelection,
        )
        dto.responseSet?.responses?.forEachIndexed { index, response ->
            responses += ResponseEntity(response.id, dto.id, index, response.label, response.score)
        }
        dto.items.forEachIndexed { index, child -> add(child, dto.id, index) }
    }

    forEachIndexed { index, dto -> add(dto, null, index) }
    return FormEntities(items, responses)
}

internal fun List<FormItemWithResponses>.toPages(): List<Page> =
    groupBy { it.item.parentId }.childrenOf(null).filterIsInstance<Page>()

private fun Map<Long?, List<FormItemWithResponses>>.childrenOf(parentId: Long?): List<FormItem> =
    get(parentId).orEmpty()
        .sortedBy { it.item.position }
        .mapNotNull { toFormItem(it) }

private fun Map<Long?, List<FormItemWithResponses>>.toFormItem(row: FormItemWithResponses): FormItem? {
    val item = row.item
    return when (item.type) {
        "page" -> Page(item.id, item.title.orEmpty(), childrenOf(item.id))
        "section" -> Section(item.id, item.title.orEmpty(), childrenOf(item.id))
        "text" -> TextQuestion(item.id, item.content.orEmpty())
        "image" -> ImageQuestion(item.id, item.title.orEmpty(), item.src.orEmpty())
        "choice" -> ChoiceQuestion(
            id = item.id,
            content = item.content.orEmpty(),
            multipleSelection = item.multipleSelection ?: false,
            responses = row.responses
                .sortedBy { it.position }
                .map { Response(it.id, it.label, it.score) },
        )
        else -> null
    }
}
