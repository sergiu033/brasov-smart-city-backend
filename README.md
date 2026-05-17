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

### PostgreSQL only (Docker)

Start only the database if you run the Spring Boot app on your machine:

```bash
cd smart-city-app
docker compose up -d postgres
```

Database: `smartcity` / user `smartcity` / password `smartcity` on port **5433** (`jdbc:postgresql://localhost:5433/smartcity`).

Run the app locally with Maven:

```bash
cd smart-city-app
./mvnw spring-boot:run
```

API: http://localhost:8083

### API + PostgreSQL (Docker, Jib)

The app image is built with the [Jib](https://github.com/GoogleContainerTools/jib) Maven plugin (no Dockerfile). Docker must be running.

**1. Build the image**

```bash
cd smart-city-app
./mvnw compile jib:dockerBuild
```

On Windows:

```powershell
cd smart-city-app
.\mvnw.cmd compile jib:dockerBuild
```

**2. Start Postgres and the API**

```bash
docker compose up -d postgres smart-city-app
```

**3. Rebuild after code changes**

Run `jib:dockerBuild` again, then recreate the app container:

```bash
./mvnw compile jib:dockerBuild
docker compose up -d --force-recreate smart-city-app
```

- API: http://localhost:8083  
- Swagger UI: http://localhost:8083/swagger-ui.html  
- Database (from host): port **5433**, same credentials as above

Optional environment variables (e.g. `GOOGLE_CLIENT_ID`, `JWT_SECRET`) can be added under the `smart-city-app` service in `docker-compose.yml` or passed when starting the container.
