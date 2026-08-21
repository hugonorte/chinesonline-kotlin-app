package com.example.chinesonline.core.config

class LiteAppConfig : AppConfig {
    override fun subscriptionStatus(): SubscriptionStatus {
        return SubscriptionStatus(false)
    }
}

object AppConfigProvider {
    fun provide(): AppConfig {
        return LiteAppConfig()
    }
}
