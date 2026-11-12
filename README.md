# Discount Calculator API

A REST API built with **Kotlin** and **Spring Boot** for calculating discounts across multiple campaign types.

The API supports coupon, on-top, and seasonal discounts. Multiple campaigns can be applied to the same cart, following a predefined calculation order.

## Features

- Calculate the total price of shopping cart items.
- Support five discount campaign types.
- Apply multiple campaigns in a single request.
- Allow only one campaign per discount category.
- Apply discounts in the required order.
- Return a breakdown of applied discounts.

## Technologies

- Kotlin
- Spring Boot
- Maven
- JUnit 5
- MockK
- Kotest Assertions

## Discount Campaigns

The API supports three discount categories:

| Category | Campaign Type | Description |
|---|---|---|
| Coupon | `FIXED_AMOUNT` | Deduct a fixed amount from the cart total. |
| Coupon | `PERCENTAGE` | Deduct a percentage from the cart total. |
| On Top | `CATEGORY_PERCENTAGE` | Deduct a percentage from items in a selected category. |
| On Top | `POINTS` | Use customer points as a discount. |
| Seasonal | `SPECIAL_CAMPAIGN` | Deduct a fixed amount for every specified spending threshold. |

### Discount Rules

1. Only one campaign can be applied per category.
2. Campaigns are applied in the following order:
   - Coupon
   - On Top
   - Seasonal
3. Fixed amount discounts cannot exceed the current total.
4. Points discounts are capped at 20% of the current total.
5. Seasonal discounts are calculated using the current total after previous discounts.

## API Endpoint

### Calculate Discount

**POST** `/api/discount/calculate`

### Request Format

The request contains shopping cart items and discount campaigns.

```json
{
  "items": [
    {
      "name": "T-Shirt",
      "category": "CLOTHING",
      "price": 350,
      "quantity": 1
    },
    {
      "name": "Hat",
      "category": "CLOTHING",
      "price": 250,
      "quantity": 1
    },
    {
      "name": "Belt",
      "category": "ACCESSORIES",
      "price": 230,
      "quantity": 1
    },
    {
      "name": "Watch",
      "category": "ACCESSORIES",
      "price": 500,
      "quantity": 1
    }
  ],
  "campaigns": [
    {
      "type": "SPECIAL_CAMPAIGN",
      "everyAmount": 300,
      "discountAmount": 40
    },
    {
      "type": "CATEGORY_PERCENTAGE",
      "category": "CLOTHING",
      "percentage": 15
    },
    {
      "type": "FIXED_AMOUNT",
      "amount": 50
    }
  ]
}
```

### Supported Item Categories

- `CLOTHING`
- `ACCESSORIES`
- `ELECTRONICS`

### Campaign Request Properties

| Campaign Type | Properties |
|---|---|
| `FIXED_AMOUNT` | `amount` |
| `PERCENTAGE` | `percentage` |
| `CATEGORY_PERCENTAGE` | `category`, `percentage` |
| `POINTS` | `customerPoints` |
| `SPECIAL_CAMPAIGN` | `everyAmount`, `discountAmount` |

## Example Calculation

Using the request above:

### Shopping Cart

| Item | Category | Price |
|---|---|---:|
| T-Shirt | CLOTHING | 350 THB |
| Hat | CLOTHING | 250 THB |
| Belt | ACCESSORIES | 230 THB |
| Watch | ACCESSORIES | 500 THB |
| **Total** | | **1,330 THB** |

### Applied Discounts

**1. Coupon – Fixed Amount**

- Original total: 1,330 THB
- Discount: 50 THB
- Remaining total: 1,280 THB

**2. On Top – Category Percentage**

- Clothing subtotal: 350 + 250 = 600 THB
- Discount: 600 × 15% = 90 THB
- Remaining total: 1,190 THB

**3. Seasonal – Special Campaign**

- Discount: floor(1,190 / 300) × 40 = 120 THB
- Remaining total: 1,070 THB

### Expected Response

```json
{
  "originalPrice": 1330,
  "totalDiscount": 260,
  "discountBreakdown": [
    {
      "campaignCategory": "COUPON",
      "campaignType": "FIXED_AMOUNT",
      "discountAmount": 50
    },
    {
      "campaignCategory": "ON_TOP",
      "campaignType": "CATEGORY_PERCENTAGE",
      "discountAmount": 90
    },
    {
      "campaignCategory": "SEASONAL",
      "campaignType": "SPECIAL_CAMPAIGN",
      "discountAmount": 120
    }
  ],
  "finalPrice": 1070
}
```

## Running the Application

Run the application using the Maven wrapper:

```bash
mvn spring-boot:run
```

The API will be available at:

```text
http://localhost:8080/api/discount/calculate
```

## Running Tests

Run all tests:

```bash
mvn test
```

The tests cover discount calculations, campaign application order, and campaign category validation.
