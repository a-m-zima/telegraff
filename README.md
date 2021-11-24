![Telegraff](docs/logo.png "Logo")

[![JitPack](https://img.shields.io/jitpack/v/github/xzima/telegraff?style=for-the-badge)](https://jitpack.io/#xzima/telegraff)

<!-- Описание -->

## Подключение

Gradle:

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
dependencies {
    implementation 'com.github.xzima:telegraff-core:2.0.0'
    implementation 'com.github.xzima:telegraff-autoconfigure:2.0.0'
    implementation 'com.github.xzima:telegraff-starter:2.0.0'
}
```

Maven:

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml

<dependencies>
    <dependency>
        <groupId>com.github.xzima</groupId>
        <artifactId>telegraff-core</artifactId>
        <version>2.0.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.xzima</groupId>
        <artifactId>telegraff-autoconfigure</artifactId>
        <version>2.0.0</version>
    </dependency>
    <dependency>
        <groupId>com.github.xzima</groupId>
        <artifactId>telegraff-starter</artifactId>
        <version>2.0.0</version>
    </dependency>
</dependencies>
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
./mvnw release:prepare        # проверка релиза, обновление версий на релиз, создание тега, обновление версии на следующий snapshot
git push --tags               # отправка релиза с тегом в github
## указать в pom.xml в fromRef предыдущий тег релиза, а в toRef -- текущий(выводимый) тег релиза
./mvnw compile -Pchangelog    #обновление changelog
## тут можно ещё обновить доку например или readme
git commit --amend # добавление changelog в последний коммит
git push --tags --force       # обновление ветки github
./mvnw compile -Prelease-note # генерация release-note
## создать релиз вручную указав имя как версию и поместив в тело сгенерированный RELEASE_NOTES.md без заголовков
./mvnw clean release:clean    # очистка метаданных
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
telegram.access-key=# api key
telegram.mode=# polling (default), webhook
telegram.webhook-base-url=# required for webhook mode
telegram.webhook-endpoint-url=# optional
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
