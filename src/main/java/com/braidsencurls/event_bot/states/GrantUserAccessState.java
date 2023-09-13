package com.braidsencurls.event_bot.states;

import com.braidsencurls.event_bot.repositories.UserRepositoryImpl;
import com.braidsencurls.event_bot.services.UserService;
import com.braidsencurls.event_bot.services.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.braidsencurls.event_bot.SendMessageGenerator.generate;

public class GrantUserAccessState implements State {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrantUserAccessState.class);
    private static final UserService userService = new UserServiceImpl(new UserRepositoryImpl());

    @Override
    public String getKey() {
        return "GRANT_USER_ACCESS";
    }
    @Override
    public SendMessage process(Update update) {
        LOGGER.info("Will Process GRANT_USER_ACCESS");
        Long chatId = update.getMessage().getChatId();
        String message = update.getMessage().getText();

        String[] textArr = message.split(" - ");
        if(textArr.length != 2) {
            return generate(chatId, "I'm sorry can you repeat that again." +
                    "Follow the format <username> - <ADMIN/MEMBER>. For example, my_telegram_user - MEMBER");
        } else {
            userService.addUser(textArr[0], textArr[1]);
            setNextState(chatId, null);
            return generate(chatId, textArr[0] + " has been successfully granted an access ");
        }
    }
}
