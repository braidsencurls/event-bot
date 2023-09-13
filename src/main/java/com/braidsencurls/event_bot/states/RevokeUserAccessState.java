package com.braidsencurls.event_bot.states;

import com.braidsencurls.event_bot.repositories.UserRepositoryImpl;
import com.braidsencurls.event_bot.services.UserService;
import com.braidsencurls.event_bot.services.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.braidsencurls.event_bot.SendMessageGenerator.generate;

public class RevokeUserAccessState implements State {
    private static final Logger LOGGER = LoggerFactory.getLogger(RevokeUserAccessState.class);
    private static final UserService userService = new UserServiceImpl(new UserRepositoryImpl());

    @Override
    public String getKey() {
        return "REVOKE_USER_ACCESS";
    }
    @Override
    public SendMessage process(Update update) {
        LOGGER.info("Will Process REVOKE_USER_ACCESS");
        Long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();

        if(userService.deleteUser(message)) {
            return generate(chatId, message + " has been revoked an access");
        }
        return generate(chatId, "Something went wrong! Make sure that "
                + message + " has been granted an access before." +
                "Try again. Enter the username you want to revoke an access");
    }
}
