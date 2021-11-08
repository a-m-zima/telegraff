package me.ruslanys.telegraff.sample.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import me.ruslanys.telegraff.core.data.inmemory.InmemoryFormState
import me.ruslanys.telegraff.core.dsl.Form
import me.ruslanys.telegraff.core.dto.TelegramMessage
import me.ruslanys.telegraff.core.exception.ValidationException
import org.springframework.stereotype.Component

@Component
class CounterForm(telegramBot: TelegramBot) : Form<TelegramMessage, InmemoryFormState>(listOf("/counter"), {

    step<Int>("counter") {

        question {
            telegramBot.execute(SendMessage(it.chatId, "До скольки нужно посчитать?").parseMode(ParseMode.MarkdownV2))
        }

        validation {
            try {
                it.text!!.toInt()
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
