package com.braidsencurls.event_bot.commands;

import com.braidsencurls.event_bot.exceptions.NoUserFoundException;
import com.braidsencurls.event_bot.exceptions.UnauthorizedUserException;
import com.braidsencurls.event_bot.repositories.UserRepositoryImpl;
import com.braidsencurls.event_bot.services.UserService;
import com.braidsencurls.event_bot.services.UserServiceImpl;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class GrantUserAccessCommand implements Command {
    UserService userService = new UserServiceImpl(new UserRepositoryImpl());
    @Override
    public SendMessage execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        setNextState(chatId, "GRANT_USER_ACCESS");
        return generateSendMessage(chatId, "Enter the username you want to grant access to. " +
                "Follow the format <username> - <ADMIN/MEMBER>. For example, my_telegram_user - MEMBER");
    }

    @Override
    public String getTextCommand() {
        return "/grantuseraccess";
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
