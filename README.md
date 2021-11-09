![Telegraff](docs/logo.png "Logo")

<!-- Описание -->

## Подключение

```groovy
repositories {
    maven {
        url "https://dl.bintray.com/ruslanys/maven"
    }
}
```

Gradle:

```groovy
compile("me.ruslanys.telegraff:telegraff-starter:1.0.0")
```

Maven:

```xml
<dependency>
    <groupId>me.ruslanys.telegraff</groupId>
    <artifactId>telegraff-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Сборка

### Локальная сборка

```shell
./mvnw clean install
```

### Локальная сборка без тестов

```shell
./mvnw clean install -DskipTests
```

### Релиз

```shell
./mvnw release:prepare  # проверка релиза, обновление версий на релиз, создание тега, обновление версии на следующий snapshot
## отправка релиза с тегом в github
## указать в pom.xml в fromRef и toRef значения предыдущего и нового релизов соответственно
./mvnw compile -Prelease-note # генерация release-note
## создать релиз вручную указав имя как версию и поместив в тело сгенерированный RELEASE_NOTES.md без заголовков
./mvnw clean release:clean # очистка метаданных
```

### Обновление changelog

```shell
./mvnw compile -Pchangelog
```

### Отчет о покрытии тестами

```shell
./mvnw clean verify -Pcoverage
## see coverage/target/site/jacoco-aggregate/index.html
```

## Настройка

```properties
telegram.access-key=                  # api key
telegram.mode=                        # polling (default), webhook
telegram.webhook-base-url=            # required for webhook mode
telegram.webhook-endpoint-url=        # optional
```

## Использование

```kotlin
package me.ruslanys.telegraff.sample.forms

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove
import com.pengrad.telegrambot.request.SendMessage
import me.ruslanys.telegraff.component.telegrambot.TelegrambotForm
import me.ruslanys.telegraff.core.exception.ValidationException
import org.springframework.stereotype.Component

enum class PaymentMethod {
    CARD, CASH
}

@Component
class TaxiForm(telegramBot: TelegramBot) : TelegrambotForm(listOf("/taxi", "такси"), {
    step<String>("locationFrom") {
        question { message, state ->
            telegramBot.execute(SendMessage(state.chatId, "@${message.from().username()} Откуда поедем?"))
        }
    }

    step<String>("locationTo") {
        question { message, state ->
            telegramBot.execute(SendMessage(state.chatId, "@${message.from().username()} Куда поедем?"))
        }
    }

    step<PaymentMethod>("paymentMethod") {
        question { message, state ->
            telegramBot.execute(
                SendMessage(state.chatId, "@${message.from().username()} Оплата картой или наличкой?")
                    .replyMarkup(
                        ReplyKeyboardMarkup("картой", "наличкой")
                            .resizeKeyboard(true)
                            .selective(true)
                    )
            )
        }

        validation {
            when (it.text()!!.lowercase()) {
                "картой" -> PaymentMethod.CARD
                "наличкой" -> PaymentMethod.CASH
                else -> throw ValidationException("Пожалуйста, выбери один из вариантов")
            }
        }
    }

    process { message, state ->
        val from = state.answers["locationFrom"] as String
        val to = state.answers["locationTo"] as String
        val paymentMethod = state.answers["paymentMethod"] as PaymentMethod

        // Business logic

        telegramBot.execute(
            SendMessage(
                state.chatId,
                """
                Заказ принят от пользователя @${message.from().username()}.
                Поедем из $from в $to. Оплата $paymentMethod.
                """.trimIndent()
            ).replyMarkup(ReplyKeyboardRemove())
        )
    }
})
```
