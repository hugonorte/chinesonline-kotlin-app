package com.example.chinesonline.core.config

class PremiumAppConfig : AppConfig {
    override fun subscriptionStatus(): SubscriptionStatus {
        return SubscriptionStatus(true)
    }
}

object AppConfigProvider {
    fun provide(): AppConfig {
        return PremiumAppConfig()
    }
}
