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
package me.ruslanys.telegraff.core.client

import me.ruslanys.telegraff.core.component.TelegramApi
import me.ruslanys.telegraff.core.dto.TelegramUpdate
import me.ruslanys.telegraff.core.event.TelegramUpdateEvent
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@RestController
class TelegramWebhookClient(
        private val telegramApi: TelegramApi,
        private val publisher: ApplicationEventPublisher,
        private val webhookUrl: String
) : TelegramClient {

    @PostConstruct
    override fun start() {
        log.info("Telegram client: WEBHOOK")
        log.info("Setting webhook at: {}", webhookUrl)
        telegramApi.setWebhook(webhookUrl)
    }

    @PreDestroy
    override fun shutdown() {
        log.info("Removing webhook")
        telegramApi.removeWebhook()
    }

    override fun onUpdate(update: TelegramUpdate) {
        log.info("Got a new event: {}", update)
        publisher.publishEvent(TelegramUpdateEvent(this, update))
    }

    @RequestMapping("#{telegramProperties.getWebhookEndpointUrl()}")
    fun update(@RequestBody update: TelegramUpdate): ResponseEntity<String> {
        onUpdate(update)
        return ResponseEntity.ok("ok")
    }

    companion object {
        private val log = LoggerFactory.getLogger(TelegramWebhookClient::class.java)
    }

}