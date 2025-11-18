package com.example.hmanager.di

import androidx.room.Room
import com.example.hmanager.AppDatabase
import com.example.hmanager.TodoRepository
import com.example.hmanager.TodoRepositoryImpl
import com.example.hmanager.TodoViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Database
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "app.db"
        ).build()
    }

    // DAO
    single { get<AppDatabase>().todoDao() }

    // Repository
    single<TodoRepository> { TodoRepositoryImpl(get()) }

    // ViewModel
    viewModel { TodoViewModel(get()) }
}