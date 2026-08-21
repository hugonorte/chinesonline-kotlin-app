package com.example.chinesonline.core.config

import org.junit.Assert.assertFalse
import org.junit.Test

class AppConfigProviderLiteTest {

    @Test
    fun `provider should return AppConfig with inactive subscription for lite flavor`() {
        val config = AppConfigProvider.provide()
        val status = config.subscriptionStatus()
        
        assertFalse("Lite flavor should NOT have advanced access", status.hasAdvancedAccess())
    }
}
