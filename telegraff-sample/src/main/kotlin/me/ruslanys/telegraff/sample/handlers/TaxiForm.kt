package me.ruslanys.telegraff.sample.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode
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
        question {
            telegramBot.execute(SendMessage(it.chatId, "Откуда поедем?").parseMode(ParseMode.Markdown))
        }
    }

    step<String>("locationTo") {
        question {
            telegramBot.execute(SendMessage(it.chatId, "Куда поедем?").parseMode(ParseMode.Markdown))
        }
    }

    step<PaymentMethod>("paymentMethod") {
        question {
            // TODO добавить клавиатуру + валидация не должна её ломать
            telegramBot.execute(SendMessage(it.chatId, "Оплата картой или наличкой?").parseMode(ParseMode.Markdown))
        }

        validation {
            when (it.text()!!.lowercase()) {
                "картой" -> PaymentMethod.CARD
                "наличкой" -> PaymentMethod.CASH
                else -> throw ValidationException("Пожалуйста, выбери один из вариантов")
            }
        }
    }

    process {
        val from = it.answers["locationFrom"] as String
        val to = it.answers["locationTo"] as String
        val paymentMethod = it.answers["paymentMethod"] as PaymentMethod

        // Business logic

        telegramBot.execute(
            SendMessage(
                it.chatId,
                """
            Заказ принят от пользователя #${it.chatId}.
            Поедем из $from в $to. Оплата $paymentMethod.
            """.trimIndent()
            ).parseMode(ParseMode.Markdown)
        )
    }
})
