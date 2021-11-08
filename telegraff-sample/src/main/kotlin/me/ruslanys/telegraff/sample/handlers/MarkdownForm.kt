package me.ruslanys.telegraff.sample.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import me.ruslanys.telegraff.core.data.inmemory.InmemoryFormState
import me.ruslanys.telegraff.core.dsl.Form
import me.ruslanys.telegraff.core.dto.TelegramMessage
import org.springframework.stereotype.Component

@Component
class MarkdownForm(telegramBot: TelegramBot) : Form<TelegramMessage, InmemoryFormState>(listOf("/markdown"), {

    process {
        telegramBot.execute(SendMessage(it.chatId, "Привет, *пользователь*!").parseMode(ParseMode.MarkdownV2))
    }
})
