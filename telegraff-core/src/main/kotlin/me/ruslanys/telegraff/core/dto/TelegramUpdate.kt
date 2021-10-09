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
package me.ruslanys.telegraff.core.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TelegramUpdate(

        @JsonProperty("update_id")
        val id: Long,

        @JsonProperty("message")
        val message: TelegramMessage?,

        @JsonProperty("edited_message")
        val editedMessage: TelegramMessage?,

        @JsonProperty("channel_post")
        val channelPost: TelegramMessage?,

        @JsonProperty("edited_channel_post")
        val editedChannelPost: TelegramMessage?

)