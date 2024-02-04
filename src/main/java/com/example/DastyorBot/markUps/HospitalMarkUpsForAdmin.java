package com.example.DastyorBot.markUps;

import com.example.DastyorBot.constant.CommonConstants;
import com.example.DastyorBot.constant.HospitalConstants;
import com.example.DastyorBot.constant.PharmacyConstants;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.LinkedList;
import java.util.List;

public class HospitalMarkUpsForAdmin {
    public static InlineKeyboardMarkup menuForAdmin() {
        List<InlineKeyboardButton> buttons = new LinkedList<>();
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();

        button.setText(HospitalConstants.CREAT);
        button.setCallbackData(HospitalConstants.CREAT);
        buttons.add(button);
        rowList.add(buttons);
        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(HospitalConstants.ADD_PHOTO);
        button.setCallbackData(HospitalConstants.ADD_PHOTO);
        buttons.add(button);
        rowList.add(buttons);

        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(HospitalConstants.ADD_VIDEO);
        button.setCallbackData(HospitalConstants.ADD_VIDEO);
        buttons.add(button);
        rowList.add(buttons);

        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();
        button.setText(HospitalConstants.MAKE_BLOCK);
        button.setCallbackData(HospitalConstants.MAKE_BLOCK);
        buttons.add(button);
        rowList.add(buttons);

        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(HospitalConstants.MAKE_UNBLOCK);
        button.setCallbackData(HospitalConstants.MAKE_UNBLOCK);
        buttons.add(button);
        rowList.add(buttons);

        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(HospitalConstants.GET_ALL);
        button.setCallbackData(HospitalConstants.GET_ALL);
        buttons.add(button);
        rowList.add(buttons);

        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(HospitalConstants.GET_BY_ID);
        button.setCallbackData(HospitalConstants.GET_BY_ID);
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
