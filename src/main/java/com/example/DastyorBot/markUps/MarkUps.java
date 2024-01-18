package com.example.DastyorBot.markUps;

import com.example.DastyorBot.constant.*;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.LinkedList;
import java.util.List;

@Component
public class MarkUps {
    public static ReplyKeyboard contactButton() {
        KeyboardRow row = new KeyboardRow();
        KeyboardButton button = new KeyboardButton("Share\uD83D\uDCDE⏏\uFE0F");
        button.setRequestContact(true);
        row.add(button);
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row))
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard adminButton() {
        KeyboardRow row = new KeyboardRow();
        KeyboardButton button = new KeyboardButton("Adminga savol yo'llash");
        row.add(button);
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row))
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard backButton() {
        KeyboardRow row = new KeyboardRow();
        KeyboardButton button = new KeyboardButton("Ortga");
        row.add(button);
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row))
                .resizeKeyboard(true)
                .build();
    }

    public static InlineKeyboardMarkup menu() {
        List<InlineKeyboardButton> buttonsRow=new LinkedList<>();
        List<List<InlineKeyboardButton>> rowList=new LinkedList<>();

        InlineKeyboardButton button=new InlineKeyboardButton();
        button.setText(AutoConstant.AVTO);
        button.setCallbackData(AutoConstant.AVTO);

        buttonsRow.add(button);
        rowList.add(buttonsRow);
        button=new InlineKeyboardButton();
        buttonsRow=new LinkedList<>();

        button.setText(AutoServiceConstants.AVTO_SERVICE);
        button.setCallbackData(AutoServiceConstants.AVTO_SERVICE);

        buttonsRow.add(button);
        rowList.add(buttonsRow);
        button=new InlineKeyboardButton();
        buttonsRow=new LinkedList<>();

        button.setText(AutoConstant.AVTO_SPARE_PARTS_STORES);
        button.setCallbackData(AutoConstant.AVTO_SPARE_PARTS_STORES);

        buttonsRow.add(button);
        rowList.add(buttonsRow);
        button=new InlineKeyboardButton();
        buttonsRow=new LinkedList<>();

        button.setText(HomeConstants.HOME);
        button.setCallbackData(HomeConstants.HOME);

        buttonsRow.add(button);
        rowList.add(buttonsRow);
        button=new InlineKeyboardButton();
        buttonsRow=new LinkedList<>();

        button.setText(HomeServiceConstants.HOME_SERVICE);
        button.setCallbackData(HomeServiceConstants.HOME_SERVICE);

        buttonsRow.add(button);
        rowList.add(buttonsRow);
        button=new InlineKeyboardButton();
        buttonsRow=new LinkedList<>();

        button.setText("Apteka 24/7 \uD83D\uDC8A\uD83C\uDFEA");
        button.setCallbackData("Apteka 24/7 \uD83D\uDC8A\uD83C\uDFEA");

        buttonsRow.add(button);
        rowList.add(buttonsRow);
        button=new InlineKeyboardButton();
        buttonsRow=new LinkedList<>();

        button.setText(HospitalConstants.HOSPITAL);
        button.setCallbackData(HospitalConstants.HOSPITAL);



//        buttonsRow.add(button);
//        button=new InlineKeyboardButton();
//
//        button.setUrl("https://sanjarni-birinchi-sayti.netlify.app");
//        button.setText("set url");
//        button.setCallbackData("Admin chat");


        buttonsRow.add(button);
        rowList.add(buttonsRow);
        return new InlineKeyboardMarkup(rowList);

        /**
         *         InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
         *
         *         List<InlineKeyboardButton> keyboardButtonRow = new ArrayList<>();
         *         List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
         *
         *         inlineKeyboardButton.setText(Language.UZBEK + " \uD83C\uDDFA\uD83C\uDDFF");
         *         inlineKeyboardButton.setCallbackData(Language.UZBEK.toString());
         *
         *         keyboardButtonRow.add(inlineKeyboardButton);
         *         inlineKeyboardButton = new InlineKeyboardButton();
         *
         *         inlineKeyboardButton.setText(Language.TURK + " \uD83C\uDDF9\uD83C\uDDF7");
         *         inlineKeyboardButton.setCallbackData(Language.TURK.toString());
         *
         *         keyboardButtonRow.add(inlineKeyboardButton);
         *         inlineKeyboardButton = new InlineKeyboardButton();
         *
         *         rowList.add(keyboardButtonRow);
         *         keyboardButtonRow = new ArrayList<>();
         *
         *         inlineKeyboardButton.setText(Language.RUSSIAN + " \uD83C\uDDF7\uD83C\uDDFA");
         *         inlineKeyboardButton.setCallbackData(Language.RUSSIAN.toString());
         *
         *         keyboardButtonRow.add(inlineKeyboardButton);
         *         inlineKeyboardButton = new InlineKeyboardButton();
         *
         *         inlineKeyboardButton.setText(Language.ENGLISH + " \uD83C\uDDEC\uD83C\uDDE7");
         *         inlineKeyboardButton.setCallbackData(Language.ENGLISH.toString());
         *
         *         keyboardButtonRow.add(inlineKeyboardButton);
         *
         *         rowList.add(keyboardButtonRow);**/
    }

    public static ReplyKeyboard backWithNextButtons() {
        KeyboardRow row = new KeyboardRow();
        KeyboardButton button = new KeyboardButton(CommonConstants.BACK);
        row.add(button);
        KeyboardButton button1 = new KeyboardButton(CommonConstants.NEXT);
        row.add(button1);
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row))
                .resizeKeyboard(true)
                .build();
    }

    public static ReplyKeyboard start() {
        KeyboardRow row = new KeyboardRow();
        KeyboardButton button1 = new KeyboardButton("/start");
        row.add(button1);
        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row))
                .resizeKeyboard(true)
                .build();
    }
}
