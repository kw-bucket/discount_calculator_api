package com.kw.play.discount.service

import com.kw.play.discount.model.cart.CartItem
import com.kw.play.discount.model.calculation.DiscountCalculationResult
import com.kw.play.discount.model.campaign.DiscountCampaign
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class CalculationEngine {

    fun applyCoupon(
        total: BigDecimal,
        campaign: DiscountCampaign,
    ): DiscountCalculationResult = when (campaign) {
        is DiscountCampaign.FixedAmount ->
            applyCouponFixedAmount(total, campaign)

        is DiscountCampaign.Percentage ->
            applyCouponPercentage(total, campaign)

        else -> noDiscountApplied(total)
    }

    fun applyOnTop(
        total: BigDecimal,
        items: List<CartItem>,
        campaign: DiscountCampaign,
    ): DiscountCalculationResult = when (campaign) {
        is DiscountCampaign.CategoryPercentage ->
            applyOnTopCategoryPercentage(total, items, campaign)

        is DiscountCampaign.Points ->
            applyOnTopPoints(total, campaign)

        else -> noDiscountApplied(total)
    }

    fun applySeasonal(
        total: BigDecimal,
        campaign: DiscountCampaign,
    ): DiscountCalculationResult = when (campaign) {
        is DiscountCampaign.SpecialCampaign ->
            applySeasonalSpecialCampaign(total, campaign)

        else -> noDiscountApplied(total)
    }

    private fun noDiscountApplied(
        total: BigDecimal
    ) = DiscountCalculationResult(
        discountAmount = BigDecimal.ZERO,
        totalAfterDiscount = total
    )

    private fun applyCouponFixedAmount(
        total: BigDecimal,
        campaign: DiscountCampaign.FixedAmount,
    ): DiscountCalculationResult {
        val discount = campaign.amount.min(total)

        return DiscountCalculationResult(
            discountAmount = discount,
            totalAfterDiscount = total.subtract(discount),
        )
    }

    private fun applyCouponPercentage(
        total: BigDecimal,
        campaign: DiscountCampaign.Percentage,
    ): DiscountCalculationResult {
        val discount = total.multiply(campaign.percentage.movePointLeft(2))

        return DiscountCalculationResult(
            discountAmount = discount,
            totalAfterDiscount = total.subtract(discount),
        )
    }

    private fun applyOnTopCategoryPercentage(
        total: BigDecimal,
        items: List<CartItem>,
        campaign: DiscountCampaign.CategoryPercentage,
    ): DiscountCalculationResult {
        val categoryTotal = items
            .filter { it.category == campaign.category }
            .fold(BigDecimal.ZERO) { sum, item ->
                sum + item.price.multiply(item.quantity.toBigDecimal())
            }

        val discount = categoryTotal.multiply(campaign.percentage.movePointLeft(2))

        return DiscountCalculationResult(
            discountAmount = discount,
            totalAfterDiscount = total.subtract(discount),
        )
    }

    private fun applyOnTopPoints(
        total: BigDecimal,
        campaign: DiscountCampaign.Points,
    ): DiscountCalculationResult {
        val maximumDiscountPercent = 20.toBigDecimal().movePointLeft(2)

        val pointsDiscount = campaign.customerPoints.toBigDecimal()
        val maximumDiscount = total.multiply(maximumDiscountPercent)
        val actualDiscount = minOf(pointsDiscount, maximumDiscount)

        return DiscountCalculationResult(
            discountAmount = actualDiscount,
            totalAfterDiscount = total.subtract(actualDiscount),
        )
    }

    private fun applySeasonalSpecialCampaign(
        total: BigDecimal,
        campaign: DiscountCampaign.SpecialCampaign,
    ): DiscountCalculationResult {
        val discountMultiplier =
            total.divideToIntegralValue(campaign.everyAmount)

        val discount = discountMultiplier
            .multiply(campaign.discountAmount)

        return DiscountCalculationResult(
            discountAmount = discount,
            totalAfterDiscount = total.subtract(discount),
        )
    }
}
