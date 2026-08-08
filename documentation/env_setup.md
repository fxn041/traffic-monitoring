# Environment Setup
This document describes the required environment variables for the project. Ensure you create a `.env` file in the project's root directory and populate it with the following variables.

| Variable Name | Description | Example Value |
|---|---|---|
| `POSTGRES_USER` | Username for the database used for conduktor | `conduktor` |
| `POSTGRES_PASSWORD` | Password for the database used for conduktor | `secret123` |
| `CONDUKTOR_ADMIN_EMAIL` | Admin email for conduktor, must follow email format rules | `admin@tuhh.local` |
| `CONDUKTOR_ADMIN_PASSWORD` | Password for the admin conduktor user, must be at least 8 characters long, include a lowercase letter, a uppercase letter, a number and a symbol | `P@ssw0rd` |
| `GRAFANA_ADMIN_USER` | Username of the admin user of grafana | `admin` |
| `GRAFANA_ADMIN_PASSWORD` | PASSWORD of the admin user of grafana | `P@ssw0rd` |

### Steps to Set Up
1. Create a `.env` file in the project's root directory.
2. Copy the table above and **replace the example values** with your actual configuration.

### Example .env file
```ini
POSTGRES_USER="conduktor"
POSTGRES_PASSWORD="secret123"
CONDUKTOR_ADMIN_EMAIL="admin@tuhh.local"
CONDUKTOR_ADMIN_PASSWORD="P@ssw0rd"
GRAFANA_ADMIN_USER="admin"
GRAFANA_ADMIN_PASSWORD="P@ssw0rd"
```
