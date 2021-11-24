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
package me.ruslanys.telegraff.component.telegrambots

import org.telegram.telegrambots.meta.api.objects.Message
import me.ruslanys.telegraff.core.data.FormStateStorage
import me.ruslanys.telegraff.core.dsl.Form
import java.util.concurrent.ConcurrentHashMap

open class TelegrambotFormStateStorage : FormStateStorage<Message, TelegrambotFormState> {

    protected val states: MutableMap<Pair<Long, Long>, TelegrambotFormState> = ConcurrentHashMap()

    override fun findByMessage(message: Message): TelegrambotFormState? =
        states[message.chat().id()!! to message.from().id()!!]

    override fun storeAnswer(state: TelegrambotFormState, formStepKey: String, answer: Any) {
        when (answer) {
            is Message -> state.answers[formStepKey] = answer.text()!!
            else -> state.answers[formStepKey] = answer
        }
    }

    override fun doNextStep(state: TelegrambotFormState) {
        state.currentStep = state.currentStep
            ?.next
            ?.invoke(state)
            ?.let { state.form.getStepByKey(it) }
    }

    override fun removeByMessage(message: Message) {
        states.remove(message.chat().id()!! to message.from().id()!!)
    }

    override fun create(message: Message, form: Form<Message, TelegrambotFormState>): TelegrambotFormState {
        val newState = TelegrambotFormState(message.chat().id()!!, message.from().id()!!, form)
        states[message.chat().id()!! to message.from().id()!!] = newState
        return newState
    }

    override fun remove(state: TelegrambotFormState) {
        states.remove(state.chatId to state.fromId)
    }

    override fun existByMessage(message: Message): Boolean {
        return states.containsKey(message.chat().id()!! to message.from().id()!!)
    }
}
