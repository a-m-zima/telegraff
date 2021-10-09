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
package me.ruslanys.telegraff.core.component

import me.ruslanys.telegraff.core.dto.TelegramMessage
import me.ruslanys.telegraff.core.dto.TelegramResponse
import me.ruslanys.telegraff.core.dto.TelegramUpdate
import me.ruslanys.telegraff.core.dto.TelegramUser
import me.ruslanys.telegraff.core.dto.request.*
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.core.ParameterizedTypeReference
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap

class DefaultTelegramApi(telegramAccessKey: String, restTemplateBuilder: RestTemplateBuilder) : TelegramApi {

    private val restTemplate = restTemplateBuilder
            .rootUri("https://api.telegram.org/bot$telegramAccessKey")
            .build()

    override fun getMe(): TelegramUser {
        val response = restTemplate.exchange(
                "/getMe",
                HttpMethod.GET,
                null,
                object : ParameterizedTypeReference<TelegramResponse<TelegramUser>>() {}
        )

        return response.body!!.result!!
    }

    override fun getUpdates(offset: Long?, timeout: Int?): List<TelegramUpdate> {
        val params = hashMapOf<String, Any>()
        offset?.let { params["offset"] = it }
        timeout?.let { params["timeout"] = it }

        val response = restTemplate.exchange(
                "/getUpdates",
                HttpMethod.POST,
                HttpEntity(params),
                object : ParameterizedTypeReference<TelegramResponse<List<TelegramUpdate>>>() {}
        )

        return response.body!!.result!!
    }

    override fun setWebhook(url: String): Boolean {
        val params = hashMapOf("url" to url)

        val response = restTemplate.exchange(
                "/setWebhook",
                HttpMethod.POST,
                HttpEntity(params),
                object : ParameterizedTypeReference<TelegramResponse<Boolean>>() {}
        )

        return response.body!!.result!!
    }

    override fun removeWebhook(): Boolean {
        return setWebhook("")
    }

    override fun sendMessage(request: TelegramMessageSendRequest): TelegramMessage {
        val response = restTemplate.exchange(
                "/sendMessage",
                HttpMethod.POST,
                HttpEntity(request),
                object : ParameterizedTypeReference<TelegramResponse<TelegramMessage>>() {}
        )

        return response.body!!.result!!
    }

    override fun sendPhoto(request: TelegramPhotoSendRequest): TelegramMessage {
        val formData = createFormData(request).apply {
            add("photo", object : ByteArrayResource(request.photo) {
                override fun getFilename(): String {
                    return "picture.png"
                }
            })
        }

        // --
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA

        val entity = restTemplate.exchange(
                "/sendPhoto",
                HttpMethod.POST,
                HttpEntity(formData, headers),
                object : ParameterizedTypeReference<TelegramResponse<TelegramMessage>>() {}
        )

        return entity.body!!.result!!
    }

    override fun sendVoice(request: TelegramVoiceSendRequest): TelegramMessage {
        val formData = createFormData(request).apply {
            add("voice", object : ByteArrayResource(request.voice) {
                override fun getFilename(): String {
                    return "voice.mp3"
                }
            })
        }

        // --
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA

        val entity = restTemplate.exchange(
                "/sendVoice",
                HttpMethod.POST,
                HttpEntity(formData, headers),
                object : ParameterizedTypeReference<TelegramResponse<TelegramMessage>>() {}
        )

        return entity.body!!.result!!
    }

    override fun sendChatAction(request: TelegramChatActionRequest): Boolean {
        val response = restTemplate.exchange(
                "/sendChatAction",
                HttpMethod.POST,
                HttpEntity(request),
                object : ParameterizedTypeReference<TelegramResponse<Boolean>>() {}
        )
        return response.body!!.result!!
    }

    private fun createFormData(request: TelegramMediaSendRequest): LinkedMultiValueMap<String, Any> =
            LinkedMultiValueMap<String, Any>().apply {
                add("chat_id", request.chatId)
                add("reply_markup", request.replyKeyboard)
                add("disable_notification", request.disableNotification)
                request.caption?.let { add("caption", request.caption) }
                request.parseMode?.let { add("parse_mode", request.parseMode.name) }
            }

}