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
package me.ruslanys.telegraff.core.exception

import me.ruslanys.telegraff.core.data.FormState
import java.lang.reflect.ParameterizedType

abstract class AbstractFormExceptionHandler<M : Any, ST : FormState<M, ST>, EX : Exception> {

    fun canHandle(exception: Exception): Boolean {
        return (this::class.java.genericSuperclass as ParameterizedType)
            .actualTypeArguments.first() == exception::class.java
    }

    abstract fun handleException(message: M, state: ST, exception: EX)

    @Suppress("UNCHECKED_CAST")
    internal fun uncheckHandleException(message: M, state: ST, exception: Exception) {
        handleException(message, state, exception as EX)
    }
}

