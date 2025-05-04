package rk.cinema.service;

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rk.cinema.configuration.ReservationConfig;
import rk.cinema.enums.ReservationState;
import rk.cinema.enums.TicketType;
import rk.cinema.model.*;
import rk.cinema.repository.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MicronautTest
class ReservationServiceTest {

    ReservationRepository reservationRepository;
    ReservedSeatRepository reservedSeatRepository;
    SeatRepository seatRepository;
    ScreeningRepository screeningRepository;
    CustomerRepository customerRepository;
    ReservationService reservationService;

    @Inject
    ReservationConfig reservationConfig;

    @BeforeEach
    void setUp() {
        // Mocks
        reservationRepository = mock(ReservationRepository.class);
        reservedSeatRepository = mock(ReservedSeatRepository.class);
        seatRepository = mock(SeatRepository.class);
        screeningRepository = mock(ScreeningRepository.class);
        customerRepository = mock(CustomerRepository.class);

        // Service under test
        reservationService = new ReservationService(
                reservationRepository,
                reservedSeatRepository,
                seatRepository,
                screeningRepository,
                customerRepository,
                reservationConfig
        );
    }

    @Test
    void testCreateReservation_successful() {
        // given
        Long customerId = 1L;
        Long screeningId = 2L;
        Long seatId = 3L;

        Customer customer = new Customer(customerId, "alice@example.com", "Alice");
        Screening screening = new Screening(screeningId, null, null, LocalDateTime.now().plusDays(1));
        Seat seat = new Seat(seatId, "A", 1, new Hall(1L, "Sala 1"));

        Reservation savedReservation = new Reservation(10L, screening, LocalDateTime.now(), ReservationState.RESERVED, customer);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(screeningRepository.findById(screeningId)).thenReturn(Optional.of(screening));
        when(reservedSeatRepository.isSeatTaken(seatId, screeningId)).thenReturn(false);
        when(seatRepository.findById(seatId)).thenReturn(Optional.of(seat));
        when(reservationRepository.save(any())).thenReturn(savedReservation);

        Map<Long, TicketType> seatToTicket = Map.of(seatId, TicketType.STANDARD);

        // when
        Reservation result = reservationService.createReservation(customerId, screeningId, seatToTicket);

        // then
        assertNotNull(result);
        verify(reservationRepository).save(any());
        verify(reservedSeatRepository).save(any());
    }
}
