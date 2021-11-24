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

import me.ruslanys.telegraff.core.data.FormState
import me.ruslanys.telegraff.core.data.FormStateStorage
import me.ruslanys.telegraff.core.data.FormStorage
import me.ruslanys.telegraff.core.dsl.Form
import me.ruslanys.telegraff.core.exception.AbstractFormExceptionHandler
import me.ruslanys.telegraff.core.exception.ValidationException
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

class FormMessageHandler<M : Any, ST : FormState<M, ST>>(
    private val formStorage: FormStorage<M, ST>,
    private val formStateStorage: FormStateStorage<M, ST>,
    private val exceptionHandlers: List<AbstractFormExceptionHandler<M, ST, out Exception>> = emptyList(),
) : ConditionalMessageHandler<M> {

    override fun isCanHandle(message: M): Boolean {
        return formStateStorage.existByMessage(message) || formStorage.existByMessage(message)
    }

    override fun handle(message: M) {
        val form = findForm(message)

        val state = formStateStorage.findByMessage(message)

        if (state == null) {
            val newState = formStateStorage.create(message, form)

            try {
                handleQuestion(message, newState)
            } catch (e: Exception) {
                handleFatalException(message, newState, e)
            }
        } else {
            try {
                handleContinuation(message, state)
            } catch (e: Exception) {
                handleFatalException(message, state, e)
            }
        }
    }

    private fun handleContinuation(message: M, state: ST) {
        val currentStep = state.currentStep!!

        // validation
        val validation = currentStep.validation

        val answer = try {
            validation(message)
        } catch (e: ValidationException) {
            // stop handling
            return handleException(message, state, e)
        }

        formStateStorage.storeAnswer(state, currentStep.key, answer)

        // next step
        formStateStorage.doNextStep(state)

        handleQuestion(message, state)
    }

    private fun handleQuestion(message: M, state: ST) {
        val currentStep = state.currentStep

        if (currentStep != null) {
            currentStep.question(message, state)
        } else {
            handleFinalization(message, state)
        }
    }

    private fun handleFinalization(message: M, state: ST) {
        state.form.process(message, state)
        formStateStorage.remove(state)
    }

    private fun findForm(message: M): Form<M, ST> {
        val state = formStateStorage.findByMessage(message)
        if (state != null) {
            return state.form
        }

        return formStorage.findByMessage(message)!!
    }

    private fun handleFatalException(message: M, state: ST, exception: Exception) {
        logger.error(exception) { "Error during form processing" }
        formStateStorage.removeByMessage(message)

        handleException(message, state, exception)
    }

    private fun handleException(message: M, state: ST, exception: Exception) {
        exceptionHandlers.firstOrNull { it.canHandle(exception) }
            ?.uncheckHandleException(message, state, exception)
            ?: logger.error(exception) { "Error on message=$message not handled" }
    }
}
