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

import me.ruslanys.telegraff.core.data.FormState
import me.ruslanys.telegraff.core.dsl.Form
import me.ruslanys.telegraff.core.dsl.Step
import org.telegram.telegrambots.meta.api.objects.Message
import java.util.concurrent.ConcurrentHashMap

open class TelegrambotFormState(
    val chatId: Long,
    val fromId: Long,
    final override val form: Form<Message, TelegrambotFormState>,
) : FormState<Message, TelegrambotFormState> {

    override val currentStepKey: String? get() = currentStep?.key

    override var currentStep: Step<Message, *, TelegrambotFormState>? = form.getInitialStep()

    val answers: MutableMap<String, Any> = ConcurrentHashMap()
}
