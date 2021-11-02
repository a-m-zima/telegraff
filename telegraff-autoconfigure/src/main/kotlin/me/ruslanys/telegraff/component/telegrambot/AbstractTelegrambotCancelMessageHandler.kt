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
package me.ruslanys.telegraff.component.telegrambot

import com.pengrad.telegrambot.model.Message
import me.ruslanys.telegraff.core.data.FormStateStorage
import me.ruslanys.telegraff.core.handler.ConditionalMessageHandler
import mu.KotlinLogging


private val logger = KotlinLogging.logger { }

abstract class AbstractTelegrambotCancelMessageHandler(
    private val formStateStorage: FormStateStorage<Message, *>,
    private val cancelCommands: List<String>,
) : ConditionalMessageHandler<Message> {

    override fun isCanHandle(message: Message): Boolean {
        logger.debug { "Check message=$message for cancel state handler" }
        val messageText = message.text() ?: return false

        return cancelCommands.any { messageText.startsWith(it, ignoreCase = true) }
    }

    override fun handle(message: Message) {
        logger.debug { "Message=$message cancel state" }
        formStateStorage.removeByMessage(message)
        cancelHandler(message)
    }

    abstract fun cancelHandler(message: Message)
}
