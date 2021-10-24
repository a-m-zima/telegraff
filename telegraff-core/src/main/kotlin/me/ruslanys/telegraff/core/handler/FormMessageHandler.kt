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

import me.ruslanys.telegraff.core.dsl.Form
import me.ruslanys.telegraff.core.dsl.FormFactory
import me.ruslanys.telegraff.core.dsl.FormState
import me.ruslanys.telegraff.core.dto.TelegramChat
import me.ruslanys.telegraff.core.dto.TelegramMessage
import me.ruslanys.telegraff.core.exception.AbstractFormExceptionHandler
import me.ruslanys.telegraff.core.exception.ValidationException
import mu.KotlinLogging
import java.util.concurrent.ConcurrentHashMap

private val logger = KotlinLogging.logger { }

class FormMessageHandler(
    formFactory: FormFactory,
    private val exceptionHandlers: List<AbstractFormExceptionHandler<out Exception>> = emptyList(),
) : ConditionalMessageHandler {

    private val forms: Map<String, Form> = formFactory.getStorage()
    private val states: MutableMap<Long, FormState> = ConcurrentHashMap()

    override fun isCanHandle(message: TelegramMessage): Boolean {
        return null != findForm(message)
    }

    override fun handle(message: TelegramMessage) {
        val form = findForm(message)!!

        val state = states[message.chat.id]

        if (state == null) {
            val newState = FormState(message.chat, form)
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

    private fun handleContinuation(state: FormState, message: TelegramMessage) {
        val currentStep = state.currentStep!!
        val text = message.text!!

        // validation
        val validation = currentStep.validation

        val answer = try {
            validation(text)
        } catch (e: ValidationException) {
            // stop handling
            return handleException(message, state, e)
        }
        state.answers[currentStep.key] = answer

        // next step
        val nextStepKey = currentStep.next(state)
        val nextStep = nextStepKey?.let { state.form.getStepByKey(nextStepKey) }
        state.currentStep = nextStep

        handleQuestion(state)
    }

    private fun handleQuestion(state: FormState) {
        val currentStep = state.currentStep

        if (currentStep != null) {
            currentStep.question(state)
        } else {
            handleFinalization(state)
        }
    }

    private fun handleFinalization(state: FormState) {
        clearState(state.chat)
        state.form.process(state, state.answers)
    }

    private fun findForm(message: TelegramMessage): Form? {
        val state = states[message.chat.id]
        if (state != null) {
            return state.form
        }

        val text = message.text?.lowercase() ?: return null
        for (entry in forms) {
            if (text.startsWith(entry.key)) {
                return entry.value
            }
        }

        return null
    }

    private fun handleFatalException(message: TelegramMessage, state: FormState, exception: Exception) {
        logger.error(exception) { "Error during form processing" }
        clearState(message.chat)

        handleException(message, state, exception)
    }

    private fun handleException(message: TelegramMessage, state: FormState, exception: Exception) {
        exceptionHandlers.firstOrNull { it.canHandle(exception) }
            ?.uncheckHandleException(message, state, exception)
            ?: logger.error(exception) { "Error on message=$message not handled" }
    }
}
