package com.github.maximilianschwaerzler.ethuzhmensa.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.FacilityInfoRepository
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.MensaDatabase
import com.github.maximilianschwaerzler.ethuzhmensa.data.utils.updateFacilityInfoDB
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewScreenViewModel @Inject constructor(
    private val facilityInfoRepo: FacilityInfoRepository,
    @ApplicationContext val context: Context
) :
    ViewModel() {
    fun printAllFacilities() = viewModelScope.launch {
        Log.d("OverviewScreenViewModel", facilityInfoRepo.getAllFacilities().toString())
    }

    val facilities = facilityInfoRepo.observeAllFacilities()
        .stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(1000), listOf())

    fun loadFacilityInfoDB() = viewModelScope.launch {
        updateFacilityInfoDB(context)
    }

    fun purgeDB() = viewModelScope.launch(Dispatchers.IO) {
        MensaDatabase.getInstance(context).clearAllTables()
    }
}