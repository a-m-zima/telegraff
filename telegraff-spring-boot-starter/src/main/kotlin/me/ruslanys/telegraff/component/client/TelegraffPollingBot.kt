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
package me.ruslanys.telegraff.component.client

import me.ruslanys.telegraff.autoconfigure.property.TelegraffProperties
import me.ruslanys.telegraff.core.handler.CompositeMessageHandler
import mu.KotlinLogging
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update


private val logger = KotlinLogging.logger { }

class TelegraffPollingBot(
    private val telegramProperties: TelegraffProperties,
    private val compositeMessageHandler: CompositeMessageHandler<Message>,
) : TelegramLongPollingBot() {

    override fun getBotToken(): String = telegramProperties.accessKey

    override fun getBotUsername(): String = telegramProperties.botUsername

    override fun onUpdateReceived(update: Update?) {
        logger.info { "Got a new update: $update" }
        when {
            null != update?.message -> {
                compositeMessageHandler.handle(update.message)
            }
            null != update?.callbackQuery?.message -> {
                compositeMessageHandler.handle(update.callbackQuery.message)
            }
            else -> {
                logger.info { "Update not handled" }
            }
        }
    }
}