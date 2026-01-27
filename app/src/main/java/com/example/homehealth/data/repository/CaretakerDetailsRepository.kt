package com.example.homehealth.data.repository

import com.example.homehealth.data.dao.CaretakerDetailsDao
import com.example.homehealth.data.models.CaretakerDetails

class CaretakerDetailsRepository (
    private val caretakerDetailsDao: CaretakerDetailsDao = CaretakerDetailsDao()
) {
    suspend fun getCaretakerProfileByUid(caretakerUid: String): CaretakerDetails? {
        return caretakerDetailsDao.getCaretakerDetailsById(caretakerUid)
    }

    suspend fun createCaretakerProfile(caretakerProfile: CaretakerDetails) : Boolean {
        return caretakerDetailsDao.createCaretakerDetails(caretakerProfile)
    }

    suspend fun updateCaretakerProfile(caretakerProfile: CaretakerDetails): Boolean {
        return caretakerDetailsDao.updateCaretakerDetails(caretakerProfile)
    }

}