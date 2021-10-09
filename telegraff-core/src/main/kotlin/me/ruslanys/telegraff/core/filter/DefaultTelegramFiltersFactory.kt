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
package me.ruslanys.telegraff.core.filter

import me.ruslanys.telegraff.core.event.TelegramUpdateEvent
import me.ruslanys.telegraff.core.util.TelegramFilterOrderUtil

class DefaultTelegramFiltersFactory(filters: List<TelegramFilter>) : TelegramFiltersFactory, TelegramFilterProcessor {

    private val filters: List<TelegramFilter> = filters.sortedBy {
        TelegramFilterOrderUtil.getOrder(it::class.java)
    }

    override fun getFilters(): List<TelegramFilter> {
        return filters
    }

    override fun process(event: TelegramUpdateEvent) {
        if (event.update.message != null) { // only new messages are supported
            val chain = DefaultTelegramFilterChain(filters)
            chain.doFilter(event.update.message)
        }
    }

}