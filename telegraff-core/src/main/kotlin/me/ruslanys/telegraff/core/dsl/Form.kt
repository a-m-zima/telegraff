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

import me.ruslanys.telegraff.core.exception.FormException

open class Form(val commands: List<String>, init: FormDsl.() -> Unit) {

    private val steps: Map<String, Step<*>>
    private val initialStepKey: String?
    val process: ProcessBlock

    init {
        val dsl = FormDsl().apply(init)
        steps = dsl.buildSteps()
        initialStepKey = dsl.stepDslList.firstOrNull()?.key
        process = dsl.process ?: throw FormException("Process block must not be null!")
    }


    fun getInitialStep(): Step<*>? {
        return if (initialStepKey != null) {
            steps[initialStepKey]!!
        } else {
            null
        }
    }

    fun getStepByKey(key: String): Step<*>? {
        return steps[key]
    }
}
