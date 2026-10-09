# Rate Limiter Service

A thread-safe, reusable rate-limiting middleware built with Spring Boot, implementing the Token Bucket algorithm.

## Why

Rate limiting prevents clients from overwhelming an API with excessive requests. This project implements it as a **Servlet Filter**, meaning it can be dropped into any Spring Boot application and automatically protect every endpoint — no per-controller code required.

## How it works

- Each client (identified by IP) gets its own token bucket.
- Each bucket holds a capacity of tokens and refills steadily over time.
- Every request consumes 1 token; if none are available, the request is rejected with `429 Too Many Requests`.

## Tech

- Java 21, Spring Boot 3.5
- Thread-safety via `synchronized` + `ConcurrentHashMap`
- Tested with JUnit 5, including concurrency stress tests

## Run locally

```bash
./mvnw spring-boot:run
```

## Try it

```bash
curl "http://localhost:8080/api/check?clientId=user1"; echo
```

Run it more than 5 times quickly — you'll get rate-limited with a `429` and a `Retry-After` header.

## Configuration

Edit `src/main/resources/application.properties`:

```properties
ratelimiter.capacity=5
ratelimiter.refill-rate=1
```

## Alternative Implementation

A Sliding Window rate-limiting algorithm is implemented separately on the `feature/sliding-window` branch, as a second approach alongside Token Bucket.