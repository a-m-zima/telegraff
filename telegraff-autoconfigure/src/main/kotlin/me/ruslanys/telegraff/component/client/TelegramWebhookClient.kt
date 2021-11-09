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

import com.pengrad.telegrambot.BotUtils
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Message
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.DeleteWebhook
import com.pengrad.telegrambot.request.SetWebhook
import me.ruslanys.telegraff.core.handler.CompositeMessageHandler
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy


private val logger = KotlinLogging.logger { }

@RestController
class TelegramWebhookClient(
    private val telegramBot: TelegramBot,
    private val compositeMessageHandler: CompositeMessageHandler<Message>,
    private val webhookUrl: String
) : TelegramClient {

    @PostConstruct
    override fun start() {
        logger.info("Telegram client: WEBHOOK")
        logger.info("Setting webhook at: {}", webhookUrl)
        telegramBot.execute(SetWebhook().url(webhookUrl))
    }

    @PreDestroy
    override fun shutdown() {
        logger.info("Removing webhook")
        telegramBot.execute(DeleteWebhook())
    }

    override fun onUpdate(update: Update) {
        logger.info("Got a new event: {}", update)
        update.message()?.let { compositeMessageHandler.handle(it) }
    }

    @RequestMapping("#{telegramProperties.getWebhookEndpointUrl()}")
    fun update(@RequestBody body: String): ResponseEntity<String> {
        val update = BotUtils.parseUpdate(body)
        onUpdate(update)
        return ResponseEntity.ok("ok")
    }
}
