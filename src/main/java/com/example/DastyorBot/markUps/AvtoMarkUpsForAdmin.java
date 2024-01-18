package com.example.DastyorBot.markUps;

import com.example.DastyorBot.constant.AutoConstant;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.LinkedList;
import java.util.List;
@Component
public class AvtoMarkUpsForAdmin {
    public static InlineKeyboardMarkup menuForAdmin() {
        List<InlineKeyboardButton> buttons=new LinkedList<>();
        List<List<InlineKeyboardButton>> rowList=new LinkedList<>();

        InlineKeyboardButton button=new InlineKeyboardButton();

        button.setText(AutoConstant.ADD_AVTO);
        button.setCallbackData(AutoConstant.ADD_AVTO);

        buttons.add(button);
        rowList.add(buttons);

        button=new InlineKeyboardButton();
        buttons=new LinkedList<>();

        button.setText(AutoConstant.ADD_PHOTO_TO_AVTO);
        button.setCallbackData(AutoConstant.ADD_PHOTO_TO_AVTO);

        buttons.add(button);
        rowList.add(buttons);

        button=new InlineKeyboardButton();
        buttons=new LinkedList<>();

        button.setText(AutoConstant.ADD_VIDEO_TO_AVTO);
        button.setCallbackData(AutoConstant.ADD_VIDEO_TO_AVTO);

        buttons.add(button);
        rowList.add(buttons);

        button=new InlineKeyboardButton();
        buttons=new LinkedList<>();

        button.setText(AutoConstant.BLOCK_AVTO);
        button.setCallbackData(AutoConstant.BLOCK_AVTO);

        buttons.add(button);
        rowList.add(buttons);

        button=new InlineKeyboardButton();
        buttons=new LinkedList<>();

        button.setText(AutoConstant.ACCTIVE_AVTO);
        button.setCallbackData(AutoConstant.ACCTIVE_AVTO);

        buttons.add(button);
        rowList.add(buttons);

        button=new InlineKeyboardButton();
        buttons=new LinkedList<>();

        button.setText(AutoConstant.GET_ALL_AVTO);
        button.setCallbackData(AutoConstant.GET_ALL_AVTO);

        buttons.add(button);
        rowList.add(buttons);

        button=new InlineKeyboardButton();
        buttons=new LinkedList<>();

        button.setText(AutoConstant.SHOW_ALL_AVTO);
        button.setCallbackData(AutoConstant.SHOW_ALL_AVTO);

        buttons.add(button);
        rowList.add(buttons);

        button=new InlineKeyboardButton();
        buttons=new LinkedList<>();

        button.setText(AutoConstant.SHOW_BY_ID);
        button.setCallbackData(AutoConstant.SHOW_BY_ID);

        buttons.add(button);
        rowList.add(buttons);

        return new InlineKeyboardMarkup(rowList);
    }
}
