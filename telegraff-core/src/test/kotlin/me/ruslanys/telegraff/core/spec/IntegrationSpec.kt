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
package me.ruslanys.telegraff.core.spec

import io.kotest.core.spec.style.FreeSpec
import me.ruslanys.telegraff.core.data.InmemoryFormStateStorage
import me.ruslanys.telegraff.core.data.InmemoryFormStorage
import me.ruslanys.telegraff.core.dsl.Form
import me.ruslanys.telegraff.core.dto.TelegramMessage
import me.ruslanys.telegraff.core.handler.DefaultCompositeMessageHandler
import me.ruslanys.telegraff.core.handler.FormMessageHandler

abstract class IntegrationSpec(body: IntegrationSpec.() -> Unit) : FreeSpec() {
    lateinit var forms: List<Form>
    lateinit var formStorage: InmemoryFormStorage

    lateinit var formHandler: FormMessageHandler

    lateinit var compositeHandler: DefaultCompositeMessageHandler

    init {
        body()
        if (::forms.isInitialized.not()) {
            forms = listOf()
        }
        if (::formStorage.isInitialized.not()) {
            formStorage = InmemoryFormStorage(forms)
        }
        if (::formHandler.isInitialized.not()) {
            formHandler = FormMessageHandler(
                formStorage,
                InmemoryFormStateStorage(),
            )
        }
        if (::compositeHandler.isInitialized.not()) {
            compositeHandler = DefaultCompositeMessageHandler(listOf(formHandler))
        }
    }

    fun handleMessage(message: TelegramMessage) = compositeHandler.handle(message)
}
