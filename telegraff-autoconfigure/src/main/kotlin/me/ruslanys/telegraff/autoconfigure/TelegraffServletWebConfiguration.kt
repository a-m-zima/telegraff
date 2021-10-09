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
import me.ruslanys.telegraff.core.client.TelegramClient
import me.ruslanys.telegraff.core.client.TelegramPollingClient
import me.ruslanys.telegraff.core.client.TelegramWebhookClient
import me.ruslanys.telegraff.core.component.DefaultTelegramApi
import me.ruslanys.telegraff.core.component.TelegramApi
import me.ruslanys.telegraff.core.dsl.DefaultHandlersFactory
import me.ruslanys.telegraff.core.dsl.HandlersFactory
import me.ruslanys.telegraff.core.filter.*
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.GenericApplicationContext

/**
 * Configuration for Telegraff when used in a servlet web context.
 *
 * @author Ruslan Molchanov
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(TelegramPollingClient::class, TelegramWebhookClient::class)
@AutoConfigureAfter(WebMvcAutoConfiguration::class, RestTemplateAutoConfiguration::class)
open class TelegraffServletWebConfiguration(val telegramProperties: TelegramProperties) {

    @Bean
    @ConditionalOnMissingBean(TelegramApi::class)
    open fun telegramApi(restTemplateBuilder: RestTemplateBuilder): TelegramApi {
        return DefaultTelegramApi(telegramProperties.accessKey, restTemplateBuilder)
    }

    @Bean
    @ConditionalOnMissingBean(name = ["telegramProperties"])
    open fun telegramProperties(): TelegramProperties = telegramProperties

    // region Clients

    @Bean
    @ConditionalOnMissingBean(TelegramClient::class)
    @ConditionalOnProperty(name = ["telegram.mode"], havingValue = "polling", matchIfMissing = true)
    open fun telegramPollingClient(
        telegramApi: TelegramApi,
        publisher: ApplicationEventPublisher
    ): TelegramPollingClient {
        return TelegramPollingClient(telegramApi, publisher)
    }

    @Bean
    @ConditionalOnMissingBean(TelegramClient::class)
    @ConditionalOnProperty(name = ["telegram.mode"], havingValue = "webhook")
    open fun telegramWebhookClient(
        telegramApi: TelegramApi,
        publisher: ApplicationEventPublisher
    ): TelegramWebhookClient {
        // TODO: Reconfigure with one of the following approaches
        /*
        @Bean(name = ["/ruslanys"])
        fun ruslanController(): Controller {
            val aa = RequestMappingHandlerAdapter()
            val bb = RequestMappingHandlerMapping()
            return Controller { request, response ->
                response.writer.print("ok")

                ModelAndView("index")
                // null
            }
        }
        */
        return TelegramWebhookClient(telegramApi, publisher, telegramProperties.getWebhookUrl())
    }

    // endregion

    @Bean
    @ConditionalOnMissingBean(HandlersFactory::class)
    open fun handlersFactory(context: GenericApplicationContext): DefaultHandlersFactory {
        return DefaultHandlersFactory(context, telegramProperties.handlersPath)
    }

    // region Filters

    @Bean
    @ConditionalOnMissingBean(TelegramFiltersFactory::class, TelegramFilterProcessor::class)
    open fun telegramFiltersFactory(filters: List<TelegramFilter>): DefaultTelegramFiltersFactory {
        return DefaultTelegramFiltersFactory(filters)
    }

    @Bean
    @ConditionalOnMissingBean(HandlersFilter::class)
    open fun handlersFilter(telegramApi: TelegramApi, handlersFactory: HandlersFactory): HandlersFilter {
        return HandlersFilter(telegramApi, handlersFactory)
    }

    @Bean
    @ConditionalOnMissingBean(CancelFilter::class)
    open fun cancelFilter(telegramApi: TelegramApi, handlersFilter: HandlersFilter): CancelFilter {
        return CancelFilter(telegramApi, handlersFilter)
    }

    @Bean
    @ConditionalOnMissingBean(UnresolvedMessageFilter::class)
    @ConditionalOnProperty(name = ["telegram.unresolved-filter.enabled"], matchIfMissing = true)
    open fun unresolvedMessageFilter(telegramApi: TelegramApi): UnresolvedMessageFilter {
        return UnresolvedMessageFilter(telegramApi)
    }

    // endregion

}
