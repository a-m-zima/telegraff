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

import me.ruslanys.telegraff.core.data.inmemory.InmemoryFormState
import me.ruslanys.telegraff.core.dsl.Form
import me.ruslanys.telegraff.core.dto.TelegramMessage
import me.ruslanys.telegraff.core.exception.ValidationException
import me.ruslanys.telegraff.core.service.TelegramApi

enum class PaymentMethod {
    CARD, CASH
}

class TaxiForm(telegramApi: TelegramApi) : Form<TelegramMessage, InmemoryFormState>(listOf("/taxi", "такси"), {
    step<String>("locationFrom") {
        question { _, state ->
            telegramApi.sendMessage(state.chatId, "Откуда поедем?")
        }
    }

    step<String>("locationTo") {
        question { _, state ->
            telegramApi.sendMessage(state.chatId, "Куда поедем?")
        }
    }

    step<PaymentMethod>("paymentMethod") {
        question { _, state ->
            telegramApi.sendMessage(state.chatId, "Оплата картой или наличкой?")
        }

        validation {
            when (it.text!!.lowercase()) {
                "картой" -> PaymentMethod.CARD
                "наличкой" -> PaymentMethod.CASH
                else -> throw ValidationException("Пожалуйста, выбери один из вариантов")
            }
        }
    }

    process { _, state ->
        val from = state.answers["locationFrom"] as String
        val to = state.answers["locationTo"] as String
        val paymentMethod = state.answers["paymentMethod"] as PaymentMethod

        // Business logic

        telegramApi.sendMessage(
            state.chatId,
            """
            Заказ принят от пользователя #${state.fromId}.
            Поедем из $from в $to. Оплата $paymentMethod.
            """.trimIndent()
        )
    }
})
