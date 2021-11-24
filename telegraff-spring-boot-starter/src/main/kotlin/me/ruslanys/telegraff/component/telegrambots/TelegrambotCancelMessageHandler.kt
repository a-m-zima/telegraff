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
package me.ruslanys.telegraff.component.telegrambots

import com.pengrad.telegrambot.TelegramBot
import org.telegram.telegrambots.meta.api.objects.Message
import com.pengrad.telegrambot.request.SendMessage
import me.ruslanys.telegraff.core.data.FormStateStorage

open class TelegrambotCancelMessageHandler(
    formStateStorage: FormStateStorage<Message, *>,
    private val telegramBot: TelegramBot,
) : AbstractTelegrambotCancelMessageHandler(formStateStorage, listOf("/cancel", "отмена")) {

    override fun cancelHandler(message: Message) {
        telegramBot.execute(SendMessage(message.chat().id(), "Хорошо, давай начнем сначала"))
    }
}
