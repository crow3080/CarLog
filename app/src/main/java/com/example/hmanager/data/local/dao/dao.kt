package com.example.hmanager.data.local.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.example.hmanager.data.local.entity.InvoiceEntity
import com.example.hmanager.data.local.entity.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Transaction
    @Query("SELECT * FROM InvoiceEntity")
    fun getInvoices(): Flow<List<InvoiceWithItems>>

    @Insert
    suspend fun addInvoice(invoice: InvoiceEntity): Long

    @Insert
    suspend fun addItems(items: List<ItemEntity>)
}

data class InvoiceWithItems(
    @Embedded val invoice: InvoiceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId"
    )
    val items: List<ItemEntity>
)
