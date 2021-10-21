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

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.*
import me.ruslanys.telegraff.core.dsl.Form
import me.ruslanys.telegraff.core.dsl.FormState
import me.ruslanys.telegraff.core.dsl.Step
import me.ruslanys.telegraff.core.dto.TelegramChat
import me.ruslanys.telegraff.core.exception.ValidationException
import me.ruslanys.telegraff.core.form.PaymentMethod
import me.ruslanys.telegraff.core.form.TaxiForm
import me.ruslanys.telegraff.core.form.TelegramApi

class TaxiFormTests : FreeSpec({

    val telegramApi: TelegramApi = mockk()
    lateinit var tgMessage: CapturingSlot<String>

    beforeEach {
        tgMessage = slot()
        justRun { telegramApi.sendMessage(capture(tgMessage)) }
    }
    afterEach {
        clearMocks(telegramApi)
    }

    val form = TaxiForm(telegramApi)
    val state = FormState(TelegramChat(-1L, "PRIVATE"), form)

    "step test" {
        form.getStepByKey("locationFrom").shouldNotBeNull()
        form.getStepByKey("locationTo").shouldNotBeNull()
        form.getStepByKey("paymentMethod").shouldNotBeNull()
        form.getStepByKey("123").shouldBeNull()
    }


    "location from question" {
        val step: Step<String> = form.getStep("locationFrom")

        step.question(state)

        tgMessage.captured shouldBe "Откуда поедем?"
    }

    "payment method validation with exception" {
        shouldThrow<ValidationException> {
            val step = form.getStep<String>("paymentMethod")

            step.validation("Payment method")
        }
    }

    "payment method validation" {
        val step = form.getStep<Any>("paymentMethod")

        val answer = step.validation("картой")
        answer.shouldNotBeNull()
        answer.shouldBeInstanceOf<Enum<*>>()
    }

    "process" {
        form.process(
            state, mapOf(
                "locationFrom" to "Дом",
                "locationTo" to "Работа",
                "paymentMethod" to PaymentMethod.CARD
            )
        )

        tgMessage.captured shouldBe """
            Заказ принят от пользователя #-1.
            Поедем из Дом в Работа. Оплата CARD.
        """.trimIndent()

    }

})

@Suppress("UNCHECKED_CAST")
fun <T : Any> Form.getStep(key: String): Step<T> = getStepByKey(key) as Step<T>
