package com.braidsencurls.event_bot.commands;

import com.braidsencurls.event_bot.exceptions.UnauthorizedUserException;
import com.braidsencurls.event_bot.repositories.UserRepositoryImpl;
import com.braidsencurls.event_bot.services.UserService;
import com.braidsencurls.event_bot.services.UserServiceImpl;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.braidsencurls.event_bot.SendMessageGenerator.generate;

public class RevokeUserAccessCommand implements Command {
    UserService userService = new UserServiceImpl(new UserRepositoryImpl());
    @Override
    public SendMessage execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        initState(chatId, "REVOKE_USER_ACCESS");
        return generate(chatId, "Enter the username you want to revoke an access");
    }

    @Override
    public String getTextCommand() {
        return "/revokeuseraccess";
    }

    @Override
    public boolean isUserAuthorized(String username) {
        if(userService.isAdmin(username)) {
            return true;
        } else {
            throw new UnauthorizedUserException("User is not authorized to perform this command");
        }
    }
}
