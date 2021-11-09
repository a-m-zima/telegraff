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
package me.ruslanys.telegraff.sample.forms

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import me.ruslanys.telegraff.component.telegrambot.TelegrambotForm
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

data class HttpbinResponse(
    val headers: Map<String, String>,
    val args: Map<String, String>,
    val origin: String,
    val url: String
)

@Component
class RestForm(telegramBot: TelegramBot) : TelegrambotForm(listOf("/rest"), {

    val rest: RestTemplate = RestTemplateBuilder()
        .rootUri("https://httpbin.org")
        .build()

    process {
        val response = rest.getForObject<HttpbinResponse>("/get")

        telegramBot.execute(SendMessage(it.chatId, "Your IP: ${response.origin}").parseMode(ParseMode.Markdown))
    }
})
