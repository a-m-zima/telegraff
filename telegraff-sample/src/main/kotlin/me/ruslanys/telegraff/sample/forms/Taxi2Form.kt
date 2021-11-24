package me.ruslanys.telegraff.sample.forms

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove
import com.pengrad.telegrambot.request.DeleteMessage
import com.pengrad.telegrambot.request.SendMessage
import me.ruslanys.telegraff.component.telegrambot.TelegrambotForm
import me.ruslanys.telegraff.core.exception.ValidationException
import org.springframework.stereotype.Component

/**
 * Аналогичен TaxiForm. Дополнительно чистит переписку. Рассчитан на одновременное существование одной формы
 * @see me.ruslanys.telegraff.sample.forms.TaxiForm
 */
@Component
class Taxi2Form(telegramBot: TelegramBot) : TelegrambotForm(listOf("/taxi2"), {
    var firstMess: Int = -1
    step<String>("locationFrom") {
        question { message, state ->
            firstMess = telegramBot.execute(SendMessage(state.chatId, "@${message.from().username()} Откуда поедем?"))
                .message().messageId()
        }
        validation {
            val answer: String = it.text()

            telegramBot.execute(DeleteMessage(it.chat().id(), it.messageId()))

            answer
        }
    }

    step<String>("locationTo") {
        question { message, state ->
            telegramBot.execute(DeleteMessage(state.chatId, firstMess))
            firstMess = telegramBot.execute(SendMessage(state.chatId, "@${message.from().username()} Куда поедем?"))
                .message().messageId()
        }
        validation {
            val answer: String = it.text()

            telegramBot.execute(DeleteMessage(it.chat().id(), it.messageId()))

            answer
        }
    }

    step<PaymentMethod>("paymentMethod") {
        question { message, state ->
            telegramBot.execute(DeleteMessage(state.chatId, firstMess))
            firstMess = telegramBot.execute(
                SendMessage(state.chatId, "@${message.from().username()} Оплата картой или наличкой?")
                    .replyMarkup(
                        ReplyKeyboardMarkup("картой", "наличкой")
                            .resizeKeyboard(true)
                            .selective(true)
                    )
            ).message().messageId()
        }
        validation {
            val answer = when (it.text()!!.lowercase()) {
                "картой" -> PaymentMethod.CARD
                "наличкой" -> PaymentMethod.CASH
                else -> throw ValidationException("Пожалуйста, выбери один из вариантов")
            }

            telegramBot.execute(DeleteMessage(it.chat().id(), it.messageId()))

            answer
        }
    }

    process { message, state ->
        val from = state.answers["locationFrom"] as String
        val to = state.answers["locationTo"] as String
        val paymentMethod = state.answers["paymentMethod"] as PaymentMethod

        // Business logic

        telegramBot.execute(DeleteMessage(state.chatId, firstMess))
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
