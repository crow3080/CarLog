package com.example.hmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientName: String,
    val date: Long,
    val total: Double
)