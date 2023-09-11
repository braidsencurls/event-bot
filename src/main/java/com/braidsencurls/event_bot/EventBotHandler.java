package com.braidsencurls.event_bot;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.braidsencurls.event_bot.commands.Command;
import com.braidsencurls.event_bot.commands.ResponseCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

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
            LOGGER.error("Failed to send message: " + e);
            response.setStatusCode(500);
        }
        return response;
    }

    private void handleUpdate(Update update) {
        try {
            LOGGER.info("Handle Update");
            if (update.getMessage() == null) {
                return;
            }

            String text = update.getMessage().getText();
            Map<String, Command> commandRegistry = SharedData.getInstance().getCommandRegistry();
            Command command = commandRegistry.get(text);
            Command responseCommand = new ResponseCommand();
            SendMessage sendMessage = command != null ? command.execute(update)
                    : responseCommand.execute(update);

            //Sending message
            LOGGER.info("Sending message: " + sendMessage);
            Message message = SENDER.execute(sendMessage);
            LOGGER.info("Message sent: " + message);
        } catch (Exception e) {
            LOGGER.error("Failed to send message: " + e);
            throw new RuntimeException("Failed to send message!", e);
        }
    }
}
