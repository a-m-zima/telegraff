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
package me.ruslanys.telegraff.core.form

import me.ruslanys.telegraff.core.dsl.Form
import me.ruslanys.telegraff.core.exception.ValidationException
import me.ruslanys.telegraff.core.service.TelegramApi

enum class PaymentMethod {
    CARD, CASH
}

class TaxiForm(telegramApi: TelegramApi) : Form(listOf("/taxi", "такси"), {
    step<String>("locationFrom") {
        question {
            telegramApi.sendMessage(it.chat.id, "Откуда поедем?")
        }
    }

    step<String>("locationTo") {
        question {
            telegramApi.sendMessage(it.chat.id, "Куда поедем?")
        }
    }

    step<PaymentMethod>("paymentMethod") {
        question {
            telegramApi.sendMessage(it.chat.id, "Оплата картой или наличкой?")
        }

        validation {
            when (it.lowercase()) {
                "картой" -> PaymentMethod.CARD
                "наличкой" -> PaymentMethod.CASH
                else -> throw ValidationException("Пожалуйста, выбери один из вариантов")
            }
        }
    }

    process { state, answers ->
        val from = answers["locationFrom"] as String
        val to = answers["locationTo"] as String
        val paymentMethod = answers["paymentMethod"] as PaymentMethod

        // Business logic

        telegramApi.sendMessage(
            state.chat.id,
            """
            Заказ принят от пользователя #${state.chat.id}.
            Поедем из $from в $to. Оплата $paymentMethod.
            """.trimIndent()
        )
    }
})
