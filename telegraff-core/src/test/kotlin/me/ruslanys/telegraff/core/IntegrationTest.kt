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
package me.ruslanys.telegraff.core

import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.clearMocks
import io.mockk.justRun
import io.mockk.mockk
import me.ruslanys.telegraff.core.dto.TelegramChat
import me.ruslanys.telegraff.core.dto.TelegramMessage
import me.ruslanys.telegraff.core.form.TaxiForm
import me.ruslanys.telegraff.core.form.TelegramApi
import me.ruslanys.telegraff.core.form.staticForms

class IntegrationTest : IntegrationSpec({
    val telegramApi = mockk<TelegramApi>()
    forms = staticForms + TaxiForm(telegramApi)
    lateinit var tgMessage: MutableList<String>

    beforeEach {
        tgMessage = mutableListOf()
        justRun { telegramApi.sendMessage(capture(tgMessage)) }
    }
    afterEach {
        clearMocks(telegramApi)
    }

    "Позитивный сценарий заказа такси" - {
        "Инициализация" {
            handleMessage(message("такси"))

            tgMessage shouldContainExactly listOf("Откуда поедем?")
        }
        "Первый вопрос" {
            handleMessage(message("Дом"))

            tgMessage shouldContainExactly listOf("Куда поедем?")
        }
        "Второй вопрос" {
            handleMessage(message("Работа"))

            tgMessage shouldContainExactly listOf("Оплата картой или наличкой?")
        }
        "Последний вопрос и проверка результата" {
            handleMessage(message("картой"))

            tgMessage shouldContainExactly listOf(
                """
                Заказ принят от пользователя #123.
                Поедем из Дом в Работа. Оплата CARD.
                """.trimIndent()
            )
        }
    }
})

private fun message(text: String) = TelegramMessage(TelegramChat(123, "PRIVATE"), text)
