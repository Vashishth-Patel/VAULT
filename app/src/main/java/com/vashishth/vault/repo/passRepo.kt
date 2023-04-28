package com.vashishth.vault.repo

import com.vashishth.vault.db.PassManDao
import com.vashishth.vault.db.password
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PassManRepo @Inject constructor(
    private val passManDao: PassManDao
) {

    suspend fun insertPassword(password: password) = withContext(Dispatchers.IO){
        passManDao.insertPassword(password)
    }

    suspend fun updatePassword(password: password) = withContext(Dispatchers.IO){
        passManDao.updatePassword(password.password,password.appName)
    }

    suspend fun deletePassword(password: password) = passManDao.deletePassword(password = password)

    fun getPassList() : Flow<List<password>> = passManDao.getPassList().flowOn(Dispatchers.IO)
        .conflate()
}