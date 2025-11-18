package com.example.hmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.*
import com.example.hmanager.di.appModule
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.core.context.startKoin


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        startKoin {
            androidContext(this@MainActivity)
            modules(appModule)
        }

        enableEdgeToEdge()

        setContent {
            TodoScreen()
        }
    }
}


@Composable
fun TodoScreen() {
    val viewModel: TodoViewModel = koinViewModel()

    var text by remember { mutableStateOf("") }
    val todos by viewModel.todos.collectAsState()

    Column(modifier = Modifier.systemBarsPadding()) {

        Row(modifier = Modifier.fillMaxWidth()) {

            TextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter todo") }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                if (text.isNotEmpty()) {
                    viewModel.addTodo(text)
                    text = ""
                }
            }) {
                Text("Add")
            }
        }

        Spacer(Modifier.height(20.dp))

        todos.forEach { todo ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                Text(todo.title, Modifier.weight(1f))

                Button(onClick = { viewModel.delete(todo) }) {
                    Text("Delete")
                }
            }
        }
    }
}

// ------------------------------------------------------
// VIEWMODEL
// ------------------------------------------------------

class TodoViewModel(
    private val repo: TodoRepository
) : ViewModel() {

    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos

    init {
        observeTodos()
    }

    private fun observeTodos() {
        viewModelScope.launch {
            repo.getTodos().collect {
                _todos.value = it
            }
        }
    }

    fun addTodo(title: String) {
        viewModelScope.launch {
            repo.addTodo(Todo(title = title))
        }
    }

    fun delete(todo: Todo) {
        viewModelScope.launch {
            repo.deleteTodo(todo)
        }
    }
}

// ------------------------------------------------------
// REPOSITORY
// ------------------------------------------------------

interface TodoRepository {
    fun getTodos(): Flow<List<Todo>>
    suspend fun addTodo(todo: Todo)
    suspend fun deleteTodo(todo: Todo)
}

class TodoRepositoryImpl(
    private val dao: TodoDao
) : TodoRepository {

    override fun getTodos(): Flow<List<Todo>> =
        dao.getTodos().map { list ->
            list.map { Todo(it.id, it.title, it.isDone) }
        }

    override suspend fun addTodo(todo: Todo) {
        dao.add(TodoEntity(todo.id, todo.title, todo.isDone))
    }

    override suspend fun deleteTodo(todo: Todo) {
        dao.delete(TodoEntity(todo.id, todo.title, todo.isDone))
    }
}

// ------------------------------------------------------
// DATA MODELS
// ------------------------------------------------------

data class Todo(
    val id: Int = 0,
    val title: String,
    val isDone: Boolean = false
)

// ------------------------------------------------------
// ROOM DATABASE
// ------------------------------------------------------

@Database(
    entities = [TodoEntity::class],
    version = 1,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}

@Dao
interface TodoDao {

    @Query("SELECT * FROM todos ORDER BY id DESC")
    fun getTodos(): Flow<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(todo: TodoEntity)

    @Delete
    suspend fun delete(todo: TodoEntity)
}

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val isDone: Boolean = false
)
