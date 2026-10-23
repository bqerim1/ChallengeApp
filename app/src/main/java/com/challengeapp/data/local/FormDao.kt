package com.challengeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface FormDao {
    @Transaction
    @Query("SELECT * FROM form_item ORDER BY position")
    fun observeForm(): Flow<List<FormItemWithResponses>>

    @Insert
    suspend fun insertItems(items: List<FormItemEntity>)

    @Insert
    suspend fun insertResponses(responses: List<ResponseEntity>)

    @Query("DELETE FROM form_item")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(items: List<FormItemEntity>, responses: List<ResponseEntity>) {
        deleteAll()
        insertItems(items)
        insertResponses(responses)
    }
}
