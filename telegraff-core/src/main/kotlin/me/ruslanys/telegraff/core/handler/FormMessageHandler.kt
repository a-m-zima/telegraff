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

import me.ruslanys.telegraff.core.data.FormStateStorage
import me.ruslanys.telegraff.core.data.FormStorage
import me.ruslanys.telegraff.core.dsl.Form
import me.ruslanys.telegraff.core.dsl.FormState
import me.ruslanys.telegraff.core.dto.TelegramMessage
import me.ruslanys.telegraff.core.exception.AbstractFormExceptionHandler
import me.ruslanys.telegraff.core.exception.ValidationException
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

class FormMessageHandler(
    private val formStorage: FormStorage,
    private val formStateStorage: FormStateStorage,
    private val exceptionHandlers: List<AbstractFormExceptionHandler<out Exception>> = emptyList(),
) : ConditionalMessageHandler {

    override fun isCanHandle(message: TelegramMessage): Boolean {
        return formStateStorage.existByMessage(message) || formStorage.existByMessage(message)
    }

    override fun handle(message: TelegramMessage) {
        val form = findForm(message)

        val state = formStateStorage.findByMessage(message)

        if (state == null) {
            val newState = formStateStorage.create(message, form)

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
        // TODO save answer
        state.answers[currentStep.key] = answer

        // next step
        // TODO set next step
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
        state.form.process(state, state.answers)
        formStateStorage.remove(state)
        // TODO remove answers
    }

    private fun findForm(message: TelegramMessage): Form {
        val state = formStateStorage.findByMessage(message)
        if (state != null) {
            return state.form
        }

        return formStorage.findByMessage(message)!!
    }

    private fun handleFatalException(message: TelegramMessage, state: FormState, exception: Exception) {
        logger.error(exception) { "Error during form processing" }
        formStateStorage.removeByMessage(message)

        handleException(message, state, exception)
    }

    private fun handleException(message: TelegramMessage, state: FormState, exception: Exception) {
        exceptionHandlers.firstOrNull { it.canHandle(exception) }
            ?.uncheckHandleException(message, state, exception)
            ?: logger.error(exception) { "Error on message=$message not handled" }
    }
}
