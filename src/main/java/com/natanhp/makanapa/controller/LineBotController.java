package com.natanhp.makanapa.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.ReplyEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.event.source.UserSource;
import com.linecorp.bot.model.objectmapper.ModelObjectMapper;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.natanhp.makanapa.model.FoodsModel;
import com.natanhp.makanapa.model.LineEventsModel;
import com.natanhp.makanapa.service.BotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class LineBotController {

    @Autowired
    @Qualifier("lineSignatureValidator")
    private LineSignatureValidator lineSignatureValidator;

    @Autowired
    private BotService botService;

    private UserProfileResponse sender = null;
    private FoodsModel foodsModel = null;

    private String[] commands = {
            "bot leave",
            "makan",
            "help"
    };

    @RequestMapping(value = "/webhook", method = RequestMethod.POST)
    public ResponseEntity<String> callback(@RequestHeader("X-Line-Signature") String xLineSignature, @RequestBody String eventPayload) {
        try {
//            if (!lineSignatureValidator.validateSignature(eventPayload.getBytes(), xLineSignature)) {
//                throw new RuntimeException("Invalid signature validation");
//            }

            ObjectMapper objectMapper = ModelObjectMapper.createNewObjectMapper();
            LineEventsModel lineEventsModel = objectMapper.readValue(eventPayload, LineEventsModel.class);

            lineEventsModel.getEvents().forEach(event -> {
                if (event instanceof FollowEvent || event instanceof JoinEvent) {
                    String replyToken = ((ReplyEvent) event).getReplyToken();
                    greetingMessage(replyToken, event.getSource());
                } else if (event instanceof MessageEvent) {
                    if (event.getSource() instanceof GroupSource) {
                        handleGroupChats((MessageEvent) event);
                    } else if (event.getSource() instanceof RoomSource) {
                        handlerRoomChats((MessageEvent) event);
                    } else if (event.getSource() instanceof UserSource) {
                        handleOneOnOneChats((MessageEvent) event);
                    }
                }
            });

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage() + e.toString() + e.getCause() + e.getStackTrace() + " TULUNG ", HttpStatus.BAD_REQUEST);
        }
    }

    private void handleGroupChats(MessageEvent event) {
        String msgText = ((TextMessageContent) event.getMessage()).toString();

        if (msgText.contains(commands[0])) {
            try {
                botService.leaveGroup(((GroupSource) event.getSource()).getGroupId());
            } catch (Exception e) {
                botService.replyText(event.getReplyToken(), "Hi, tambahkan dulu bot makanapa sebagai teman!");
            }
        } else if (msgText.contains(commands[1])) {
//            TODO = Create a function to generate flex-like message with food picture and name and a button to retry
        } else if (msgText.contains(commands[2])) {
            helpMessageHandler(event.getReplyToken());
        } else {
            fallbackMessageHandler(event.getReplyToken());
        }
    }

    private void greetingMessage(String replyToken, Source source) {
        String greetingMessage;
        if (source instanceof UserSource) {
            String senderId = source.getSenderId();
            sender = botService.getProfile(senderId);

            greetingMessage = "Hallo " + sender.getDisplayName() + "! Selamat datang di makanapa bot buat kamu yang bingung mau makan apa!\nSilahkan ketik \'" + commands[2] + "\' untuk melihat semua perintah bot!";
        } else {
            greetingMessage = "Hallo ! Selamat datang di makanapa bot buat kamu yang bingung mau makan apa!\nSilahkan ketik \'" + commands[2] + "\' untuk melihat semua perintah bot!";
        }

        botService.replyText(replyToken, greetingMessage);
    }

    private void handlerRoomChats(MessageEvent event) {
        String msgText = ((TextMessageContent) event.getMessage()).toString();

        if (msgText.contains(commands[0])) {
            try {
                botService.leaveRoom(((RoomSource) event.getSource()).getRoomId());
            } catch (Exception e) {
                botService.replyText(event.getReplyToken(), "Hi, tambahkan dulu bot makanapa sebagai teman!");
            }
        } else if (msgText.contains(commands[1])) {
            //            TODO = Create a function to generate flex-like message with food picture and name and a button to retry
        } else if (msgText.contains(commands[2])) {
            helpMessageHandler(event.getReplyToken());
        } else {
            fallbackMessageHandler(event.getReplyToken());
        }
    }

    private void handleOneOnOneChats(MessageEvent event) {
        String msgText = ((TextMessageContent) event.getMessage()).toString();

        if (msgText.contains(commands[1])) {
            //            TODO = Create a function to generate flex-like message with food picture and name and a button to retry
        } else if (msgText.contains(commands[2])) {
            helpMessageHandler(event.getReplyToken());
        } else {
            fallbackMessageHandler(event.getReplyToken());
        }
    }

    private void helpMessageHandler(String replyToken) {
        String helpMessage = "Halo, bot ini digunakan untuk memilih makanan. MakanApa punya beberapa fitur seperti:\n" +
                commands[0] + " - Untuk mengeluarkan bot dari group / chat room\n" +
                commands[1] + " - Untuk merandom makanan\n" +
                commands[2] + " - Untuk menampilkan pesan ini\n";

        botService.replyText(replyToken, helpMessage);
    }

    private void fallbackMessageHandler(String replyToken) {
        botService.replyText(replyToken, "Maaf aku ngga paham, silahkan ketik \'" + commands[2] + "\' untuk menampilkan semua menu.");
    }
}
