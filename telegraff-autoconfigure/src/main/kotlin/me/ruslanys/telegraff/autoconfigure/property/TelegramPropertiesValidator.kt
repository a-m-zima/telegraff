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
package me.ruslanys.telegraff.autoconfigure.property

import org.springframework.validation.Errors
import org.springframework.validation.Validator

class TelegramPropertiesValidator : Validator {

    override fun supports(type: Class<*>): Boolean {
        return type === TelegramProperties::class.java
    }

    override fun validate(target: Any, errors: Errors) {
        val properties = target as TelegramProperties

        if (properties.accessKey.isEmpty()) {
            errors.rejectValue("accessKey", "accessKey.null",
                    "Telegram Access Key must not be null!")
        }

        if (properties.handlersPath.isEmpty()) {
            errors.rejectValue("handlersPath", "handlersPath.null",
                    "Telegram handlers path must not be null!")
        }

        if (properties.mode == TelegramMode.WEBHOOK) {
            val webhookBaseUrl = properties.webhookBaseUrl
            if (webhookBaseUrl == null || webhookBaseUrl.isEmpty()) {
                errors.rejectValue("webhookBaseUrl", "webhookBaseUrl.empty",
                        "You have to set Webhook Base URL with Webhook mode.")
            } else if (!webhookBaseUrl.startsWith("https://")) {
                errors.rejectValue("webhookBaseUrl", "webhookBaseUrl.https",
                        "You have to set HTTPS protocol at Webhook base URL.")
            }

        }
    }

}