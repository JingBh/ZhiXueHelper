package top.jingbh.zhixuehelper.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

val KEY_TOKEN = stringPreferencesKey("token")
