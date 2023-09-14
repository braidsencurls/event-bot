package com.braidsencurls.event_bot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.braidsencurls.event_bot.commands.Command;
import com.braidsencurls.event_bot.commands.ResponseCommand;
import com.braidsencurls.event_bot.exceptions.UnauthorizedUserException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;

import static com.braidsencurls.event_bot.TelegramFactory.sender;
import static java.lang.System.getenv;

public class EventBotHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final AbsSender SENDER = sender(getenv("bot_token"), getenv("bot_username"));
    private static final Logger LOGGER = LoggerFactory.getLogger(EventBotHandler.class);

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {LOGGER.info("Received message....");
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            String messageBody = request.getBody();
            LOGGER.info("Message Body: " + messageBody);
            Update update = MAPPER.readValue(messageBody, Update.class);
            handleUpdate(update);
            response.setStatusCode(200);
        } catch (Exception e) {
            response.setStatusCode(200);
        }
        return response;
    }

    private void handleUpdate(Update update) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        Long chatId = update.getMessage().getChatId();

        try {
            LOGGER.info("Handle Update");
            if (update.getMessage() == null) {
                return;
            }

            String text = update.getMessage().getText();
            String username = update.getMessage().getChat().getUserName();

            Command command = getCommand(text);
            command.isUserAuthorized(username);
            sendMessage = command.execute(update);
            SENDER.execute(sendMessage);
        } catch (UnauthorizedUserException e) {
            LOGGER.error("Unauthorized user access");
            sendMessage.setChatId(chatId);
            sendMessage.setText("I am sorry but you are not authorized to perform the command");
            SENDER.execute(sendMessage);
        } catch (Exception e) {
            LOGGER.error("Failed to send message: " + e);
            sendMessage.setChatId(chatId);
            sendMessage.setText("Ouhhh noooo!! Some technical issues!");
            SENDER.execute(sendMessage);
            throw new RuntimeException("Failed to send message!", e);
        }
    }

    private static Command getCommand(String text) {
        Map<String, Command> commandRegistry = SharedData.getInstance().getCommandRegistry();
        Command command = commandRegistry.get(text);
        return command == null ? new ResponseCommand() : command;

    }
}
