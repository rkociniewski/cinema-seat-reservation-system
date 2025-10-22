package rk.powermilk.cinema.repository

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import rk.powermilk.cinema.enums.ReservationState
import rk.powermilk.cinema.model.Customer
import rk.powermilk.cinema.model.Hall
import rk.powermilk.cinema.model.Movie
import rk.powermilk.cinema.model.Reservation
import rk.powermilk.cinema.model.Screening
import java.time.LocalDateTime

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ReservationRepositoryTest : DatabaseTestBase() {

    @Inject
    lateinit var reservationRepository: ReservationRepository

    @Inject
    lateinit var customerRepository: CustomerRepository

    @Inject
    lateinit var screeningRepository: ScreeningRepository

    @Inject
    lateinit var movieRepository: MovieRepository

    @Inject
    lateinit var hallRepository: HallRepository

    private lateinit var testCustomer: Customer
    private lateinit var testScreening: Screening

    @BeforeEach
    fun setUp() {
        reservationRepository.deleteAll()
        screeningRepository.deleteAll()
        customerRepository.deleteAll()
        movieRepository.deleteAll()
        hallRepository.deleteAll()

        dbStart()

        // Create test data
        testCustomer = customerRepository.save(Customer(0, "test@example.com", "Test User"))
        val movie = movieRepository.save(Movie(0, "Test Movie", 120))
        val hall = hallRepository.save(Hall(0, "Test Hall"))
        testScreening = screeningRepository.save(Screening(0, movie, hall, LocalDateTime.now().plusDays(1)))
    }

    @AfterAll
    fun tearDown() {
        dbStop()
    }

    @Test
    fun `should save and retrieve reservation`() {
        // given
        val reservation = Reservation(
            0,
            testScreening,
            LocalDateTime.now(),
            ReservationState.RESERVED,
            testCustomer
        )

        // when
        val saved = reservationRepository.save(reservation)

        // then
        assertNotNull(saved.id)
        assertTrue(saved.id > 0)
        assertEquals(testScreening.id, saved.screening.id)
        assertEquals(testCustomer.id, saved.customer.id)
        assertEquals(ReservationState.RESERVED, saved.state)
    }

    @Test
    fun `should find reservations by customer id`() {
        // given
        val customer2 = customerRepository.save(Customer(0, "other@example.com", "Other User"))

        reservationRepository.save(
            Reservation(0, testScreening, LocalDateTime.now(), ReservationState.RESERVED, testCustomer)
        )
        reservationRepository.save(
            Reservation(0, testScreening, LocalDateTime.now(), ReservationState.PAID, testCustomer)
        )
        reservationRepository.save(
            Reservation(0, testScreening, LocalDateTime.now(), ReservationState.RESERVED, customer2)
        )

        // when
        val reservations = reservationRepository.findByCustomerId(testCustomer.id)

        // then
        assertEquals(2, reservations.size)
        assertTrue(reservations.all { it.customer.id == testCustomer.id })
    }

    @Test
    fun `should find expired reservations`() {
        // given
        val now = LocalDateTime.now()
        val twoHoursAgo = now.minusHours(2)
        val oneHourAgo = now.minusHours(1)
        val recent = now.minusMinutes(10)

        // Expired reservations (created > 1 hour ago, still RESERVED)
        reservationRepository.save(
            Reservation(0, testScreening, twoHoursAgo, ReservationState.RESERVED, testCustomer)
        )
        reservationRepository.save(
            Reservation(0, testScreening, oneHourAgo.minusMinutes(1), ReservationState.RESERVED, testCustomer)
        )

        // Not expired - recent
        reservationRepository.save(
            Reservation(0, testScreening, recent, ReservationState.RESERVED, testCustomer)
        )

        // Not expired - already paid
        reservationRepository.save(
            Reservation(0, testScreening, twoHoursAgo, ReservationState.PAID, testCustomer)
        )

        // when - looking for reservations older than 1 hour
        val expirationTime = now.minusHours(1)
        val expired = reservationRepository.findExpiredReservations(expirationTime)

        // then
        assertEquals(2, expired.size)
        assertTrue(expired.all { it.state == ReservationState.RESERVED })
        assertTrue(expired.all { it.createdAt.isBefore(expirationTime) })
    }

    @Test
    fun `should return empty list when no expired reservations`() {
        // given
        val now = LocalDateTime.now()
        reservationRepository.save(
            Reservation(0, testScreening, now.minusMinutes(10), ReservationState.RESERVED, testCustomer)
        )
        reservationRepository.save(
            Reservation(0, testScreening, now.minusMinutes(5), ReservationState.RESERVED, testCustomer)
        )

        // when - looking for reservations older than 1 hour
        val expirationTime = now.minusHours(1)
        val expired = reservationRepository.findExpiredReservations(expirationTime)

        // then
        assertTrue(expired.isEmpty())
    }

    @Test
    fun `should update reservation state`() {
        // given
        val reservation = reservationRepository.save(
            Reservation(0, testScreening, LocalDateTime.now(), ReservationState.RESERVED, testCustomer)
        )
        val updated = reservation.copy(state = ReservationState.PAID)

        // when
        reservationRepository.update(updated)
        val found = reservationRepository.findById(reservation.id)

        // then
        assertTrue(found.isPresent)
        assertEquals(ReservationState.PAID, found.get().state)
    }

    @Test
    fun `should delete reservation`() {
        // given
        val reservation = reservationRepository.save(
            Reservation(0, testScreening, LocalDateTime.now(), ReservationState.RESERVED, testCustomer)
        )

        // when
        reservationRepository.deleteById(reservation.id)
        val found = reservationRepository.findById(reservation.id)

        // then
        assertTrue(found.isEmpty)
    }

    @Test
    fun `should count all reservations`() {
        // given
        reservationRepository.save(
            Reservation(0, testScreening, LocalDateTime.now(), ReservationState.RESERVED, testCustomer)
        )
        reservationRepository.save(
            Reservation(0, testScreening, LocalDateTime.now(), ReservationState.PAID, testCustomer)
        )

        // when
        val count = reservationRepository.count()

        // then
        assertEquals(2, count)
    }

    @Test
    fun `should find all reservations`() {
        // given
        reservationRepository.save(
            Reservation(0, testScreening, LocalDateTime.now(), ReservationState.RESERVED, testCustomer)
        )
        reservationRepository.save(
            Reservation(0, testScreening, LocalDateTime.now(), ReservationState.PAID, testCustomer)
        )
        reservationRepository.save(
            Reservation(0, testScreening, LocalDateTime.now(), ReservationState.CANCELED, testCustomer)
        )

        // when
        val reservations = reservationRepository.findAll().toList()

        // then
        assertEquals(3, reservations.size)
    }

    @Test
    fun `should handle multiple reservations for same screening`() {
        // given
        val customer2 = customerRepository.save(Customer(0, "user2@example.com", "User Two"))
        val customer3 = customerRepository.save(Customer(0, "user3@example.com", "User Three"))

        reservationRepository.save(
            Reservation(0, testScreening, LocalDateTime.now(), ReservationState.RESERVED, testCustomer)
        )
        reservationRepository.save(
            Reservation(0, testScreening, LocalDateTime.now(), ReservationState.RESERVED, customer2)
        )
        reservationRepository.save(
            Reservation(0, testScreening, LocalDateTime.now(), ReservationState.PAID, customer3)
        )

        // when
        val allReservations = reservationRepository.findAll().toList()

        // then
        assertEquals(3, allReservations.size)
        assertTrue(allReservations.all { it.screening.id == testScreening.id })
    }
}
