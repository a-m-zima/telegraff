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
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.justRun
import io.mockk.mockk
import me.ruslanys.telegraff.core.data.inmemory.InmemoryFormStorage
import me.ruslanys.telegraff.core.dto.TelegramChat
import me.ruslanys.telegraff.core.dto.TelegramMessageImpl
import me.ruslanys.telegraff.core.dto.TelegramUser
import me.ruslanys.telegraff.core.form.CounterForm
import me.ruslanys.telegraff.core.form.TaxiForm
import me.ruslanys.telegraff.core.form.WelcomeForm
import me.ruslanys.telegraff.core.handler.*
import me.ruslanys.telegraff.core.service.OpenInmemoryFormStateStorage
import me.ruslanys.telegraff.core.service.TelegramApi
import me.ruslanys.telegraff.core.spec.IntegrationSpec

class IntegrationTest : IntegrationSpec({
    val telegramApi = mockk<TelegramApi>()
    val formStateStorage = OpenInmemoryFormStateStorage()

    forms = listOf(
        CounterForm(telegramApi),
        TaxiForm(telegramApi),
        WelcomeForm(telegramApi),
    )

    formStorage = InmemoryFormStorage(forms)
    formHandler = FormMessageHandler(
        formStorage,
        formStateStorage,
        listOf(ValidationExceptionHandler(telegramApi))
    )
    val cancelHandler = CancelMessageHandler(formStateStorage, telegramApi)
    compositeHandler = DefaultCompositeMessageHandler(
        listOf(cancelHandler, formHandler),
        FinalMessageHandler(telegramApi)
    )

    lateinit var tgMessage: MutableList<String>

    beforeEach {
        tgMessage = mutableListOf()
        justRun { telegramApi.sendMessage(123, capture(tgMessage)) }
    }
    afterEach {
        clearMocks(telegramApi)
    }

    "Позитивный сценарий заказа такси" - {
        "Инициализация" {
            handleMessage(message("такси"))

            tgMessage shouldContainExactly listOf("Откуда поедем?")
            formStateStorage.getStorage() shouldHaveSize 1
            formStateStorage.getStorage() shouldContainKey (123L to 111)
            formStateStorage.getStorage()[123L to 111] should {
                it?.currentStepKey shouldBe "locationFrom"
            }
        }
        "Отменяем" {
            handleMessage(message("отмена"))

            tgMessage shouldContainExactly listOf("Хорошо, давай начнем сначала")
            formStateStorage.getStorage() shouldHaveSize 0
        }
        "Проверяем что нельзя продолжить" {
            handleMessage(message("Дом"))

            tgMessage shouldContainExactly listOf("Извини, я тебя не понимаю")
            formStateStorage.getStorage() shouldHaveSize 0
        }
        "Повторная инициализация" {
            handleMessage(message("такси"))

            tgMessage shouldContainExactly listOf("Откуда поедем?")
            formStateStorage.getStorage() shouldHaveSize 1
            formStateStorage.getStorage() shouldContainKey (123L to 111)
            formStateStorage.getStorage()[123L to 111] should {
                it?.currentStepKey shouldBe "locationFrom"
            }
        }
        "Первый вопрос" {
            handleMessage(message("Дом"))

            tgMessage shouldContainExactly listOf("Куда поедем?")
            formStateStorage.getStorage() shouldHaveSize 1
            formStateStorage.getStorage() shouldContainKey (123L to 111)
            formStateStorage.getStorage()[123L to 111] should {
                it?.currentStepKey shouldBe "locationTo"
                it?.answers?.get("locationFrom") shouldBe "Дом"
            }
        }
        "Второй вопрос" {
            handleMessage(message("Работа"))

            tgMessage shouldContainExactly listOf("Оплата картой или наличкой?")
            formStateStorage.getStorage() shouldHaveSize 1
            formStateStorage.getStorage() shouldContainKey (123L to 111)
            formStateStorage.getStorage()[123L to 111] should {
                it?.currentStepKey shouldBe "paymentMethod"
                it?.answers?.get("locationTo") shouldBe "Работа"
            }
        }
        "Даём неверный ответ" {
            handleMessage(message("Какая то дич"))

            tgMessage shouldContainExactly listOf("Пожалуйста, выбери один из вариантов")
            formStateStorage.getStorage() shouldHaveSize 1
            formStateStorage.getStorage() shouldContainKey (123L to 111)
            formStateStorage.getStorage()[123L to 111] should {
                it?.currentStepKey shouldBe "paymentMethod"
                it?.answers?.get("locationTo") shouldBe "Работа"
            }
        }
        "Последний вопрос и проверка результата" {
            handleMessage(message("картой"))

            tgMessage shouldContainExactly listOf(
                """
                Заказ принят от пользователя #123.
                Поедем из Дом в Работа. Оплата CARD.
                """.trimIndent()
            )
            formStateStorage.getStorage() shouldHaveSize 0
        }
    }
})

private fun message(text: String) = TelegramMessageImpl(
    TelegramChat(123, "PRIVATE"),
    TelegramUser(111),
    text
)
