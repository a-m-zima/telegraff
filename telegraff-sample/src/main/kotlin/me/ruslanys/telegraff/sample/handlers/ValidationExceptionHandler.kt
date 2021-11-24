package me.ruslanys.telegraff.sample.handlers

import com.pengrad.telegrambot.TelegramBot
import org.telegram.telegrambots.meta.api.objects.Message
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import me.ruslanys.telegraff.component.telegrambot.TelegrambotFormState
import me.ruslanys.telegraff.core.exception.AbstractFormExceptionHandler
import me.ruslanys.telegraff.core.exception.ValidationException
import org.springframework.stereotype.Component


@Component
class ValidationExceptionHandler(
    private val telegramBot: TelegramBot
) : AbstractFormExceptionHandler<Message, TelegrambotFormState, ValidationException>() {

    override fun handleException(message: Message, state: TelegrambotFormState, exception: ValidationException) {
        telegramBot.execute(
            SendMessage(message.chat().id(), exception.message)
                .replyToMessageId(message.messageId())
                .parseMode(ParseMode.Markdown)
        )
    }
}
