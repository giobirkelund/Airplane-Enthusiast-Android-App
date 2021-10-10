package com.example.flyapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SharedViewModelFactory (mContext: Context): ViewModelProvider.Factory{
   val context = mContext

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SharedViewModel(context) as T
    }
}