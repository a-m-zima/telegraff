package me.ruslanys.telegraff.sample.handlers

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import me.ruslanys.telegraff.component.telegrambot.TelegrambotForm
import org.springframework.stereotype.Component

@Component
class HtmlForm(telegramBot: TelegramBot) : TelegrambotForm(listOf("/html"), {

    process {
        telegramBot.execute(SendMessage(it.chatId, "Привет, <b>пользователь!</b>").parseMode(ParseMode.HTML))
    }
})
