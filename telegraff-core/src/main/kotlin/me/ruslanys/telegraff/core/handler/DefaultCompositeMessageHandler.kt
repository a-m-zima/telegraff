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
package me.ruslanys.telegraff.core.handler

import me.ruslanys.telegraff.core.dto.TelegramMessage
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

class DefaultCompositeMessageHandler(
    private val handlers: List<ConditionalMessageHandler>,
    private val finalizer: MessageHandler,
) : CompositeMessageHandler {

    override fun handle(message: TelegramMessage) {
        val handler: MessageHandler = handlers.firstOrNull { it.isCanHandle(message) } ?: finalizer
        logger.debug { "message $message will be handled with ${handler::class.simpleName}" }
        handler.handle(message)
    }
}