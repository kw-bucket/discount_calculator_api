package com.kw.play.discount.service

import com.kw.play.discount.model.campaign.CampaignCategory
import com.kw.play.discount.model.campaign.DiscountCampaign
import com.kw.play.discount.model.cart.CartItem
import com.kw.play.discount.model.cart.ItemCategory
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class DiscountServiceTest {

    private val calculationEngine = CalculationEngine()
    private val discountService = DiscountService(calculationEngine)

    @Test
    fun `should return original price when there are no campaigns`() {
        val items = listOf(
            CartItem(
                name = "T-Shirt",
                category = ItemCategory.CLOTHING,
                price = BigDecimal("600"),
                quantity = 1
            )
        )

        val result = discountService.calculateFinalPrice(items, emptyList())

        result.originalPrice shouldHaveAmount "600"
        result.totalDiscount shouldHaveAmount "0"
        result.finalPrice shouldHaveAmount "600"
        result.discountBreakdown shouldBe emptyList()
    }

    @Test
    fun `should apply fixed amount coupon`() {
        val items = createCart(total = "600")
        val campaigns = listOf(
            DiscountCampaign.FixedAmount(BigDecimal("50"))
        )

        val result = discountService.calculateFinalPrice(items, campaigns)

        result.originalPrice shouldHaveAmount "600"
        result.totalDiscount shouldHaveAmount "50"
        result.finalPrice shouldHaveAmount "550"
    }

    @Test
    fun `should apply percentage coupon`() {
        val items = createCart(total = "600")
        val campaigns = listOf(
            DiscountCampaign.Percentage(BigDecimal("10"))
        )

        val result = discountService.calculateFinalPrice(items, campaigns)

        result.totalDiscount shouldHaveAmount "60"
        result.finalPrice shouldHaveAmount "540"
    }

    @Test
    fun `should apply category percentage discount`() {
        val items = listOf(
            CartItem(
                name = "T-Shirt",
                category = ItemCategory.CLOTHING,
                price = BigDecimal("500"),
                quantity = 2
            ),
            CartItem(
                name = "Phone",
                category = ItemCategory.ELECTRONICS,
                price = BigDecimal("1490"),
                quantity = 1
            ),
            CartItem(
                name = "Bag",
                category = ItemCategory.CLOTHING,
                price = BigDecimal("50"),
                quantity = 1
            )
        )
        val campaigns = listOf(
            DiscountCampaign.CategoryPercentage(
                category = ItemCategory.CLOTHING,
                percentage = BigDecimal("15")
            )
        )

        val result = discountService.calculateFinalPrice(items, campaigns)

        result.originalPrice shouldHaveAmount "2540"
        result.totalDiscount shouldHaveAmount "157.5"
        result.finalPrice shouldHaveAmount "2382.5"
    }

    @Test
    fun `should apply points discount`() {
        val items = createCart(total = "830")
        val campaigns = listOf(
            DiscountCampaign.Points(customerPoints = 68)
        )

        val result = discountService.calculateFinalPrice(items, campaigns)

        result.totalDiscount shouldHaveAmount "68"
        result.finalPrice shouldHaveAmount "762"
    }

    @Test
    fun `should limit points discount to 20 percent of current total`() {
        val items = createCart(total = "600")
        val campaigns = listOf(
            DiscountCampaign.Points(customerPoints = 200)
        )

        val result = discountService.calculateFinalPrice(items, campaigns)

        result.totalDiscount shouldHaveAmount "120"
        result.finalPrice shouldHaveAmount "480"
    }

    @Test
    fun `should apply seasonal special campaign`() {
        val items = createCart(total = "830")
        val campaigns = listOf(
            DiscountCampaign.SpecialCampaign(
                everyAmount = BigDecimal("300"),
                discountAmount = BigDecimal("40")
            )
        )

        val result = discountService.calculateFinalPrice(items, campaigns)

        result.totalDiscount shouldHaveAmount "80"
        result.finalPrice shouldHaveAmount "750"
    }

    @Test
    fun `should apply campaigns in the correct order`() {
        val items = createCart(total = "830")

        // Deliberately provide campaigns in a different order.
        val campaigns = listOf(
            DiscountCampaign.SpecialCampaign(
                everyAmount = BigDecimal("300"),
                discountAmount = BigDecimal("40")
            ),
            DiscountCampaign.Points(customerPoints = 68),
            DiscountCampaign.FixedAmount(BigDecimal("50"))
        )

        val result = discountService.calculateFinalPrice(items, campaigns)

        // Coupon: 830 - 50 = 780
        // On Top: 780 - 68 = 712
        // Seasonal: floor(712 / 300) * 40 = 80
        // Final price: 712 - 80 = 632

        result.originalPrice shouldHaveAmount "830"
        result.totalDiscount shouldHaveAmount "198"
        result.finalPrice shouldHaveAmount "632"

        result.discountBreakdown.map { it.campaignCategory } shouldContainExactly listOf(
            CampaignCategory.COUPON,
            CampaignCategory.ON_TOP,
            CampaignCategory.SEASONAL
        )
    }

    @Test
    fun `should reject multiple campaigns in the same category`() {
        val items = createCart(total = "600")
        val campaigns = listOf(
            DiscountCampaign.FixedAmount(BigDecimal("50")),
            DiscountCampaign.Percentage(BigDecimal("10"))
        )

        shouldThrow<IllegalArgumentException> {
            discountService.calculateFinalPrice(items, campaigns)
        }
    }

    private fun createCart(total: String): List<CartItem> {
        return listOf(
            CartItem(
                name = "Test Item",
                category = ItemCategory.CLOTHING,
                price = BigDecimal(total),
                quantity = 1
            )
        )
    }

    private infix fun BigDecimal.shouldHaveAmount(expected: String) {
        this.compareTo(BigDecimal(expected)) shouldBe 0
    }
}
