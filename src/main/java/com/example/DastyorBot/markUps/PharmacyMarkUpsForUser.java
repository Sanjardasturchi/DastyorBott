package com.example.DastyorBot.markUps;

import com.example.DastyorBot.constant.CommonConstants;
import com.example.DastyorBot.constant.PharmacyConstants;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.LinkedList;
import java.util.List;

public class PharmacyMarkUpsForUser {
    public static InlineKeyboardMarkup menuForUser() {
        List<InlineKeyboardButton> buttons = new LinkedList<>();
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();

        button.setText(PharmacyConstants.PHARMACY_FOR_PEOPLE);
        button.setCallbackData(PharmacyConstants.PHARMACY_FOR_PEOPLE);
        buttons.add(button);
        rowList.add(buttons);
        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(PharmacyConstants.PHARMACY_FOR_ANIMALS);
        button.setCallbackData(PharmacyConstants.PHARMACY_FOR_ANIMALS);
        buttons.add(button);
        rowList.add(buttons);

        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(CommonConstants.BACK);
        button.setCallbackData(CommonConstants.BACK);
        buttons.add(button);
        rowList.add(buttons);

        return new InlineKeyboardMarkup(rowList);
    }
}
