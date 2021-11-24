/**
 * Copyright © 2018-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.ruslanys.telegraff.autoconfigure.client

import com.ninjasquad.springmockk.MockkBean
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import io.mockk.CapturingSlot
import io.mockk.justRun
import io.mockk.slot
import me.ruslanys.telegraff.component.client.TelegramWebhookClient
import me.ruslanys.telegraff.core.handler.CompositeMessageHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TelegramWebhookClient::class)
class TelegramWebhookClientTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockkBean(relaxed = true)
    private lateinit var telegramBot: TelegramBot

    @MockkBean
    private lateinit var compositeMessageHandler: CompositeMessageHandler<Message>

    private lateinit var capture: CapturingSlot<Message>

    @BeforeEach
    internal fun setUp() {
        capture = slot()
        justRun { compositeMessageHandler.handle(capture(capture)) }
    }

    @Test
    fun `Update with valid token`() {
        mvc.perform(
            post("/telegram")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getUpdateJson())
        )

            .andExpect(status().isOk)
            .andExpect(content().string("ok"))


        assertThat(capture.captured.messageId()).isEqualTo(5363)
        assertThat(capture.captured.from().username()).isEqualTo("ruslanys")
        assertThat(capture.captured.text()).isEqualTo("Как дела?")
    }

    private fun getUpdateJson(): String = """
        {
          "update_id": 366468163,
          "message": {
            "message_id": 5363,
            "from": {
              "id": 73168307,
              "is_bot": false,
              "first_name": "Ruslan",
              "last_name": "Molchanov",
              "username": "ruslanys",
              "language_code": "en-BY"
            },
            "chat": {
              "id": 73168307,
              "first_name": "Ruslan",
              "last_name": "Molchanov",
              "username": "ruslanys",
              "type": "private"
            },
            "date": 1538990403,
            "text": "Как дела?"
          }
        }
        """.trimIndent()


    @Configuration
    open class Config {

        @Bean(name = ["telegramProperties"])
        open fun telegramProperties() = Properties()

        @Bean
        open fun telegramWebhookClient(
            telegramBot: TelegramBot,
            compositeMessageHandler: CompositeMessageHandler<Message>,
        ): TelegramWebhookClient {
            return TelegramWebhookClient(telegramBot, compositeMessageHandler, "http://localhost/telegram")
        }

        class Properties {
            fun getWebhookEndpointUrl() = "/telegram"
        }
    }
}
