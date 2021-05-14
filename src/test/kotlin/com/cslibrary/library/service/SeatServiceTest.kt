package com.cslibrary.library.service

import com.cslibrary.library.data.User
import com.cslibrary.library.data.dto.response.SeatResponse
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.lang.reflect.Field
import java.lang.reflect.Method

@SpringBootTest
@RunWith(SpringRunner::class)
class SeatServiceTest {
    @Autowired
    private lateinit var seatService: SeatService

    @Before
    @After
    fun initSeat() {
        seatService.initSeats()
    }

    @Test
    fun is_seat_initiate_correctly() {
        val field: Field = SeatService::class.java.getDeclaredField("userSeatInfo").apply {
            isAccessible = true
        }
        val initiatedHashMap: HashMap<Int, User?> = field.get(seatService) as HashMap<Int, User?>

        assertThat(initiatedHashMap.size).isEqualTo(30)
    }

    @Test
    fun is_getAllSeats_returns_correctly() {
        val gotSeat: List<SeatResponse> = seatService.getAllSeats()

        assertThat(gotSeat.size).isEqualTo(30)
        for (eachSeat in gotSeat) {
            assertThat(eachSeat.isUsing).isEqualTo(false)
        }
    }

    @Test
    fun is_checkValue_returns_false_null() {
        val method: Method = SeatService::class.java.getDeclaredMethod("checkValue", User::class.java).apply {
            isAccessible = true
        }
        val isUserPresent: Boolean = method.invoke(seatService, null) as Boolean
        assertThat(isUserPresent).isEqualTo(false)
    }

    @Test
    fun is_checkValue_returns_true_null() {
        val method: Method = SeatService::class.java.getDeclaredMethod("checkValue", User::class.java).apply {
            isAccessible = true
        }
        val isUserPresent: Boolean = method.invoke(seatService, User()) as Boolean
        assertThat(isUserPresent).isEqualTo(true)
    }

    @Test
    fun is_reserveSeat_works_well() {
        val mockSeatNumber: Int = 10
        val mockUser: User = User()
        runCatching {
            seatService.reserveSeat(mockUser, mockSeatNumber)
        }.onFailure {
            println(it.stackTraceToString())
            fail("We don't have any data for seat, but reserving failed!")
        }.onSuccess {
            assertThat(it).isEqualTo(mockSeatNumber)
        }

        runCatching {
            seatService.reserveSeat(mockUser, mockSeatNumber)
        }.onSuccess {
            fail("Seat is duplicated and succeed")
        }.onFailure {
            assertThat(it is IllegalStateException).isEqualTo(true)
        }
    }
}