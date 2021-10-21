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

import me.ruslanys.telegraff.core.annotation.TelegraffDsl
import me.ruslanys.telegraff.core.exception.FormException

@TelegraffDsl
class FormDsl {

    internal val stepDslList: MutableList<StepDsl<*>> = arrayListOf()
    internal var process: ProcessBlock? = null


    fun <T : Any> step(key: String, init: StepDsl<T>.() -> Unit): StepDsl<T> {
        val dsl = StepDsl<T>(key).apply(init)
        stepDslList += dsl
        return dsl
    }

    fun process(processor: ProcessBlock) {
        this.process = processor
    }

    internal fun buildSteps(): Map<String, Step<*>> {
        val steps: ArrayList<Step<*>> = arrayListOf()

        for (i in 0 until stepDslList.size) {
            val builder = stepDslList[i]
            val step = builder.build {
                if (i + 1 < stepDslList.size) {
                    stepDslList[i + 1].key
                } else {
                    null
                }
            }
            steps.add(step)
        }

        return steps.associateBy { it.key }
    }
}

@TelegraffDsl
class StepDsl<T : Any>(val key: String) {

    private var question: QuestionBlock? = null

    private var validation: ValidationBlock<T>? = null
    private var next: NextStepBlock? = null


    fun question(question: QuestionBlock) {
        this.question = question
    }

    fun validation(validation: ValidationBlock<T>) {
        this.validation = validation
    }

    fun next(next: NextStepBlock) {
        this.next = next
    }

    internal fun build(defaultNext: NextStepBlock): Step<T> {
        return Step(
            key,
            question ?: throw FormException("Step question must not be null!"),
            validation ?: {
                @Suppress("UNCHECKED_CAST")
                it as T
            },
            next ?: defaultNext
        )
    }
}


typealias ProcessBlock = (state: FormState, answers: Map<String, Any>) -> Unit

typealias QuestionBlock = (FormState) -> Unit
typealias ValidationBlock<T> = (String) -> T
typealias NextStepBlock = (FormState) -> String?
