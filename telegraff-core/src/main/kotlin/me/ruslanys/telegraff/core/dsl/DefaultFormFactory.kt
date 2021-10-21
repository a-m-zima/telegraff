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
package me.ruslanys.telegraff.core.dsl

class DefaultFormFactory(forms: List<Form>) : FormFactory {

    private val formStorage: MutableMap<String, Form> = hashMapOf()

    init {
        forms.forEach { add(it) }
    }

    private fun add(form: Form) {
        for (rawCommand in form.commands) {
            val command = rawCommand.lowercase()
            val previousValue = formStorage.put(command, form)
            if (previousValue != null) {
                throw IllegalArgumentException("$command is already in use.")
            }
        }
    }

    override fun getStorage(): Map<String, Form> = formStorage
}
