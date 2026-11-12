package com.kw.play.discount.model.cart

import java.math.BigDecimal

data class CartItem(
    val name: String,
    val category: ItemCategory,
    val price: BigDecimal,
    val quantity: Int,
)
