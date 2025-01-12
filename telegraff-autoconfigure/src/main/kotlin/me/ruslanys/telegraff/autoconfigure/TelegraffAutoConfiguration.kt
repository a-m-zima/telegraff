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

import me.ruslanys.telegraff.autoconfigure.property.TelegramProperties
import me.ruslanys.telegraff.autoconfigure.property.TelegramPropertiesValidator
import me.ruslanys.telegraff.core.client.TelegramPollingClient
import me.ruslanys.telegraff.core.client.TelegramWebhookClient
import me.ruslanys.telegraff.core.component.TelegramApi
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.validation.Validator

/**
 * Enable Auto-Configuration for Telegraff.
 *
 * @author Ruslan Molchanov
 */
@Configuration
@ConditionalOnClass(TelegramPollingClient::class, TelegramWebhookClient::class, TelegramApi::class)
@EnableConfigurationProperties(TelegramProperties::class)
@Import(TelegraffNonWebConfiguration::class, TelegraffServletWebConfiguration::class)
open class TelegraffAutoConfiguration {

    companion object {
        @Bean
        fun configurationPropertiesValidator(): Validator {
            return TelegramPropertiesValidator()
        }
    }
}
