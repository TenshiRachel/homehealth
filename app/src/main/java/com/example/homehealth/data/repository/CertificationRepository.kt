package com.example.homehealth.data.repository

import com.example.homehealth.data.dao.CertificationDao
import com.example.homehealth.data.models.Certification

class CertificationRepository(
    private val certificationDao: CertificationDao = CertificationDao()
) {

    suspend fun getAllCertifications(): List<Certification> {
        return certificationDao.getAllCertifications()
    }

    suspend fun getCertificationById(certificationId: String): Certification? {
        return certificationDao.getCertificationById(certificationId)
    }

    suspend fun certificationExists(name: String): Boolean {
        return certificationDao.certificationExists(name)
    }

    suspend fun createCertification(certification: Certification) : Boolean {
        return certificationDao.createCertification(certification)
    }

    suspend fun updateCertification(certification: Certification): Boolean {
        return certificationDao.updateCertification(certification)
    }

    suspend fun deleteCertification(certificationId: String): Boolean {
        return certificationDao.deleteCertification(certificationId)
    }
}