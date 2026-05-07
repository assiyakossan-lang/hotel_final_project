# Postman Guide

## 1. Install Postman
Download Postman from the official website and install it.

## 2. Import collection
Open Postman -> Import -> select:

`HotelBooking_Postman_Collection.json`

## 3. Start project
Run Spring Boot application in IntelliJ IDEA.

Base URL:

`http://localhost:8080`

## 4. Login first
Send request:

`POST http://localhost:8080/api/auth/login`

Body:

```json
{
  "email": "admin@hotel.kz",
  "password": "admin123"
}
```

Postman will save the session cookie automatically.

## 5. Test CRUD
Use requests:
- GET Rooms
- POST Room
- PUT Room
- DELETE Room
- GET Users
- POST User
- GET Bookings
- POST Booking

If API returns 401 or 403, login as admin again.
