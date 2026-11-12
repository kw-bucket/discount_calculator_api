package com.kw.play.discount.service

import com.kw.play.discount.dto.DiscountBreakdown
import com.kw.play.discount.dto.DiscountResponse
import com.kw.play.discount.model.campaign.CampaignCategory
import com.kw.play.discount.model.cart.CartItem
import com.kw.play.discount.model.campaign.DiscountCampaign
import com.kw.play.discount.model.campaign.campaignCategory
import com.kw.play.discount.model.campaign.campaignType
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class DiscountService(private val calculationEngine: CalculationEngine) {

    fun calculateFinalPrice(
        items: List<CartItem>,
        campaigns: List<DiscountCampaign>,
    ): DiscountResponse {
        val groupedCampaigns = campaigns.groupBy { it.campaignCategory }

        /*
            Apply only one campaign from the same category,
            i.e. users have to choose either Fixed amount or Percentage discount.
         */
        require(groupedCampaigns.values.all { it.size <= 1 }) {
            "Only one campaign per category can be applied"
        }

        val originalPrice = calculateCartTotal(items)
        val discountBreakdown = mutableListOf<DiscountBreakdown>()

        /*
           The order of applying campaigns is Coupon > On Top > Seasonal.
         */

        var currentTotal = originalPrice

        // Apply Coupon.
        campaigns.firstOrNull {
            it.campaignCategory == CampaignCategory.COUPON
        }?.also { campaign ->
            val result = calculationEngine.applyCoupon(currentTotal, campaign)

            currentTotal = result.totalAfterDiscount

            discountBreakdown.add(
                DiscountBreakdown(
                    campaignCategory = campaign.campaignCategory,
                    campaignType = campaign.campaignType,
                    discountAmount = result.discountAmount,
                )
            )
        }

        // Apply On Top.
        campaigns.firstOrNull {
            it.campaignCategory == CampaignCategory.ON_TOP
        }?.also { campaign ->
            val result = calculationEngine.applyOnTop(
                currentTotal,
                items,
                campaign
            )

            currentTotal = result.totalAfterDiscount

            discountBreakdown.add(
                DiscountBreakdown(
                    campaignCategory = campaign.campaignCategory,
                    campaignType = campaign.campaignType,
                    discountAmount = result.discountAmount,
                )
            )
        }

        // Apply Seasonal.
        campaigns.firstOrNull {
            it.campaignCategory == CampaignCategory.SEASONAL
        }?.also { campaign ->
            val result = calculationEngine.applySeasonal(currentTotal, campaign)

            currentTotal = result.totalAfterDiscount

            discountBreakdown.add(
                DiscountBreakdown(
                    campaignCategory = campaign.campaignCategory,
                    campaignType = campaign.campaignType,
                    discountAmount = result.discountAmount,
                )
            )
        }

        return DiscountResponse(
            originalPrice = originalPrice,
            totalDiscount = originalPrice.subtract(currentTotal),
            discountBreakdown = discountBreakdown,
            finalPrice = currentTotal
        )
    }

    private fun calculateCartTotal(items: List<CartItem>): BigDecimal =
        items.fold(BigDecimal.ZERO) { total, item ->
            total + item.price.multiply(item.quantity.toBigDecimal())
        }
}
