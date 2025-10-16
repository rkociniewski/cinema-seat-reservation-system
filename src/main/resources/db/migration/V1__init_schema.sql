-- Movies
CREATE TABLE movie (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    duration_in_minutes INT NOT NULL
);

-- Customers (Users)
CREATE TABLE customer (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL
);

-- Halls
CREATE TABLE hall (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Seats in a Hall
CREATE TABLE seat (
    id BIGSERIAL PRIMARY KEY,
    row VARCHAR(10) NOT NULL,
    number INT NOT NULL,
    hall_id BIGINT NOT NULL REFERENCES hall(id)
);

-- Screenings
CREATE TABLE screening (
    id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL REFERENCES movie(id),
    hall_id BIGINT NOT NULL REFERENCES hall(id),
    start_time TIMESTAMP NOT NULL
);

-- Reservations
CREATE TABLE reservation (
    id BIGSERIAL PRIMARY KEY,
    screening_id BIGINT NOT NULL REFERENCES screening(id),
    customer_id BIGINT NOT NULL REFERENCES customer(id),
    created_at TIMESTAMP NOT NULL,
    state VARCHAR(20) NOT NULL
);

-- Reserved seats with ticket type
CREATE TABLE reserved_seat (
    id BIGSERIAL PRIMARY KEY,
    seat_id BIGINT NOT NULL REFERENCES seat(id),
    reservation_id BIGINT NOT NULL REFERENCES reservation(id) ON DELETE CASCADE,
    ticket_type VARCHAR(20) NOT NULL,
    UNIQUE (seat_id, reservation_id)
);
