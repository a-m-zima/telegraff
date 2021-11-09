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
