package com.example.recipes.business.usecases.interfaces

import android.os.Bundle

interface SendEventToAnalyticsUseCase {
    fun sendEventToAnalytics(eventName: String, bundle: Bundle)
}