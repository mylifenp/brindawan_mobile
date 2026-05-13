package com.brindawan.mobile.screens.detail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow

class DetailViewModel(private val museumRepository: com.brindawan.mobile.data.MuseumRepository) : ViewModel() {
    fun getObject(objectId: Int): Flow<com.brindawan.mobile.data.MuseumObject?> =
        museumRepository.getObjectById(objectId)
}
