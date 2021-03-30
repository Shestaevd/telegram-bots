package ru.kvp24;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.kvp24.messages.IncomingUpdate;
import ru.kvp24.messages.OutgoingSendMessage;
import ru.kvp24.stateMachine.State;
import ru.kvp24.stateMachine.StateManager;
import ru.kvp24.util.Logger;
import ru.kvp24.util.TimeUtils;
import scala.Option;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

public class TelegramBot extends TelegramLongPollingBot {

    String token;
    String botName;
    StateManager<IncomingUpdate, OutgoingSendMessage> handler;


    public TelegramBot(String token, String botName, State<IncomingUpdate, OutgoingSendMessage> initState) {
        this.token = token;
        this.botName = botName;
        this.handler = new StateManager<>(FiniteDuration.apply(8, TimeUnit.HOURS), initState);
    }

    public static void create(String token, String botName, State<IncomingUpdate, OutgoingSendMessage> initState) {
        TelegramBot bot = new TelegramBot(token, botName, initState);
        TelegramBotsApi telegramBotsApi;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (TimeUtils.isNightTime()) {
            Utils.extractChatId(update).foreach(chatId -> {
                Option<OutgoingSendMessage> reply = handler.messageReceive(IncomingUpdate.apply(chatId, update));
                if (reply.isDefined())
                    try {
                        SendMessage toSend = reply.get().outgoingMessage();
                        toSend.setChatId(chatId);
                        execute(toSend);
                    } catch (TelegramApiException exception) {
                        Logger.error("Error occurred during reply execution " + reply.get().toString());
                        exception.printStackTrace();
                    }
                return null;
            });
        }
    }
}
