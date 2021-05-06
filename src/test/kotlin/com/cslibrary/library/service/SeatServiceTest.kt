package com.cslibrary.library.service

import com.cslibrary.library.data.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.lang.reflect.Field

@SpringBootTest
@RunWith(SpringRunner::class)
class SeatServiceTest {
    @Autowired
    private lateinit var seatService: SeatService

    @Test
    fun is_seat_initiate_correctly() {
        val field: Field = SeatService::class.java.getDeclaredField("userSeatInfo").apply {
            isAccessible = true
        }
        val initiatedHashMap: HashMap<Int, User?> = field.get(seatService) as HashMap<Int, User?>

        assertThat(initiatedHashMap.size).isEqualTo(30)
    }
}