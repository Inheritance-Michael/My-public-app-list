# 🗄️ Room Database Module (`:roomdatabase`)

This module is a complete implementation of a Contacts management app using **AndroidX Room 3.0.3**, **Jetpack Compose (Material 3)**, and **MVI / Unidirectional Data Flow**.

## Highlights

- **Room 3 (`androidx.room3`)**: SQLite abstraction using KSP annotation processing with reified `Room.databaseBuilder<ContactDatabase>()`.
- **Reactive Queries**: Exposes `Flow<List<Contact>>` from `ContactDAO` reacting instantaneously to database updates.
- **Compose Material 3 UI**: Full-screen `SearchBar`, sorting filter chips, dynamic card corner rounding, and interactive contact creation dialog.
- **Unidirectional Architecture**: Strict separation of concerns between `ContactState`, `ContactEvent`, and `ContactViewModel`.

For full setup instructions, architecture breakdown, and diagram, please see the [main project README](../README.md).
