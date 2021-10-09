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
import me.ruslanys.telegraff.core.dto.TelegramUpdate
import me.ruslanys.telegraff.core.dto.TelegramUser
import me.ruslanys.telegraff.core.dto.request.TelegramChatActionRequest
import me.ruslanys.telegraff.core.dto.request.TelegramMessageSendRequest
import me.ruslanys.telegraff.core.dto.request.TelegramPhotoSendRequest
import me.ruslanys.telegraff.core.dto.request.TelegramVoiceSendRequest

interface TelegramApi {

    fun getMe(): TelegramUser

    fun getUpdates(offset: Long?, timeout: Int?): List<TelegramUpdate>

    fun getUpdates(offset: Long?): List<TelegramUpdate> {
        return getUpdates(offset, null)
    }

    fun getUpdates(): List<TelegramUpdate> {
        return getUpdates(null)
    }

    fun setWebhook(url: String): Boolean

    fun removeWebhook(): Boolean

    fun sendMessage(request: TelegramMessageSendRequest): TelegramMessage

    fun sendPhoto(request: TelegramPhotoSendRequest): TelegramMessage

    fun sendVoice(request: TelegramVoiceSendRequest): TelegramMessage

    fun sendChatAction(request: TelegramChatActionRequest): Boolean

}
