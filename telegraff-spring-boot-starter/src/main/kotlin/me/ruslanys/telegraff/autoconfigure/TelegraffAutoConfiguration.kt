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
package me.ruslanys.telegraff.autoconfigure

import me.ruslanys.telegraff.autoconfigure.property.TelegraffProperties
import me.ruslanys.telegraff.autoconfigure.property.TelegraffPropertiesValidator
import me.ruslanys.telegraff.core.handler.CompositeMessageHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.validation.Validator
import org.telegram.telegrambots.meta.TelegramBotsApi

/**
 * Enable Auto-Configuration for Telegraff.
 *
 * @author Ruslan Molchanov
 * @author Alex Zima
 */
@Configuration
@ConditionalOnClass(CompositeMessageHandler::class, TelegramBotsApi::class)
@ConditionalOnProperty(prefix = "telegrambots", name = ["enabled"], havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(TelegraffProperties::class)
@Import(TelegraffNonWebConfiguration::class, TelegraffServletWebConfiguration::class)
open class TelegraffAutoConfiguration {

    companion object {
        @Bean
        fun configurationPropertiesValidator(): Validator {
            return TelegraffPropertiesValidator()
        }
    }
}
