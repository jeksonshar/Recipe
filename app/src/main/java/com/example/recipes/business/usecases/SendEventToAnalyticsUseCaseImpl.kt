package com.example.recipes.business.usecases

import android.os.Bundle
import com.example.recipes.business.usecases.interfaces.SendEventToAnalyticsUseCase
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import javax.inject.Inject

class SendEventToAnalyticsUseCaseImpl @Inject constructor() : SendEventToAnalyticsUseCase {

    private val firebaseAnalytics = Firebase.analytics

    override fun sendEventToAnalytics(eventName: String, bundle: Bundle) {
        firebaseAnalytics.logEvent(eventName, bundle)
    }
}