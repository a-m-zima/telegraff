package me.ruslanys.telegraff.sample.forms

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import me.ruslanys.telegraff.component.telegrambot.TelegrambotForm
import org.springframework.stereotype.Component

@Component
class MarkdownForm(telegramBot: TelegramBot) : TelegrambotForm(listOf("/markdown"), {

    process { _, state ->
        telegramBot.execute(SendMessage(state.chatId, "Привет, *пользователь*!").parseMode(ParseMode.Markdown))
    }
})
