package com.example.roomdatabase.data

sealed interface ContactEvent {
    object SaveContact:  ContactEvent
    data class SetFirstName(val firstName: String): ContactEvent
    data class SetLastName(val lastName: String): ContactEvent
    data class SetPhoneNumber(val phoneNumber: String): ContactEvent

    data class SetEmail(val email: String): ContactEvent

    data class SetCompany(val company: String): ContactEvent

    data class ToggleFavourite(val contact: Contact) : ContactEvent

    object ShowAddNewContact: ContactEvent

    object HideAddNewContact: ContactEvent

    data class OnSearchQueryChange(val query: String): ContactEvent

    data class EditContact(val contact: Contact): ContactEvent
    data class SortContacts(val sortType: SortType ): ContactEvent
    data class DeleteContact(val contact: Contact): ContactEvent

    data class ShowContactInfo(val contact: Contact): ContactEvent

    object HideContactInfo: ContactEvent
}