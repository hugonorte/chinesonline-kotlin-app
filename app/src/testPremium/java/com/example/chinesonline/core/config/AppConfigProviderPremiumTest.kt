package com.example.chinesonline.core.config

import org.junit.Assert.assertTrue
import org.junit.Test

class AppConfigProviderPremiumTest {

    @Test
    fun `provider should return AppConfig with active subscription for premium flavor`() {
        val config = AppConfigProvider.provide()
        val status = config.subscriptionStatus()
        
        assertTrue("Premium flavor SHOULD have advanced access", status.hasAdvancedAccess())
    }
}
