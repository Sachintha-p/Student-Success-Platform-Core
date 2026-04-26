# Student Success Platform — Backend API

A production-ready Spring Boot 3.2 / Java 17 REST API for **SLIIT's Student Success Platform**. This platform provides four core feature modules secured with JWT, utilizing PostgreSQL for persistence, Flyway for migrations, and OpenAI GPT-4 integration for intelligent features.

## 🚀 Key Features

* **Smart Team Matchmaker**: AI-powered group formation and skills-based matching.
* **Job Matchmaker & ATS**: CV analysis, ATS scoring, and AI-driven job suggestions.
* **Campus Engagement Hub**: Event management, project milestone tracking, Kanban tasks, and file sharing.
* **AI Academic Assistant**: GPT-4 powered Q&A with conversation history and study resources.
* **Enterprise Architecture**: SOLID principles, global exception handling, and base entities with auditing.

## 🛠 Tech Stack

| Category | Technology |
| :--- | :--- |
| **Framework** | Spring Boot 3.2.3 |
| **Language** | Java 17 |
| **Database** | PostgreSQL 15+ |
| **Migration** | Flyway 10.10.0 |
| **AI Integration** | Spring AI with OpenAI GPT-4 |
| **Security** | JWT (jjwt 0.12.5) |
| **Documentation**| Springdoc OpenAPI 2.3.0 (Swagger) |
| **Utilities** | Lombok, MapStruct, Jackson |

## 📋 Prerequisites

| Tool | Recommended Version |
| :--- | :--- |
| **Java** | 17+ |
| **Maven** | 3.9+ |
| **PostgreSQL** | 15+ |
| **Node.js** | 18+ (for frontend development) |

## ⚙️ Setup and Installation

### 1. Environment Configuration
Create a `.env` file in the project root with the following variables:

| Variable | Description |
| :--- | :--- |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |
| `JWT_SECRET` | HS512 signing key (at least 64 characters) |
| `OPENAI_API_KEY` | OpenAI API key for AI features |
| `MAIL_USERNAME` | SMTP username for notifications |
| `MAIL_PASSWORD` | SMTP application password |

### 2. Database Initialization
```bash
psql -U postgres -c "CREATE DATABASE student_platform;"
