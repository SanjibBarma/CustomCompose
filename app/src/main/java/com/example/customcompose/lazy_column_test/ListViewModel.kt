package com.example.customcompose.lazy_column_test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ListViewModel() : ViewModel() {
    private val _items = MutableStateFlow<List<Int>>(emptyList())
    val items = _items.asStateFlow()

    init {
        startAddingItems()
    }

    private fun startAddingItems() {
        viewModelScope.launch {
            var count = 1
            while (true) {
                _items.value = _items.value + count
                count++
                delay(1000)
            }
        }
    }
}