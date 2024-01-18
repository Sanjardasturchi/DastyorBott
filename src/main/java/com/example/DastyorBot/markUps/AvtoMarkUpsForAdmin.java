package com.example.DastyorBot.markUps;

import com.example.DastyorBot.constant.AutoConstant;
import com.example.DastyorBot.constant.CommonConstants;
import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.LinkedList;
import java.util.List;

@Component
public class AvtoMarkUpsForAdmin {
    public static final List<String> cities = List.of("Toshkent", "Chirchiq", "Angren", "Sirdaryo", "Bekobod", "Olmaliq",
            "Quvasoy", "Qarshi", "Shahrisabz", "Urganch", "Navoiy", "Jizzax",
            "Termiz", "Margʻilon", "Qoʻqon", "Buxoro", "Beruni",
            "Fargʻona", "Nukus", "Andijon", "Samarqand", "Namangan");
    public static final List<String> timeList = List.of("00:00", "01:00", "02:00", "03:00", "04:00", "05:00",
            "06:00", "07:00", "08:00", "09:00", "10:00", "11:00",
            "12:00", "13:00", "14:00", "15:00", "16:00", "17:00",
            "18:00", "19:00", "20:00", "21:00", "22:00", "23:00");

    public static InlineKeyboardMarkup menuForAdmin() {
        List<InlineKeyboardButton> buttons = new LinkedList<>();
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();

        button.setText(AutoConstant.ADD_AVTO);
        button.setCallbackData(AutoConstant.ADD_AVTO);
        buttons.add(button);
        rowList.add(buttons);
        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(AutoConstant.ADD_PHOTO_TO_AVTO);
        button.setCallbackData(AutoConstant.ADD_PHOTO_TO_AVTO);
        buttons.add(button);
        rowList.add(buttons);
        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(AutoConstant.ADD_VIDEO_TO_AVTO);
        button.setCallbackData(AutoConstant.ADD_VIDEO_TO_AVTO);
        buttons.add(button);
        rowList.add(buttons);
        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(AutoConstant.BLOCK_AVTO);
        button.setCallbackData(AutoConstant.BLOCK_AVTO);
        buttons.add(button);
        rowList.add(buttons);
        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(AutoConstant.ACCTIVE_AVTO);
        button.setCallbackData(AutoConstant.ACCTIVE_AVTO);
        buttons.add(button);
        rowList.add(buttons);
        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(AutoConstant.GET_ALL_AVTO);
        button.setCallbackData(AutoConstant.GET_ALL_AVTO);
        buttons.add(button);
        rowList.add(buttons);
        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(AutoConstant.SHOW_ALL_AVTO);
        button.setCallbackData(AutoConstant.SHOW_ALL_AVTO);
        buttons.add(button);
        rowList.add(buttons);
        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(AutoConstant.SHOW_BY_ID);
        button.setCallbackData(AutoConstant.SHOW_BY_ID);
        buttons.add(button);
        rowList.add(buttons);
        return new InlineKeyboardMarkup(rowList);
    }

    public static InlineKeyboardMarkup choosingCity() {
        List<InlineKeyboardButton> buttons = new LinkedList<>();
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();

        int count = 0;
        for (String city : cities) {
            count++;
            button.setText(city);
            button.setCallbackData(city);

            buttons.add(button);
            button = new InlineKeyboardButton();
            if (count % 2 == 0 || count == cities.size() - 1) {
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

    public static InlineKeyboardMarkup chooseCarType() {
        List<InlineKeyboardButton> buttons = new LinkedList<>();
        List<List<InlineKeyboardButton>> rowList = new LinkedList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();

        button.setText(AutoConstant.CARS);
        button.setCallbackData(AutoConstant.CARS);
        buttons.add(button);
        rowList.add(buttons);
        button = new InlineKeyboardButton();
        buttons = new LinkedList<>();

        button.setText(AutoConstant.TRUCKS);
        button.setCallbackData(AutoConstant.TRUCKS);
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
