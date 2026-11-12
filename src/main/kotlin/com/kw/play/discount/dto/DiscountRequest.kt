package com.kw.play.discount.dto

import com.kw.play.discount.model.cart.CartItem
import com.kw.play.discount.model.campaign.DiscountCampaign

data class DiscountRequest(
    val items: List<CartItem>,
    val campaigns: List<DiscountCampaign>,
)
