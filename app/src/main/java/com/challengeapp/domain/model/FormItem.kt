package com.challengeapp.domain.model

sealed interface FormItem {
    val id: Long
}

data class Page(
    override val id: Long,
    val title: String,
    val items: List<FormItem>,
) : FormItem

data class Section(
    override val id: Long,
    val title: String,
    val items: List<FormItem>,
) : FormItem

data class TextQuestion(
    override val id: Long,
    val content: String,
) : FormItem

data class ImageQuestion(
    override val id: Long,
    val title: String,
    val src: String,
) : FormItem

data class ChoiceQuestion(
    override val id: Long,
    val content: String,
    val multipleSelection: Boolean,
    val responses: List<Response>,
) : FormItem

data class Response(
    val id: Long,
    val label: String,
    val score: Int?,
)
