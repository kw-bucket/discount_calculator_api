package com.kw.play.discount.model.campaign

import com.kw.play.discount.model.campaign.DiscountCampaign.CategoryPercentage
import com.kw.play.discount.model.campaign.DiscountCampaign.FixedAmount
import com.kw.play.discount.model.campaign.DiscountCampaign.Percentage
import com.kw.play.discount.model.campaign.DiscountCampaign.Points
import com.kw.play.discount.model.campaign.DiscountCampaign.SpecialCampaign

val DiscountCampaign.campaignCategory: CampaignCategory
    get() = when (this) {
        is FixedAmount,
        is Percentage, ->
            CampaignCategory.COUPON

        is CategoryPercentage,
        is Points, ->
            CampaignCategory.ON_TOP

        is SpecialCampaign, ->
            CampaignCategory.SEASONAL
    }

val DiscountCampaign.campaignType: CampaignType
    get() = when (this) {
        is FixedAmount -> CampaignType.FIXED_AMOUNT
        is Percentage -> CampaignType.PERCENTAGE
        is CategoryPercentage -> CampaignType.CATEGORY_PERCENTAGE
        is Points -> CampaignType.POINTS
        is SpecialCampaign -> CampaignType.SPECIAL_CAMPAIGN
    }
