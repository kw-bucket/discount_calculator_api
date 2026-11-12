package com.kw.play.discount.service

import com.kw.play.discount.model.campaign.DiscountCampaign
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CalculationEngineTest {

    private val calculationEngine = CalculationEngine()

    @Test
    fun `should apply fixed amount coupon`() {
        val total = BigDecimal("600")
        val campaign = DiscountCampaign.FixedAmount(
            amount = BigDecimal("50")
        )

        val result = calculationEngine.applyCoupon(total, campaign)

        result.totalAfterDiscount shouldHaveAmount "550"
        result.discountAmount shouldHaveAmount "50"
    }

    @Test
    fun `should not discount more than the current total with fixed amount coupon`() {
        val total = BigDecimal("600")
        val campaign = DiscountCampaign.FixedAmount(
            amount = BigDecimal("800")
        )

        val result = calculationEngine.applyCoupon(total, campaign)

        result.totalAfterDiscount shouldHaveAmount "0"
        result.discountAmount shouldHaveAmount "600"
    }

    @Test
    fun `should apply percentage coupon`() {
        val total = BigDecimal("600")
        val campaign = DiscountCampaign.Percentage(
            percentage = BigDecimal("10")
        )

        val result = calculationEngine.applyCoupon(total, campaign)

        result.totalAfterDiscount shouldHaveAmount "540"
        result.discountAmount shouldHaveAmount "60"
    }

    @Test
    fun `should return zero discount for zero percent coupon`() {
        val total = BigDecimal("600")
        val campaign = DiscountCampaign.Percentage(
            percentage = BigDecimal("0")
        )

        val result = calculationEngine.applyCoupon(total, campaign)

        result.totalAfterDiscount shouldHaveAmount "600"
        result.discountAmount shouldHaveAmount "0"
    }

    private infix fun BigDecimal.shouldHaveAmount(expected: String) {
        this.compareTo(BigDecimal(expected)) shouldBe 0
    }
}
