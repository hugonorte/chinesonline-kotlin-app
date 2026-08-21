package com.example.chinesonline.core.config

class SubscriptionStatus(private val active: Boolean) {
    fun hasAdvancedAccess(): Boolean {
        return active
    }
}

interface AppConfig {
    fun subscriptionStatus(): SubscriptionStatus
}
