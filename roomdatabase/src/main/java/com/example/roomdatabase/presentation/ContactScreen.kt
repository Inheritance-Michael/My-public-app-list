package com.example.roomdatabase.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.roomdatabase.data.Contact
import com.example.roomdatabase.data.ContactEvent
import com.example.roomdatabase.data.ContactState
import com.example.roomdatabase.data.SortType

fun contactCardShape(index: Int, total: Int, radius: Dp = 16.dp): Shape {
    return when {
        total == 1 -> RoundedCornerShape(radius)
        index == 0 -> RoundedCornerShape(
            topStart = radius, topEnd = radius,
            bottomStart = 0.dp, bottomEnd = 0.dp
        )
        index == total - 1 -> RoundedCornerShape(
            topStart = 0.dp, topEnd = 0.dp,
            bottomStart = radius, bottomEnd = radius
        )
        else -> RoundedCornerShape(0.dp)
    }
}

@Composable
private fun ContactAvatar(name: String) {
    val initial = name.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
    Surface(
        modifier = Modifier.size(44.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = initial,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ContactCard(
    contact: Contact,
    shape: Shape,
    onTap: () -> Unit,
    onDelete: () -> Unit
) {
    val displayName = listOfNotNull(contact.firstName, contact.lastName)
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { contact.phoneNumber }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onTap),
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(name = displayName)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                if (contact.phoneNumber.isNotBlank()) {
                    Text(
                        text = contact.phoneNumber,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                    )
                }
            }
            if (contact.isFavourite == true) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Favourite",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete contact",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun ContactInfoDialog(
    contact: Contact,
    onEvent: (ContactEvent) -> Unit
) {
    val displayName = listOfNotNull(contact.firstName, contact.lastName)
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { "No Name" }

    AlertDialog(
        onDismissRequest = { onEvent(ContactEvent.HideContactInfo) },
        icon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onEvent(ContactEvent.ToggleFavourite(contact)) }) {
                    Icon(
                        imageVector = if (contact.isFavourite == true)
                            Icons.Default.Star else Icons.Outlined.StarOutline,
                        contentDescription = "Toggle favourite",
                        tint = if (contact.isFavourite == true)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Phone, null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(contact.phoneNumber, style = MaterialTheme.typography.bodyMedium)
                }
                if (!contact.email.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Email, null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(contact.email, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                if (!contact.company.isNullOrBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Business, null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(contact.company, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onEvent(ContactEvent.EditContact(contact)) }) {
                Text("Edit")
            }
        },
        dismissButton = {
            TextButton(onClick = { onEvent(ContactEvent.HideContactInfo) }) {
                Text("Close")
            }
        }
    )
}

@Composable
fun AddContactDialog(
    state: ContactState,
    onEvent: (ContactEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val isEditing = state.id > 0

    AlertDialog(
        modifier = modifier,
        onDismissRequest = { onEvent(ContactEvent.HideAddNewContact) },
        title = { Text(if (isEditing) "Edit Contact" else "Add Contact") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.firstName ?: "",
                    onValueChange = { onEvent(ContactEvent.SetFirstName(it)) },
                    label = { Text("First Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.lastName ?: "",
                    onValueChange = { onEvent(ContactEvent.SetLastName(it)) },
                    label = { Text("Last Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.phoneNumber,
                    onValueChange = { onEvent(ContactEvent.SetPhoneNumber(it)) },
                    label = { Text("Phone Number *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.email ?: "",
                    onValueChange = { onEvent(ContactEvent.SetEmail(it)) },
                    label = { Text("Email (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.company ?: "",
                    onValueChange = { onEvent(ContactEvent.SetCompany(it)) },
                    label = { Text("Company (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onEvent(ContactEvent.SaveContact) }) {
                Text(if (isEditing) "Update" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { onEvent(ContactEvent.HideAddNewContact) }) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactScreen(
    state: ContactState,
    onEvent: (ContactEvent) -> Unit
) {
    val favourites = remember(state.contact) {
        state.contact.filter { it.isFavourite == true }
    }
    val allContacts = state.contact

    var searchActive by remember { mutableStateOf(false) }

    if (state.isAddingContact) {
        AddContactDialog(state = state, onEvent = onEvent)
    }
    if (state.isViewingContact && state.selectContact != null) {
        ContactInfoDialog(contact = state.selectContact, onEvent = onEvent)
    }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = !searchActive,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { onEvent(ContactEvent.ShowAddNewContact) },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Contact")
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            SearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = state.searchQuery ?: "",
                        onQueryChange = { onEvent(ContactEvent.OnSearchQueryChange(it)) },
                        onSearch = {},
                        expanded = searchActive,
                        onExpandedChange = { searchActive = it },
                        placeholder = { Text("Search contacts…") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        },
                        trailingIcon = {
                            if (searchActive) {
                                IconButton(onClick = {
                                    if (!state.searchQuery.isNullOrBlank()) {
                                        onEvent(ContactEvent.OnSearchQueryChange(""))
                                    } else {
                                        searchActive = false
                                    }
                                }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close search")
                                }
                            }
                        }
                    )
                },
                expanded = searchActive,
                onExpandedChange = { searchActive = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!state.searchQuery.isNullOrBlank()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        if (allContacts.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "No contacts match \"${state.searchQuery}\"",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        } else {
                            itemsIndexed(
                                items = allContacts,
                                key = { _, c -> "search_${c.id}" }
                            ) { index, contact ->
                                val shape = contactCardShape(index, allContacts.size)
                                ContactCard(
                                    contact = contact,
                                    shape = shape,
                                    onTap = { onEvent(ContactEvent.ShowContactInfo(contact)) },
                                    onDelete = { onEvent(ContactEvent.DeleteContact(contact)) }
                                )
                            }
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 16.dp + innerPadding.calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Sort by",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SortType.entries.forEach { sortType ->
                                FilterChip(
                                    selected = state.sortType == sortType,
                                    onClick = { onEvent(ContactEvent.SortContacts(sortType)) },
                                    label = {
                                        Text(
                                            text = sortType.name.replace("_", " "),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                if (favourites.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.padding(bottom = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Favourites",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    itemsIndexed(
                        items = favourites,
                        key = { _, c -> "fav_${c.id}" }
                    ) { index, contact ->
                        val shape = contactCardShape(index, favourites.size)
                        ContactCard(
                            contact = contact,
                            shape = shape,
                            onTap = { onEvent(ContactEvent.ShowContactInfo(contact)) },
                            onDelete = { onEvent(ContactEvent.DeleteContact(contact)) }
                        )
                    }

                    item { Spacer(Modifier.height(20.dp)) }
                }

                item {
                    Text(
                        text = if (state.searchQuery.isNullOrBlank())
                            "All Contacts (${allContacts.size})"
                        else
                            "Results for \"${state.searchQuery}\" (${allContacts.size})",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                if (allContacts.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (state.searchQuery.isNullOrBlank())
                                    "No contacts yet.\nTap + to add one."
                                else
                                    "No contacts match your search.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
                    itemsIndexed(
                        items = allContacts,
                        key = { _, c -> "all_${c.id}" }
                    ) { index, contact ->
                        val shape = contactCardShape(index, allContacts.size)
                        ContactCard(
                            contact = contact,
                            shape = shape,
                            onTap = { onEvent(ContactEvent.ShowContactInfo(contact)) },
                            onDelete = { onEvent(ContactEvent.DeleteContact(contact)) }
                        )
                    }
                }
            }
        }
    }
}