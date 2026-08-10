
Readme · MD
# Brokerage Platform
 
A scalable, secure microservices-based brokerage system built to explore
real-world patterns used in trading and portfolio management platforms.
 
## Tech Stack
- **Backend:** Java 25, Spring Boot 3.5
- **Database:** PostgreSQL 15 (Hibernate JPA)
- **Cache:** Redis 7 — used for caching hot-path portfolio and account lookups
- **Security:** Spring Security, JWT tokens
- **DevOps:** Docker, Kubernetes, AWS EC2
- **Monitoring:** Prometheus
## Features
- Portfolio rebalancing engine
- Real-time trade execution (BUY/SELL)
- JWT secured REST APIs
- Role based access (ADVISOR / INVESTOR / ADMIN)
- Redis caching for high performance
- AI powered portfolio summaries (OpenAI)
## API Endpoints
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | /api/v1/auth/register | Public |
| POST | /api/v1/auth/login | Public |
| GET | /api/v1/portfolios/account/{id} | All roles |
| PUT | /api/v1/portfolios/account/{id}/rebalance | ADVISOR, ADMIN |
| POST | /api/v1/trades/execute | All roles |
| GET | /api/v1/trades/account/{id} | All roles |
 
## Running Locally
```bash
# Start PostgreSQL and Redis
docker-compose up -d
 
# Start the application
mvn spring-boot:run
```
 
## Design Notes
- Role-based access control (ADVISOR / INVESTOR / ADMIN) is enforced at the
  API layer via Spring Security and JWT claims.
- Redis sits in front of PostgreSQL for frequently-read portfolio and account
  data to reduce database load on the hot path.
- The rebalancing engine and trade execution flow are built as separate
  service layers to keep pricing/execution logic decoupled from account and
  portfolio management.
- This is a portfolio/learning project, not a production system — there is
  no real brokerage backend, real funds, or real user base behind it.
## Next Steps (not done yet)
- Load-test the Redis-cached vs. uncached read paths to get real, reproducible
  latency numbers instead of estimates
- Add integration tests around the trade execution and rebalancing flows
- Wire up CI (build + test) via GitHub Actions
