# Brașov Smart City Backend

Monorepo for the Brașov Smart City platform: Spring Boot API (`smart-city-app`) and the OTP routing engine (`algo-rutare-main/algo-rutare-main`).

## Routing engine (`algo-rutare-main/algo-rutare-main`)

Large binaries (`otp.jar`, `brasov.pbf`) are not in the repo. Download them with the setup script, then start OTP and the Node bridge with Docker Compose.

### Download engine data

```bash
cd algo-rutare-main/algo-rutare-main
chmod +x setup-engine.sh
./setup-engine.sh
```

### Run OTP + bridge (Docker)

```bash
cd algo-rutare-main/algo-rutare-main
docker compose up -d
```

- OTP: http://localhost:8080  
- Bridge: http://localhost:8081  

## Smart City API (`smart-city-app`)

### PostgreSQL (Docker)

```bash
cd smart-city-app
docker compose up -d
```

Database: `smartcity` / user `smartcity` / password `smartcity` on port **5433**.

Run the Spring Boot app locally.
