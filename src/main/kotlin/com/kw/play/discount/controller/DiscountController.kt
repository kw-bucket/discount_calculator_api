package com.kw.play.discount.controller

import com.kw.play.discount.dto.DiscountRequest
import com.kw.play.discount.dto.DiscountResponse
import com.kw.play.discount.service.DiscountService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/discount")
class DiscountController(
    private val discountService: DiscountService
) {

    @PostMapping("/calculate")
    fun calculateDiscount(
        @RequestBody request: DiscountRequest,
    ): DiscountResponse {
        val discountResponse = discountService.calculateFinalPrice(
            request.items,
            request.campaigns,
        )

        return discountResponse
    }
}
