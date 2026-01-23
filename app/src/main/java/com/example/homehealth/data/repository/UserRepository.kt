package com.example.homehealth.data.repository

import com.example.homehealth.data.dao.UserDao
import com.example.homehealth.data.models.User

class UserRepository(private val userDao: UserDao = UserDao()) {
    suspend fun createUser(user: User) : Boolean {
        return userDao.createUser(user)
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }

    suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }

    suspend fun getUsersByRole(role: String): List<User> {
        return userDao.getUsersByRole(role)
    }

    suspend fun updateUser(user: User): Boolean {
        return userDao.updateUser(user)
    }
}