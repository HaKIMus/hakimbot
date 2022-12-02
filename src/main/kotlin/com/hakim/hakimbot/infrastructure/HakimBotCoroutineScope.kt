package com.hakim.hakimbot.infrastructure

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class HakimBotCoroutineScope : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + Job()
}