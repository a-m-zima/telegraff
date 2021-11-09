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

class CounterForm(telegramApi: TelegramApi) : Form<TelegramMessage, InmemoryFormState>(listOf("/counter"), {

    step<Int>("counter") {

        question { _, state ->
            telegramApi.sendMessage(state.chatId, "До скольки нужно посчитать?")
        }

        validation {
            try {
                it.text!!.toInt()
            } catch (e: Exception) {
                throw ValidationException("Укажите число")
            }
        }
    }

    process { _, state ->
        val amount = state.answers["counter"] as Int

        for (i in 1..amount) {
            telegramApi.sendMessage(state.chatId, i.toString())
        }
    }
})
