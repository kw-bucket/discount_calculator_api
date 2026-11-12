package com.kw.play.discount.dto

import java.math.BigDecimal

data class DiscountResponse (
    val originalPrice: BigDecimal,
    val discountBreakdown: List<DiscountBreakdown>,
    val totalDiscount: BigDecimal,
    val finalPrice: BigDecimal,
)
