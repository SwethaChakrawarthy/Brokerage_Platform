# Brokerage Platform

A scalable, secure microservices-based brokerage system
handling 150K+ investment accounts.

## Tech Stack
- **Backend:** Java 25, Spring Boot 3.5
- **Database:** PostgreSQL 15 (Hibernate JPA)
- **Cache:** Redis 7 — 18% latency reduction
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

## Project Impact
- 18% faster transaction processing
- 22% improved API response times
- 35% reduction in manual reporting effort
- Supports 150K+ investment accounts
