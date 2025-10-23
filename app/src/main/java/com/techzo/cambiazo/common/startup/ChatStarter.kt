package com.techzo.cambiazo.common.startup

import com.techzo.cambiazo.common.Constants
import com.techzo.cambiazo.common.UserPreferences
import com.techzo.cambiazo.data.repository.ChatRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatStarter @Inject constructor(
    private val chatRepository: ChatRepository,
    private val prefs: UserPreferences
) {
    fun startIfLoggedIn() {
        val inMemoryId = Constants.user?.id?.toString()
        if (!inMemoryId.isNullOrBlank()) {
            chatRepository.startUserInbox(inMemoryId)
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            val storedId = prefs.getUserIdOnce()
            if (storedId != null) {
                chatRepository.startUserInbox(storedId.toString())
            }
        }
    }
}