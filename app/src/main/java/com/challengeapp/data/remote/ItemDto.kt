package com.challengeapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemDto(
    val id: Long,
    val type: String,
    val title: String? = null,
    val content: String? = null,
    val src: String? = null,
    val items: List<ItemDto> = emptyList(),
    @SerialName("response_set") val responseSet: ResponseSetDto? = null,
)

@Serializable
data class ResponseSetDto(
    val id: Long,
    @SerialName("multiple_selection") val multipleSelection: Boolean,
    val responses: List<ResponseDto>,
)

@Serializable
data class ResponseDto(
    val id: Long,
    val label: String,
    val score: Int? = null,
)
