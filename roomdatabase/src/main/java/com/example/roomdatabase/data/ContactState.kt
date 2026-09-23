package com.example.roomdatabase.data

data class ContactState(
    val contact: List<Contact> = emptyList(),
    val id: Int = 0,
    val searchQuery: String? = "",
    val firstName: String? = "",
    val lastName: String? = "",
    val phoneNumber: String = "",
    val email: String? = "",
    val company: String? = "",
    val isFavourite: Boolean = false,
    val isAddingContact: Boolean = false,
    val isViewingContact: Boolean = false,
    val selectContact: Contact? = null,
    val sortType: SortType? = null,
)
