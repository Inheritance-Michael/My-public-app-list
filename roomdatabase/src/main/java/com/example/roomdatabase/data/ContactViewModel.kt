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
import kotlin.collections.emptyList

@OptIn(ExperimentalCoroutinesApi::class)
class ContactViewModel(
    private val dao: ContactDAO
): ViewModel() {
    private val searchQuery = MutableStateFlow("")

    private val _state = MutableStateFlow(ContactState())

    private val _sortType = MutableStateFlow<SortType?>(null)


    private val _contacts = combine(_sortType, searchQuery){
        sortType, query ->
        Pair(sortType,query)
    }.flatMapLatest { (sortType, query) ->
        if(query.isNotBlank()){
            dao.searchContact(query)
        }else
            when(sortType) {
                SortType.EMAIL -> dao.getContactOrderByEmail()
                SortType.COMPANY -> dao.getContactOrderByCompany()
                SortType.PHONE_NUMBER -> dao.getContactOrderByPhoneNumber()
                else -> dao.getAllContact()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList())

    val state = combine(_state, _sortType, _contacts, searchQuery){ state, sortType, contacts, query ->
        state.copy(
            contact = contacts,
            sortType = sortType,
            searchQuery = query
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ContactState())

    fun onEvent(event: ContactEvent){
        when(event){
            is ContactEvent.DeleteContact -> {
                viewModelScope.launch {
                    dao.deleteContact(event.contact)
                }
            }
            ContactEvent.HideAddNewContact -> {
                _state.update { it.copy(
                    isAddingContact = false,
                    id = 0,
                    firstName = "",
                    lastName = "",
                    phoneNumber = "",
                    email = "",
                    company = "",
                    isFavourite = false
                )
                }
            }
            ContactEvent.SaveContact -> {
                val current = state.value

                if(current.phoneNumber.isBlank()){
                    return
                }

                val contacts = Contact(
                    id = current.id,
                    firstName = current.firstName?.trim(),
                    lastName = current.lastName?.trim(),
                    phoneNumber = current.phoneNumber.trim(),
                    company = current.company?.trim(),
                    email = current.email?.trim(),
                    isFavourite = current.isFavourite
                )
                viewModelScope.launch {
                    dao.upsertContact(contacts)
                }
                _state.update {
                    it.copy(
                        id = 0,
                        isAddingContact = false,
                        firstName = "",
                        lastName = "",
                        phoneNumber = "",
                        email = "",
                        company = "",
                        isFavourite = false
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
            ContactEvent.ShowAddNewContact -> {
                _state.update {
                    it.copy(
                        isAddingContact = true
                    )
                }
            }
            is ContactEvent.SortContacts -> {
                _sortType.value = event.sortType
            }

            is ContactEvent.ToggleFavourite -> {
                viewModelScope.launch {
                    val updated = event.contact.copy(
                        isFavourite = !(event.contact.isFavourite?:false)
                    )
                    dao.upsertContact(updated)

                    _state.update {
                        if(it.selectContact?.id == updated.id){
                            it.copy(
                                selectContact = updated
                            )
                        }else it
                    }
                }
            }
            is ContactEvent.EditContact -> {
                _state.update {
                    it.copy(
                        id = event.contact.id,
                        firstName = event.contact.firstName.orEmpty(),
                        lastName = event.contact.lastName.orEmpty(),
                        phoneNumber = event.contact.phoneNumber,
                        email = event.contact.email.orEmpty(),
                        company = event.contact.company.orEmpty(),
                        isFavourite = event.contact.isFavourite?:false,
                        isAddingContact = true,
                        isViewingContact = false
                    )
                }
            }
            ContactEvent.HideContactInfo -> {
                _state.update {
                    it.copy(
                        selectContact = null,
                        isViewingContact = false
                    )
                }
            }
            is ContactEvent.OnSearchQueryChange -> {
                searchQuery.value = event.query
            }
            is ContactEvent.SetCompany -> {
                _state.update {
                    it.copy(
                        company = event.company
                    )
                }
            }
            is ContactEvent.SetEmail -> {
                _state.update {
                    it.copy(
                        email = event.email
                    )
                }
            }
            is ContactEvent.ShowContactInfo -> {
                _state.update {
                  it.copy(
                      selectContact = event.contact,
                      isViewingContact = true
                  )
                }
            }
        }
    }
}