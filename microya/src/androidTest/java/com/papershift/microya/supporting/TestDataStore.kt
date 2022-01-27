package com.papershift.microya.supporting

import okhttp3.Request
import okhttp3.Response

object TestDataStore {
    var request: Request? = null
    var response: Response? = null

    fun reset() {
        request = null
        response = null
    }
}

