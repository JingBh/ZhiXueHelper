package top.jingbh.zhixuehelper.data.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.first
import top.jingbh.zhixuehelper.data.util.AuthDataStore
import javax.inject.Inject

class TokenLocalDataSource @Inject constructor(
    @AuthDataStore private val dataStore: DataStore<Preferences>
) {
    suspend fun fetchToken(): String? {
        return dataStore.data.first()[KEY_TOKEN]
    }

    suspend fun putToken(newToken: String) {
        dataStore.edit { data ->
            data[KEY_TOKEN] = newToken
        }
    }
}
