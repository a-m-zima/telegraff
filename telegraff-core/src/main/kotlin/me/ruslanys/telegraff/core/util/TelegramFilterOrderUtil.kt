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
package me.ruslanys.telegraff.core.util

import me.ruslanys.telegraff.core.annotation.TelegramFilterOrder
import org.springframework.core.annotation.AnnotationUtils

object TelegramFilterOrderUtil {

    fun getOrder(type: Class<*>): Int {
        return AnnotationUtils.findAnnotation(type, TelegramFilterOrder::class.java)?.value ?: 0
    }

}