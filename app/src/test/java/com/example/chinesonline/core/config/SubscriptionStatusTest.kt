package com.example.chinesonline.core.config

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SubscriptionStatusTest {

    @Test
    fun `hasAdvancedAccess returns true when status is active`() {
        val status = SubscriptionStatus(true)
        assertTrue("Expected hasAdvancedAccess to be true", status.hasAdvancedAccess())
    }

    @Test
    fun `hasAdvancedAccess returns false when status is inactive`() {
        val status = SubscriptionStatus(false)
        assertFalse("Expected hasAdvancedAccess to be false", status.hasAdvancedAccess())
    }
}
