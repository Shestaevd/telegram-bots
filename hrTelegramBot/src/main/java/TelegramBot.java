import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import commands.DefaultCommand;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import commands.ReplyCommand;
import ru.kvp24.util.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TelegramBot extends TelegramLongPollingBot {

    String token;
    String botName;
    List<ReplyCommand> commands;
    DefaultCommand defaultCommand;

    Cache<@NonNull Long, @NonNull Long> cache = Caffeine
            .newBuilder()
            .recordStats()
            .expireAfterWrite(5L, TimeUnit.MINUTES)
            .build();

    public static void create(String token, String botName, DefaultCommand defaultCommand, ReplyCommand... commands) {
        TelegramBot bot = new TelegramBot(token, botName, defaultCommand, commands);
        TelegramBotsApi telegramBotsApi = null;
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        try {
            if (telegramBotsApi != null) {
                telegramBotsApi.registerBot(bot);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private TelegramBot(String token, String botName, DefaultCommand defaultCommand, ReplyCommand... commands) {
        this.token = token;
        this.botName = botName;
        this.commands = new ArrayList<>(Arrays.asList(commands));
        this.defaultCommand = defaultCommand;
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
        try {
            if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getChatId() != null) {
                Long chatId = update.getMessage().getChatId();
                String text = update.getMessage().getText();
                if (isLimitExceed(chatId)) {
                    SendMessage reply = commands
                        .stream()
                        .filter(predicate -> predicate.command().trim().equals(text.trim().toLowerCase()))
                        .findFirst()
                        .map(command -> command.handle(update.getMessage()))
                        .orElse(defaultCommand.handle(update.getMessage()));
                    updateCache(chatId);
                    execute(reply);
                } else {
                    SendMessage errorReply = new SendMessage();
                    errorReply.setChatId(update.getMessage().getChatId().toString());
                    errorReply.setText("Все ваши обращения зарегистрированны, приносим извинения если их обработка занимает много времени. Мы обязательно с вами свяжемся, если вы указали корректные данные");
                    execute(errorReply);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("Unhandled error on message: " + e.getLocalizedMessage(), botName);
        }
    }


    private void updateCache(Long key) {
        if (cache.asMap().containsKey(key)) {
            cache.put(key, cache.asMap().get(key) + 1L);
        } else {
            cache.put(key, 1L);
        }
    }

    private boolean isLimitExceed(Long key) {
        return !(cache.asMap().containsKey(key) && cache.asMap().get(key) > 2L);
    }
}
