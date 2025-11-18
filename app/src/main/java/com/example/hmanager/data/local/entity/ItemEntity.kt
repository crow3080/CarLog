package com.example.hmanager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val invoiceId: Int,
    val name: String,
    val quantity: Int,
    val price: Double
)