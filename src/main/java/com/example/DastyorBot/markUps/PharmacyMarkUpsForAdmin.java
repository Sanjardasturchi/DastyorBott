package com.example.DastyorBot.markUps;

import com.example.DastyorBot.constant.CommonConstants;
import com.example.DastyorBot.constant.PharmacyConstants;
import com.example.DastyorBot.enums.PharmacyType;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.LinkedList;
import java.util.List;

import static com.example.DastyorBot.markUps.AvtoMarkUpsForAdmin.timeList;

public class PharmacyMarkUpsForAdmin {
    public static InlineKeyboardMarkup menuForAdmin() {
        List<InlineKeyboardButton> buttons = new LinkedList<>();
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();

        button.setText(PharmacyConstants.CREAT);
        button.setCallbackData(PharmacyConstants.CREAT);
        buttons.add(button);
        rowList.add(buttons);
        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(PharmacyConstants.ADD_PHOTO);
        button.setCallbackData(PharmacyConstants.ADD_PHOTO);
        buttons.add(button);
        rowList.add(buttons);

        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(PharmacyConstants.ADD_VIDEO);
        button.setCallbackData(PharmacyConstants.ADD_VIDEO);
        buttons.add(button);
        rowList.add(buttons);

        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();
        button.setText(PharmacyConstants.MAKE_BLOCK);
        button.setCallbackData(PharmacyConstants.MAKE_BLOCK);
        buttons.add(button);
        rowList.add(buttons);

        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(PharmacyConstants.MAKE_UNBLOCK);
        button.setCallbackData(PharmacyConstants.MAKE_UNBLOCK);
        buttons.add(button);
        rowList.add(buttons);

        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(PharmacyConstants.GET_ALL);
        button.setCallbackData(PharmacyConstants.GET_ALL);
        buttons.add(button);
        rowList.add(buttons);

        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(PharmacyConstants.GET_BY_ID);
        button.setCallbackData(PharmacyConstants.GET_BY_ID);
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

    public static InlineKeyboardMarkup acceptToCreat() {
        List<InlineKeyboardButton> buttons = new LinkedList<>();
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();

        button.setText(PharmacyConstants.ACCEPT);
        button.setCallbackData(PharmacyConstants.ACCEPT);
        buttons.add(button);
        rowList.add(buttons);
        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(PharmacyConstants.NON_ACCEPT);
        button.setCallbackData(PharmacyConstants.NON_ACCEPT);
        buttons.add(button);
        rowList.add(buttons);
        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup typeOfPharmacy() {
        List<InlineKeyboardButton> buttons = new LinkedList<>();
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();

        button.setText("Dorixona  \uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC67\u200D\uD83D\uDC66");
        button.setCallbackData(PharmacyConstants.PHARMACY_FOR_PERSON);
        buttons.add(button);
        rowList.add(buttons);
        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText("Veterinar dorixonasi  \uD83D\uDC08");
        button.setCallbackData(PharmacyConstants.PHARMACY_FOR_ANIMAL);
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
    public static InlineKeyboardMarkup times() {
        List<InlineKeyboardButton> buttons = new LinkedList<>();
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();

        int count = 0;
        // timeList(8:00,9:00,10:00)
        for (String time : timeList) {
            count++;
            button.setText(time);
            button.setCallbackData(time);

            buttons.add(button);
            button = new InlineKeyboardButton();
            if (count % 3 == 0 || count == timeList.size()) {
                rowList.add(buttons);
                buttons = new LinkedList<>();
            }
        }
        button.setText(CommonConstants.BACK);
        button.setCallbackData(CommonConstants.BACK);
        buttons.add(button);
        rowList.add(buttons);
        return new InlineKeyboardMarkup(rowList);
    }
}
