package com.example.roomdatabase.data


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class ContactViewModel(
    private val dao: ContactDAO
): ViewModel() {
    private val _state = MutableStateFlow(ContactState())
    private val _sortType = MutableStateFlow(SortType.FIRSTNAME)

    private val _contacts = _sortType
        .flatMapLatest { sortType ->
            when(sortType){
                SortType.FIRSTNAME -> dao.getContactOrderByFirstName()
                SortType.LASTNAME -> dao.getContactOrderByLastName()
                SortType.PHONE_NUMBER -> dao.getContactOrderByPhoneNumber()
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val state = combine(_state, _sortType, _contacts){ state, sortType, contacts ->
        state.copy(
            contact = contacts,
            sortType = sortType,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactState())

    fun onEvent(event: ContactEvent){
        when(event){
            is ContactEvent.DeleteContact -> {
                viewModelScope.launch {
                    dao.deleteContact(event.contact)
                }
            }
            ContactEvent.HideDialog -> {
                _state.update { it.copy(
                    isAddingContact = false
                )
                }
            }
            ContactEvent.SaveContact -> {
                val firstName = state.value.firstName
                val lastName = state.value.lastName
                val phoneNumber = state.value.phoneNumber

                if(firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank()){
                    return
                }
                val contacts = Contact(
                    firstName= firstName,
                    lastName = lastName,
                    phoneNumber =  phoneNumber
                )
                viewModelScope.launch {
                    dao.upsertContact(contacts)
                }
                _state.update {
                    it.copy(
                        isAddingContact = false,
                        firstName = "",
                        lastName = "",
                        phoneNumber = ""
                    )
                }

            }
            is ContactEvent.SetFirstName -> {
                _state.update {
                    it.copy(
                       firstName =  event.firstName
                    )
                }
            }
            is ContactEvent.SetLastName -> {
                _state.update {
                    it.copy(
                        lastName = event.lastName
                    )
                }
            }
            is ContactEvent.SetPhoneNumber ->{
                _state.update {
                    it.copy(
                        phoneNumber = event.phoneNumber
                    )
                }
            }
            ContactEvent.ShowDialog -> {
                _state.update {
                    it.copy(
                        isAddingContact = true
                    )
                }
            }
            is ContactEvent.SortContacts -> {
                _sortType.value = event.sortType
            }
        }
    }
}