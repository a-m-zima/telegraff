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

import me.ruslanys.telegraff.core.dto.request.keyboard.TelegramMarkupReplyKeyboard
import me.ruslanys.telegraff.core.dto.request.keyboard.TelegramRemoveReplyKeyboard

class HtmlMessage(text: String, vararg replies: String, chatId: Long = 0) : TelegramMessageSendRequest(
        chatId,
        text,
        TelegramParseMode.HTML,
        if (replies.isNotEmpty()) TelegramMarkupReplyKeyboard(replies.asList()) else TelegramRemoveReplyKeyboard()
)