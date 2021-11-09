package me.ruslanys.telegraff.sample.forms

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import me.ruslanys.telegraff.component.telegrambot.TelegrambotForm
import me.ruslanys.telegraff.core.exception.ValidationException
import org.springframework.stereotype.Component

@Component
class CounterForm(telegramBot: TelegramBot) : TelegrambotForm(listOf("/counter"), {

    step<Int>("counter") {

        question {
            telegramBot.execute(SendMessage(it.chatId, "До скольки нужно посчитать?").parseMode(ParseMode.Markdown))
        }

        validation {
            try {
                it.text().toInt()
            } catch (e: Exception) {
                throw ValidationException("Укажите число")
            }
        }
    }

    process {
        val amount = it.answers["counter"] as Int

        for (i in 1..amount) {
            telegramBot.execute(SendMessage(it.chatId, i.toString()).parseMode(ParseMode.Markdown))
        }
    }
})
