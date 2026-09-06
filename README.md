# SmartMart — Backend (Spring Boot)

RESTful backend for a retail POS ("Point of Sale") system. Handles product
inventory, customer CRM, bill generation with **18% GST**, Razorpay-powered
online payments, and PDF invoice generation.

---

## Tech Stack

| Layer       | Technology                                            |
| ----------- | ----------------------------------------------------- |
| Language    | Java 17+                                              |
| Framework   | Spring Boot 3.x (Web, Data JPA, Security, Validation) |
| Database    | MySQL 8+                                              |
| Build       | Maven                                                 |
| ORM         | Spring Data JPA / Hibernate (`ddl-auto: update`)      |
| Payments    | Razorpay (Razorpay Java SDK, test mode)               |
| PDF         | OpenPDF (bill / invoice generation)                   |
| Files       | Local file uploads (`uploads/` dir, served at `/uploads/**`) |

---

## System Architecture

```
        React SPA (:5173)
              │
              │  JSON (Axios)
              ▼
   ┌────────────────────────────┐
   │  Spring Boot REST API (:8080)│
   │  Controllers → Services → Repos │
   └───────┬───────────┬──────────┘
           │           │
           ▼           ▼
        MySQL 8     Razorpay API
     (smartmart_pos)  (orders + signature verify)
```

**Request flow — online bill:**
`POST /bills` (creates `PENDING`, deducts stock) →
`POST /payment/create-order` (Razorpay order, saves `razorpayOrderId`) →
browser checkout →
`POST /payment/verify` (HMAC-SHA256 signature check, bill → `PAID`, saves
`razorpayPaymentId`) → `GET /bills/{id}/pdf` for the invoice.

---

## Project Structure

```
src/main/java/in/rohitahire/smartmartpos/
├── SmartmartposApplication.java
├── controller/        # REST endpoints (Bill, Customer, Product, Invoice, Payment, Auth, Upload)
├── service/           # business logic (BillService, CustomerService, PaymentService, PdfService)
│   └── impl/          # BillServiceImpl (GST calc, stock deduction)
├── repository/        # Spring Data JPA repositories
├── entity/            # Bill, BillItem, Customer, Product, User
├── dto/               # request/response objects (BillRequest, PaymentVerifyRequest, ...)
├── exception/         # @RestControllerAdvice → clean error messages
└── config/            # Web / CORS / Security config
```

---

## Database Schema

- **products** — id, name, category, price, quantity, image_url
- **customers** — id, name, phone, email
- **users** — id, username, password
- **bills** — id, customer_name, customer_id (nullable), customer_phone,
  total_amount, payment_status (`PAID`/`PENDING`), created_at,
  razorpay_order_id, razorpay_payment_id
- **bill_items** — id, bill_id (FK), product_id (FK), quantity, price

`bills.customerId` links a bill to a customer (walk-in bills leave it `NULL`).
Customer analytics (bill count, total spent, last visit) are computed from the
bill ledger.

---

## API Reference

| Method | Endpoint                       | Description                        |
| ------ | ------------------------------ | ---------------------------------- |
| POST   | `/api/auth/login`              | Login (admin / admin123)           |
| GET/POST| `/api/products`               | List / create products             |
| PUT/DELETE| `/api/products/{id}`        | Update / delete product            |
| GET/POST| `/api/customers`              | List (with spend stats) / create   |
| GET    | `/api/customers/{id}`          | Customer detail + stats            |
| PUT/DELETE| `/api/customers/{id}`       | Update / delete customer           |
| POST   | `/api/bills`                   | Create bill (`PENDING`, GST 18%)   |
| GET    | `/api/bills` / `/api/invoices` | List bills/invoices                |
| GET    | `/api/bills/{id}`              | Bill detail                        |
| PUT    | `/api/bills/{id}/pay`          | Mark bill as paid (cash)           |
| GET    | `/api/bills/{id}/pdf`          | Download PDF invoice               |
| POST   | `/api/payment/create-order`    | Create Razorpay order `{billId}`   |
| POST   | `/api/payment/verify`          | Verify Razorpay signature          |
| POST   | `/api/upload`                  | Upload product image (local)       |

---

## Local Setup

Requirements: JDK 17+, Maven, MySQL 8 (server running).

```bash
# 1. Create the database
mysql -uroot -p
CREATE DATABASE smartmart_pos;

# 2. Configure credentials in src/main/resources/application.properties
spring.datasource.username=root
spring.datasource.password=yourpassword

# 3. Run
mvn spring-boot:run        # API → http://localhost:8080
```

Tables are created automatically (`ddl-auto: update`).

### Environment variables (optional)

```bash
RAZORPAY_KEY_ID=rzp_test_...
RAZORPAY_KEY_SECRET=...
```
Defaults fall back to the bundled **test** keys, so no config is required to
demo the payment flow. **Never put production/live keys in the repo.**

Test-card for Razorpay Checkout: `4111 1111 1111 1111` (any future expiry, any CVV).

---

## Key Design Points

- **Stock held in a `@Transactional` service** — quantity is validated and
  decremented atomically with bill creation; it rolls back if anything fails.
- **GST is computed server-side** (`BillServiceImpl.applyGst`, 18%) so the
  frontend can't underreport totals.
- **Lazy-loading bugs avoided** — bill→items→product relations use fetch
  joins (`findByIdWithItems`) so `GET /bills` and the PDF never hit lazy-init errors.
- **Broad-but-clean error contract** — a global `@RestControllerAdvice` turns
  domain failures into `{ "message": "..." }` with proper status codes
  (400 stock / 404 not found).
- **Payments by signature, not trust** — the backend independently computes
  `HMAC-SHA256(orderId|paymentId, secret)`; only a matching signature marks a
  bill `PAID`.