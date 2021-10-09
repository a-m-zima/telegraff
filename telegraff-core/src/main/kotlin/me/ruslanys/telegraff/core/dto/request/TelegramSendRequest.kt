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
package me.ruslanys.telegraff.core.dto.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import me.ruslanys.telegraff.core.dto.request.keyboard.TelegramReplyKeyboard

@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class TelegramSendRequest(

        @get:JsonProperty("chat_id")
        var chatId: Long,

        @get:JsonProperty("reply_markup")
        val replyKeyboard: TelegramReplyKeyboard,

        @get:JsonProperty("disable_notification")
        val disableNotification: Boolean = false

) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TelegramSendRequest) return false

        if (chatId != other.chatId) return false
        if (replyKeyboard != other.replyKeyboard) return false
        if (disableNotification != other.disableNotification) return false

        return true
    }

    override fun hashCode(): Int {
        var result = chatId.hashCode()
        result = 31 * result + replyKeyboard.hashCode()
        result = 31 * result + disableNotification.hashCode()
        return result
    }

}