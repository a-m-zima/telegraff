package me.ruslanys.telegraff.sample.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import me.ruslanys.telegraff.core.data.inmemory.InmemoryFormState
import me.ruslanys.telegraff.core.dsl.Form
import me.ruslanys.telegraff.core.dto.TelegramMessage
import me.ruslanys.telegraff.core.exception.ValidationException
import me.ruslanys.telegraff.sample.NameGenerator
import org.springframework.stereotype.Component
import kotlin.math.abs

@Component
class NameForm(
    telegramBot: TelegramBot,
    nameGenerator: NameGenerator,
) : Form<TelegramMessage, InmemoryFormState>(listOf("/name", "имя"), {

    step<Int>("length")
    {
        question {
            telegramBot.execute(SendMessage(it.chatId, "Какой длины?").parseMode(ParseMode.MarkdownV2))
        }

        validation {
            try {
                abs(it.text!!.toInt())
            } catch (e: Exception) {
                throw ValidationException("Укажи число")
            }
        }
    }


    process {
        val length = it.answers["length"] as Int
        val name = nameGenerator.generateName(length)

        telegramBot.execute(SendMessage(it.chatId, "Сгенерированное имя: $name").parseMode(ParseMode.MarkdownV2))
    }
})
