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
package me.ruslanys.telegraff.core.filter

import me.ruslanys.telegraff.core.dsl.Handler
import me.ruslanys.telegraff.core.dsl.HandlerState
import me.ruslanys.telegraff.core.dsl.HandlersFactory
import me.ruslanys.telegraff.core.dto.TelegramChat
import me.ruslanys.telegraff.core.dto.TelegramMessage
import me.ruslanys.telegraff.core.exception.ValidationException
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap

private val logger = KotlinLogging.logger { }

class HandlersFilter(handlersFactory: HandlersFactory) : TelegramFilter {

    private val handlers: Map<String, Handler> = handlersFactory.getHandlers()
    private val states: MutableMap<Long, HandlerState> = ConcurrentHashMap()


    override fun handleMessage(message: TelegramMessage, chain: TelegramFilterChain) {
        val handler = findHandler(message)
        if (handler == null) {
            chain.doFilter(message)
            return
        }

        val state = states[message.chat.id]

        if (state == null) {
            val newState = HandlerState(message.chat, handler)
            states[message.chat.id] = newState

            try {
                handleQuestion(newState)
            } catch (e: Exception) {
                handleFatalException(message, newState, e)
            }
        } else {
            try {
                handleContinuation(state, message)
            } catch (e: Exception) {
                handleFatalException(message, state, e)
            }
        }
    }

    fun clearState(chat: TelegramChat) {
        states.remove(chat.id)
    }

    private fun handleContinuation(state: HandlerState, message: TelegramMessage) {
        val currentStep = state.currentStep!!
        val text = message.text!!

        // validation
        val validation = currentStep.validation

        val answer = try {
            validation(text)
        } catch (e: ValidationException) {
            // stop handling
            return handleValidationException(message, state, e)
        }
        state.answers[currentStep.key] = answer

        // next step
        val nextStepKey = currentStep.next(state)
        val nextStep = nextStepKey?.let { state.handler.getStepByKey(nextStepKey) }
        state.currentStep = nextStep

        handleQuestion(state)
    }

    private fun handleQuestion(state: HandlerState) {
        val currentStep = state.currentStep

        if (currentStep != null) {
            currentStep.question(state)
        } else {
            handleFinalization(state)
        }
    }

    private fun handleFinalization(state: HandlerState) {
        clearState(state.chat)
        state.handler.process(state, state.answers)
    }

    private fun findHandler(message: TelegramMessage): Handler? {
        val state = states[message.chat.id]
        if (state != null) {
            return state.handler
        }

        val text = message.text?.lowercase() ?: return null
        for (entry in handlers) {
            if (text.startsWith(entry.key)) {
                return entry.value
            }
        }

        return null
    }

    private fun handleFatalException(message: TelegramMessage, newState: HandlerState, exception: Exception) {
        logger.error(exception) { "Error during handler processing" }
        clearState(message.chat)
        // TODO
        // MarkdownMessage("Что-то пошло не так :(")
    }

    private fun handleValidationException(
        message: TelegramMessage,
        newState: HandlerState,
        exception: Exception
    ) {
        // TODO
        // TelegramMessageSendRequest(0, e.message, TelegramParseMode.MARKDOWN, question.replyKeyboard)
        // отправка question.replyKeyboard давала возможность в случае ошибки сохранить кнопки которые задавал вопрос
    }
}
