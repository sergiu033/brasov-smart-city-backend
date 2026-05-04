CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMP,
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);

CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE recommendation_categories (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(150) NOT NULL
);

CREATE TABLE recommendations (
    id BIGSERIAL PRIMARY KEY,
    category_id BIGINT NOT NULL REFERENCES recommendation_categories(id),
    title VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    location VARCHAR(255),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE parking_zones (
    id BIGSERIAL PRIMARY KEY,
    zone_code VARCHAR(50) NOT NULL UNIQUE,
    tariff_per_hour NUMERIC(10, 2) NOT NULL,
    tariff_per_day NUMERIC(10, 2) NOT NULL
);

CREATE TABLE city_reports (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    category VARCHAR(100) NOT NULL,
    description TEXT,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    photo_url VARCHAR(500),
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE municipal_services (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO recommendation_categories (code, display_name)
VALUES ('gastronomie', 'Gastronomie'),
       ('natura', 'Natura'),
       ('plimbare', 'Plimbare in oras'),
       ('cultura', 'Cultura'),
       ('experiente', 'Experiente');

INSERT INTO recommendations (category_id, title, location, description)
VALUES (1, 'Restaurant local recomandat', 'Centrul vechi', 'Punct de pornire pentru modulul de activitati.'),
       (2, 'Traseu usor spre Tampa', 'Tampa', 'Exemplu de recomandare seeded.');

INSERT INTO events (title, description, location, start_time, end_time)
VALUES ('Eveniment pilot', 'Date initiale pentru modulul de evenimente.', 'Piata Sfatului', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL '2 hours');

INSERT INTO parking_zones (zone_code, tariff_per_hour, tariff_per_day)
VALUES ('A', 5.00, 30.00),
       ('B', 3.00, 20.00);

INSERT INTO municipal_services (name, status)
VALUES ('Programari ghiseu', 'planned'),
       ('Plata taxe locale', 'planned');
