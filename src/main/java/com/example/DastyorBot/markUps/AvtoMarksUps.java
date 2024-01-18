package com.example.DastyorBot.markUps;



import com.example.DastyorBot.constant.AutoConstant;
import com.example.DastyorBot.constant.CommonConstants;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.LinkedList;
import java.util.List;
@Component
public class AvtoMarksUps {

    public static InlineKeyboardMarkup menu() {
        List<InlineKeyboardButton> buttons=new LinkedList<>();
        List<List<InlineKeyboardButton>> rowList=new LinkedList<>();

        InlineKeyboardButton button=new InlineKeyboardButton();

        button.setText(AutoConstant.AVTO_BOUGHT);
        button.setCallbackData(AutoConstant.AVTO_BOUGHT);

        buttons.add(button);
        rowList.add(buttons);

        button=new InlineKeyboardButton();
        buttons=new LinkedList<>();

        button.setText(AutoConstant.AVTO_RENT);
        button.setCallbackData(AutoConstant.AVTO_RENT);

        buttons.add(button);
        rowList.add(buttons);

        button=new InlineKeyboardButton();
        buttons=new LinkedList<>();

        button.setText(CommonConstants.BACK);
        button.setCallbackData(CommonConstants.BACK);

        buttons.add(button);
        rowList.add(buttons);

        return new InlineKeyboardMarkup(rowList);
    }

    public static ReplyKeyboardMarkup changeToBack() {
        KeyboardRow row = new KeyboardRow();

        KeyboardButton button = new KeyboardButton(CommonConstants.BACK);
        row.add(button);


        return ReplyKeyboardMarkup.builder()
                .keyboard(List.of(row))
                .resizeKeyboard(true)
                .build();
    }

    public static InlineKeyboardMarkup avtoChooseMenu() {
        List<InlineKeyboardButton> buttons=new LinkedList<>();
        List<List<InlineKeyboardButton>> rowList=new LinkedList<>();

        InlineKeyboardButton button=new InlineKeyboardButton();

        button.setText(AutoConstant.CARS);
        button.setCallbackData(AutoConstant.CARS);

        buttons.add(button);
        rowList.add(buttons);

        button=new InlineKeyboardButton();
        buttons=new LinkedList<>();

        button.setText(AutoConstant.TRUCKS);
        button.setCallbackData(AutoConstant.TRUCKS);

        buttons.add(button);
        rowList.add(buttons);

        button=new InlineKeyboardButton();
        buttons=new LinkedList<>();

        button.setText(AutoConstant.ALL_CARS);
        button.setCallbackData(AutoConstant.ALL_CARS);

        buttons.add(button);
        rowList.add(buttons);

        button=new InlineKeyboardButton();
        buttons=new LinkedList<>();

        button.setText(CommonConstants.BACK);
        button.setCallbackData(CommonConstants.BACK);

        buttons.add(button);
        rowList.add(buttons);

        return new InlineKeyboardMarkup(rowList);
    }


    public static InlineKeyboardMarkup acceptToCreat() {
        List<InlineKeyboardButton> buttons=new LinkedList<>();
        List<List<InlineKeyboardButton>> rowList=new LinkedList<>();

        InlineKeyboardButton button=new InlineKeyboardButton();

        button.setText(CommonConstants.ACCEPT);
        button.setCallbackData(CommonConstants.ACCEPT);

        buttons.add(button);
        rowList.add(buttons);

        button=new InlineKeyboardButton();
        buttons=new LinkedList<>();

        button.setText(CommonConstants.NON_ACCEPTANCE);
        button.setCallbackData(CommonConstants.NON_ACCEPTANCE);

        buttons.add(button);
        rowList.add(buttons);

        return new InlineKeyboardMarkup(rowList);
    }
}
