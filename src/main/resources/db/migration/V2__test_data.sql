-- Customers
INSERT INTO customer (email, name)
VALUES ('alice@example.com', 'Alice Liddell'),
       ('bob@example.com', 'Bob Stone');

-- Movies
INSERT INTO movie (title, duration_in_minutes)
VALUES ('The Matrix', 136),
       ('Inception', 148);

-- Halls
INSERT INTO hall (name)
VALUES ('Sala 1'), ('Sala 2');

-- Seats
DO $$
DECLARE
    r CHAR;
    n INT;
    hall_id BIGINT := 1;
BEGIN
    FOR r IN 'A'..'E' LOOP
        FOR n IN 1..10 LOOP
            INSERT INTO seat (row, number, hall_id)
            VALUES (r, n, hall_id);
        END LOOP;
    END LOOP;
END $$;

-- screenings
INSERT INTO screening (movie_id, hall_id, start_time)
VALUES (1, 1, NOW() + INTERVAL '1 day'),
       (2, 2, NOW() + INTERVAL '2 days');

-- Alice Reservations
INSERT INTO reservation (screening_id, created_at, state, customer_id)
VALUES (1, NOW(), 'RESERVED', 1);

-- reserved seats
INSERT INTO reserved_seat (seat_id, reservation_id, ticket_type)
VALUES (1, 1, 'CHILD_DISCOUNT'),
       (2, 1, 'STANDARD'),
       (3, 1, 'SENIOR_DISCOUNT');
