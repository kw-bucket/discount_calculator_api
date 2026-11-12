package com.kw.play.discount.model.campaign

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.kw.play.discount.model.cart.ItemCategory
import java.math.BigDecimal

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = DiscountCampaign.FixedAmount::class,
        name = "FIXED_AMOUNT"
    ),
    JsonSubTypes.Type(
        value = DiscountCampaign.Percentage::class,
        name = "PERCENTAGE"
    ),
    JsonSubTypes.Type(
        value = DiscountCampaign.CategoryPercentage::class,
        name = "CATEGORY_PERCENTAGE"
    ),
    JsonSubTypes.Type(
        value = DiscountCampaign.Points::class,
        name = "POINTS"
    ),
    JsonSubTypes.Type(
        value = DiscountCampaign.SpecialCampaign::class,
        name = "SPECIAL_CAMPAIGN"
    ),
)
sealed interface DiscountCampaign {
    /*
        Coupon: Discounts the entire cart by subtracting an amount from the total price
     */
    data class FixedAmount(
        val amount: BigDecimal,
    ) : DiscountCampaign

    /*
        Coupon: Discounts the entire cart by subtracting a percentage from the total price
     */
    data class Percentage(
        val percentage: BigDecimal,
    ) : DiscountCampaign

    /*
        On Top: Discount the entire amount of a specific category of items in cart
     */
    data class CategoryPercentage(
        val category: ItemCategory,
        val percentage: BigDecimal,
    ) : DiscountCampaign

    /*
        On Top: Users spent points for a fixed amount of discount (1 point = 1 THB).
        The amount will be capped at 20% of total price
     */
    data class Points(
        val customerPoints: Int,
    ) : DiscountCampaign

    /*
        Seasonal: From the total price, at every X THB, subtracting a fixed amount Y THB
     */
    data class SpecialCampaign(
        val everyAmount: BigDecimal,
        val discountAmount: BigDecimal,
    ) : DiscountCampaign
}
