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

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.GetUpdates
import me.ruslanys.telegraff.core.handler.CompositeMessageHandler
import mu.KotlinLogging
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import java.util.concurrent.TimeUnit
import javax.annotation.PreDestroy


private val logger = KotlinLogging.logger { }
private const val POLLING_TIMEOUT = 10

class TelegramPollingClient(
    private val telegramBot: TelegramBot,
    private val compositeMessageHandler: CompositeMessageHandler<Message>,
) : TelegramClient, ApplicationListener<ApplicationReadyEvent> {

    private val client = Client()

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        start()
    }

    override fun start() {
        logger.info("Telegram client: POLLING")
        client.start()
    }

    @PreDestroy
    override fun shutdown() {
        client.interrupt()
        client.join()
    }

    override fun onUpdate(update: Update) {
        logger.info("Got a new event: {}", update)
        update.message()?.let { compositeMessageHandler.handle(it) }
    }

    private inner class Client : Thread("PollingClient") {

        private var offset: Int = 0

        override fun run() {
            while (!isInterrupted) {
                try {
                    val updates = telegramBot.execute(GetUpdates().offset(offset).timeout(POLLING_TIMEOUT))
                        .updates()

                    if (updates.isEmpty()) {
                        continue
                    }

                    // publish updates
                    for (update in updates) {
                        onUpdate(update)
                    }

                    // --
                    offset = updates.last().updateId() + 1
                } catch (e: Exception) {
                    logger.error("Exception during polling messages", e)
                    TimeUnit.SECONDS.sleep(10)
                }
            }
        }

    }
}
