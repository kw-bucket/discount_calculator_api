package com.kw.play.discount.dto

import com.kw.play.discount.model.campaign.CampaignCategory
import com.kw.play.discount.model.campaign.CampaignType
import java.math.BigDecimal

data class DiscountBreakdown(
    val campaignCategory: CampaignCategory,
    val campaignType: CampaignType,
    val discountAmount: BigDecimal
)
