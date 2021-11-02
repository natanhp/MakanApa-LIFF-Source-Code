package com.natanhp.makanapa.service;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class BotService {

    @Autowired
    private LineMessagingClient lineMessagingClient;

    public void reply(ReplyMessage replyMessage) {
        try {
            lineMessagingClient.replyMessage(replyMessage).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void reply(String replyToken, Message message) {
        ReplyMessage replyMessage = new ReplyMessage(replyToken, message);
        reply(replyMessage);
    }

    public void reply(String replyToken, List<Message> messages) {
        ReplyMessage replyMessage = new ReplyMessage(replyToken, messages);
        reply(replyMessage);
    }

    public void replyText(String replyToken, String messageText) {
        TextMessage textMessage = new TextMessage(messageText);
        reply(replyToken, textMessage);
    }

    public void replyText(String replyToken, String[] messageText) {
        List<Message> textMessages = Arrays
                .stream(messageText)
                .map(TextMessage::new)
                .collect(Collectors.toList());

        reply(replyToken, textMessages);
    }

    public UserProfileResponse getProfile(String userId) {
        try {
            return lineMessagingClient.getProfile(userId).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void leaveGroup(String groupId) {
        try {
            lineMessagingClient.leaveGroup(groupId).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void leaveRoom(String roomId) {
        try {
            lineMessagingClient.leaveRoom(roomId).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
