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

import com.fasterxml.jackson.annotation.JsonProperty
import me.ruslanys.telegraff.core.dto.request.keyboard.TelegramRemoveReplyKeyboard
import me.ruslanys.telegraff.core.dto.request.keyboard.TelegramReplyKeyboard

open class TelegramMessageSendRequest(

        chatId: Long,

        @get:JsonProperty("text")
        val text: String,

        @get:JsonProperty("parse_mode")
        val parseMode: TelegramParseMode,

        replyMarkup: TelegramReplyKeyboard = TelegramRemoveReplyKeyboard(),

        disableNotification: Boolean = false,

        @get:JsonProperty("disable_web_page_preview")
        val disableWebPagePreview: Boolean = false

) : TelegramSendRequest(chatId, replyMarkup, disableNotification) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TelegramMessageSendRequest) return false
        if (!super.equals(other)) return false

        if (text != other.text) return false
        if (parseMode != other.parseMode) return false
        if (disableWebPagePreview != other.disableWebPagePreview) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + parseMode.hashCode()
        result = 31 * result + disableWebPagePreview.hashCode()
        return result
    }

}