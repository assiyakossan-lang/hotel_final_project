# Hotel Booking System

Spring Boot web application for hotel room booking.

## Run
1. Open project in IntelliJ IDEA as Maven project.
2. Use JDK 17.
3. Run `HotelBookingApplication.java`.
4. Open `http://localhost:8080`.

## Demo accounts
- admin@hotel.kz / admin123
- user@hotel.kz / user123

## Main features
- Registration and login
- Roles: USER and ADMIN
- Room booking
- Customer reviews with 1-5 rating
- Admin panel for rooms, users, bookings and reviews
- REST API
- H2 database inside the project folder

## Entities
- User
- Room
- Booking
- Review

## Architecture
Controller -> Service -> Repository -> Database

## H2 console
Open `http://localhost:8080/h2-console`

JDBC URL:
`jdbc:h2:file:./data/hotel_booking_db`

User: `sa`
Password: empty
