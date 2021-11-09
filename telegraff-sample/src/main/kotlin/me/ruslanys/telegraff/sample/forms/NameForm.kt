package me.ruslanys.telegraff.sample.forms

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import me.ruslanys.telegraff.component.telegrambot.TelegrambotForm
import me.ruslanys.telegraff.core.exception.ValidationException
import me.ruslanys.telegraff.sample.NameGenerator
import org.springframework.stereotype.Component
import kotlin.math.abs

@Component
class NameForm(
    telegramBot: TelegramBot,
    nameGenerator: NameGenerator,
) : TelegrambotForm(listOf("/name", "имя"), {

    step<Int>("length")
    {
        question {
            telegramBot.execute(SendMessage(it.chatId, "Какой длины?").parseMode(ParseMode.Markdown))
        }

        validation {
            try {
                abs(it.text()!!.toInt())
            } catch (e: Exception) {
                throw ValidationException("Укажи число")
            }
        }
    }


    process {
        val length = it.answers["length"] as Int
        val name = nameGenerator.generateName(length)

        telegramBot.execute(SendMessage(it.chatId, "Сгенерированное имя: $name").parseMode(ParseMode.Markdown))
    }
})
