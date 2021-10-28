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
import me.ruslanys.telegraff.core.data.FormState
import me.ruslanys.telegraff.core.exception.FormException

@TelegraffDsl
class FormDsl<M : Any, ST : FormState<M,ST>> {

    internal val stepDslList: MutableList<StepDsl<M, *, ST>> = arrayListOf()
    internal var process: ProcessBlock<ST>? = null


    fun <T : Any> step(key: String, init: StepDsl<M, T, ST>.() -> Unit): StepDsl<M, T, ST> {
        val dsl = StepDsl<M, T, ST>(key).apply(init)
        stepDslList += dsl
        return dsl
    }

    fun process(processor: ProcessBlock<ST>) {
        this.process = processor
    }

    internal fun buildSteps(): Map<String, Step<M, *, ST>> {
        val steps: ArrayList<Step<M, *, ST>> = arrayListOf()

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
class StepDsl<M : Any, T : Any, ST : FormState<M,ST>>(val key: String) {

    private var question: QuestionBlock<ST>? = null

    private var validation: ValidationBlock<M, T>? = null
    private var next: NextStepBlock<ST>? = null


    fun question(question: QuestionBlock<ST>) {
        this.question = question
    }

    fun validation(validation: ValidationBlock<M, T>) {
        this.validation = validation
    }

    fun next(next: NextStepBlock<ST>) {
        this.next = next
    }

    internal fun build(defaultNext: NextStepBlock<ST>): Step<M, T, ST> {
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


typealias  ProcessBlock<ST> = (ST) -> Unit

typealias QuestionBlock<ST> = (ST) -> Unit
typealias ValidationBlock<M, T> = (M) -> T
typealias NextStepBlock<ST> = (ST) -> String?
