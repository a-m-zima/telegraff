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
import me.ruslanys.telegraff.core.dto.TelegramMessage

data class HttpbinResponse(
    val headers: Map<String, String>,
    val args: Map<String, String>,
    val origin: String,
    val url: String
)

object RestForm : Form<TelegramMessage, InmemoryFormState>(listOf("/rest"), {

    /*val rest = RestTemplateBuilder()
            .rootUri("https://httpbin.org")
            .build()*/

    process {
        // val response = rest.getForObject<HttpbinResponse>("/get")!!

        // MarkdownMessage("Your IP: ${response.origin}")
    }
})
