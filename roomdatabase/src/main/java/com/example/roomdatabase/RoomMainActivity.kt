package com.example.roomdatabase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room3.Room
import com.example.roomdatabase.data.ContactDatabase
import com.example.roomdatabase.data.ContactViewModel
import com.example.roomdatabase.presentation.ContactScreen
import com.example.roomdatabase.ui.theme.MyApplicationTheme

class RoomMainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder<ContactDatabase>(
            context = applicationContext,
            name = "contact.db"
        ).build()
    }

    private val viewModel by viewModels<ContactViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return ContactViewModel(db.contactDao()) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val state by viewModel.state.collectAsState()
                ContactScreen(state, viewModel::onEvent)
            }
        }
    }
}