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

import me.ruslanys.telegraff.core.data.FormState

class Step<M : Any, T : Any, ST : FormState<M,ST>>(
    val key: String,
    val question: QuestionBlock<M, ST>,
    val validation: ValidationBlock<M, T>,
    val next: NextStepBlock<ST>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Step<*, *, *>) return false

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }
}
