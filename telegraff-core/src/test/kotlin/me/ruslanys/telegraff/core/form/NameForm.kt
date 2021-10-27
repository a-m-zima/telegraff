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
package me.ruslanys.telegraff.core.form

import me.ruslanys.telegraff.core.data.inmemory.InmemoryFormState
import me.ruslanys.telegraff.core.dsl.Form
import me.ruslanys.telegraff.core.exception.ValidationException

object NameForm : Form<InmemoryFormState>(listOf("/name", "имя"), {

    step<Int>("length")
    {
        question {
            // MarkdownMessage("Какой длины?")
        }

        validation {
            try {
                Math.abs(it.toInt())
            } catch (e: Exception) {
                throw ValidationException("Укажи число")
            }
        }
    }


    process {
        // val nameGenerator = getBean<NameGenerator>()

        val length = it.answers["length"] as Int
        // val name = nameGenerator.generateName(length)

        // MarkdownMessage("Сгенерированное имя: $name")
    }
})
