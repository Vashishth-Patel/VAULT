package com.vashishth.vault.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vashishth.vault.Crypto.Crypto
import com.vashishth.vault.db.password
import com.vashishth.vault.repo.PassManRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository : PassManRepo
) : ViewModel(){

    fun insertLogo(password: password) = viewModelScope.launch {
        repository.insertPassword(password = password(appName = password.appName, password = Crypto().encrypt(password.password)))
    }

    private val _passList = MutableStateFlow<List<password>>(emptyList())
    val passList = _passList.asStateFlow()

    init {
        viewModelScope.launch (Dispatchers.IO){
            repository.getPassList().distinctUntilChanged()
                .collect{listOfCustomers ->
                    if (listOfCustomers.isNullOrEmpty()){
                        Log.d("Empty",": Empty List")
                    }else{
                        _passList.value = listOfCustomers
                    }
                }
        }
    }

}