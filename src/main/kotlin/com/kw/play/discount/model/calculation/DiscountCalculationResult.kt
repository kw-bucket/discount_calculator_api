package com.kw.play.discount.model.calculation

import java.math.BigDecimal

data class DiscountCalculationResult(
    val discountAmount: BigDecimal,
    val totalAfterDiscount: BigDecimal,
)
