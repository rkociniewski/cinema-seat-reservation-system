package rk.cinema.service;

import jakarta.inject.Singleton;
import rk.cinema.enums.ReservationState;
import rk.cinema.enums.TicketType;
import rk.cinema.model.*;
import rk.cinema.repository.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class ReservationService {

    private final Duration reservationTimeout;
    private final ReservationRepository reservationRepository;
    private final ReservedSeatRepository reservedSeatRepository;
    private final SeatRepository seatRepository;
    private final ScreeningRepository screeningRepository;
    private final CustomerRepository customerRepository;

    public ReservationService(
            Duration reservationTimeout, ReservationRepository reservationRepository,
            ReservedSeatRepository reservedSeatRepository,
            SeatRepository seatRepository,
            ScreeningRepository screeningRepository,
            CustomerRepository customerRepository
    ) {
        this.reservationTimeout = reservationTimeout;
        this.reservationRepository = reservationRepository;
        this.reservedSeatRepository = reservedSeatRepository;
        this.seatRepository = seatRepository;
        this.screeningRepository = screeningRepository;
        this.customerRepository = customerRepository;
    }

    public Reservation createReservation(Long customerId, Long screeningId, Map<Long, TicketType> seatIdToTicketType) {
        Customer customer = customerRepository.findById(customerId).orElseThrow();
        Screening screening = screeningRepository.findById(screeningId).orElseThrow();

        // check if seats are reserved
        for (Long seatId : seatIdToTicketType.keySet()) {
            if (reservedSeatRepository.isSeatTaken(seatId, screeningId)) {
                throw new IllegalArgumentException("Seat " + seatId + " is already reserved for this screening.");
            }
        }

        Reservation reservation = new Reservation(
                null,
                screening,
                LocalDateTime.now(),
                ReservationState.RESERVED,
                customer
        );
        reservation = reservationRepository.save(reservation);

        for (Map.Entry<Long, TicketType> entry : seatIdToTicketType.entrySet()) {
            Seat seat = seatRepository.findById(entry.getKey()).orElseThrow();
            reservedSeatRepository.save(new ReservedSeat(null, seat, reservation, entry.getValue()));
        }

        return reservation;
    }

    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
        if (reservation.state() == ReservationState.PAID) {
            throw new IllegalStateException("Cannot cancel a paid reservation.");
        }
        Reservation updated = new Reservation(
                reservation.id(),
                reservation.screening(),
                reservation.createdAt(),
                ReservationState.CANCELED,
                reservation.customer()
        );
        reservationRepository.update(updated);
    }

    public void confirmPayment(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();

        if (Duration.between(reservation.createdAt(), LocalDateTime.now()).compareTo(reservationTimeout) > 0) {
            throw new IllegalStateException("Reservation expired.");
        }

        Reservation updated = new Reservation(
                reservation.id(),
                reservation.screening(),
                reservation.createdAt(),
                ReservationState.PAID,
                reservation.customer()
        );
        reservationRepository.update(updated);
    }

    public List<Seat> getAvailableSeatsForScreening(Long screeningId) {
        Screening screening = screeningRepository.findById(screeningId).orElseThrow();
        List<Seat> allSeats = seatRepository.findByHallId(screening.hall().id());
        List<ReservedSeat> reserved = reservedSeatRepository.findByScreeningId(screeningId);
        Set<Long> reservedSeatIds = reserved.stream().map(rs -> rs.seat().id()).collect(Collectors.toSet());
        return allSeats.stream().filter(seat -> !reservedSeatIds.contains(seat.id())).toList();
    }

    public void expireOldReservations() {
        LocalDateTime expirationTime = LocalDateTime.now().minus(reservationTimeout);
        List<Reservation> expired = reservationRepository.findExpiredReservations(expirationTime);

        expired.stream().map(reservation -> new Reservation(
                reservation.id(),
                reservation.screening(),
                reservation.createdAt(),
                ReservationState.CANCELED,
                reservation.customer()
        )).forEach(reservationRepository::update);
    }
}