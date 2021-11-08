package me.ruslanys.telegraff.sample.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import me.ruslanys.telegraff.core.data.inmemory.InmemoryFormState
import me.ruslanys.telegraff.core.dsl.Form
import me.ruslanys.telegraff.core.dto.TelegramMessage
import me.ruslanys.telegraff.core.exception.ValidationException
import org.springframework.stereotype.Component

enum class PaymentMethod {
    CARD, CASH
}

@Component
class TaxiForm(telegramBot: TelegramBot) : Form<TelegramMessage, InmemoryFormState>(listOf("/taxi", "такси"), {
    step<String>("locationFrom") {
        question {
            telegramBot.execute(SendMessage(it.chatId, "Откуда поедем?").parseMode(ParseMode.MarkdownV2))
        }
    }

    step<String>("locationTo") {
        question {
            telegramBot.execute(SendMessage(it.chatId, "Куда поедем?").parseMode(ParseMode.MarkdownV2))
        }
    }

    step<PaymentMethod>("paymentMethod") {
        question {
            telegramBot.execute(SendMessage(it.chatId, "Оплата картой или наличкой?").parseMode(ParseMode.MarkdownV2))
        }

        validation {
            when (it.text!!.lowercase()) {
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
            ).parseMode(ParseMode.MarkdownV2)
        )
    }
})
