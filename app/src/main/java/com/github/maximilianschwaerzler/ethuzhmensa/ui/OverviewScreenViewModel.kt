package com.github.maximilianschwaerzler.ethuzhmensa.ui

import androidx.lifecycle.ViewModel
import com.github.maximilianschwaerzler.ethuzhmensa.data.db.FacilityInfoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OverviewScreenViewModel @Inject constructor(private val facilityInfoRepo: FacilityInfoRepository) :
    ViewModel() {

}