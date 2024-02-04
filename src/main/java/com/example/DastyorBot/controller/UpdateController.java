package com.example.DastyorBot.controller;

import com.dark.programs.speech.translator.GoogleTranslate;
import com.example.DastyorBot.constant.*;
import com.example.DastyorBot.dto.AutomobileDTO;
import com.example.DastyorBot.dto.PharmacyDTO;
import com.example.DastyorBot.dto.ProfileDTO;
import com.example.DastyorBot.entity.mediaEntity.AutoMediaEntity;
import com.example.DastyorBot.enums.AcctiveStatus;
import com.example.DastyorBot.enums.MediaType;
import com.example.DastyorBot.enums.ProfileRole;
import com.example.DastyorBot.enums.SelectedPurchaseType;
import com.example.DastyorBot.markUps.*;
import com.example.DastyorBot.service.AutomobileService;
import com.example.DastyorBot.service.GeneralService;
import com.example.DastyorBot.service.PharmacyService;
import com.example.DastyorBot.service.ProfileService;
import com.example.DastyorBot.service.mediaService.AvtoMediaService;
import io.github.nazarovctrl.telegrambotspring.bot.MessageSender;
import io.github.nazarovctrl.telegrambotspring.controller.AbstractUpdateController;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FontFamily;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class UpdateController extends AbstractUpdateController {
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private PharmacyService pharmacyService;
    @Autowired
    private MarkUps markUps;
    @Autowired
    private AutomobileService avtoService;
    @Autowired
    private AvtoMediaService avtoMediaService;

    //  PharmacyConstants.PHARMACY
    @Override
    public void handle(Update update) {
        if (update.hasCallbackQuery()) {
            ProfileDTO currentProfile = profileService.getByChatId(update.getCallbackQuery().getMessage().getChatId().toString());
            if (currentProfile.getRole().equals(ProfileRole.SUPER_ADMIN)) {
                callBQSuperAdmin(update, currentProfile);
            } else if (currentProfile.getRole().equals(ProfileRole.ADMIN)) {
                callBQAdmin(update, currentProfile);
            } else {
                callBQUser(update, currentProfile);
            }
            return;
        } else if (update.hasMessage()) {
            ProfileDTO currentProfile = profileService.getByChatId(update.getMessage().getChatId().toString());
            checkCommon(update, currentProfile);
            try {
                if (currentProfile.getRole().equals(ProfileRole.SUPER_ADMIN)) {
                    messageSuperAdmin(update, currentProfile);
                } else if (currentProfile.getRole().equals(ProfileRole.ADMIN)) {
                    messageAdmin(update, currentProfile);
                } else {
                    messageUser(update, currentProfile);
                }
            } catch (Exception e) {
                e.printStackTrace();
                messageUser(update, currentProfile);
            }
        }
    }

    private void checkCommon(Update update, ProfileDTO currentProfile) {
        if (!update.getMessage().hasText()) {
            return;
        }
        String text = update.getMessage().getText();
        if (text.equals(CommonConstants.language)) {
            if (currentProfile == null) {
                executeMessage(new SendMessage(update.getMessage().getChatId().toString(),
                        "Iltimos oldin ro'yxattan o'ting! \nLütfen önce kayıt olun!\n" +
                                "Пожалуйста, сначала зарегистрируйтесь!\nPlease register first!\n"));
            }
        }
    }

    private Boolean executeMessage(SendMessage sendMessage) {
        try {
            messageSender.execute(sendMessage);
            return true;
        } catch (TelegramApiException e) {
            return false;
        }
    }

    private Boolean executeDeleteMessage(DeleteMessage sendMessage) {
        try {
            messageSender.execute(sendMessage);
            return true;
        } catch (TelegramApiException e) {
            return false;
        }
    }

    private Boolean executeEditMessageText(EditMessageText sendMessage) {
        try {
            messageSender.execute(sendMessage);
            return true;
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        }
    }


    private void messageUser(Update update, ProfileDTO currentProfile) {
        Message message = update.getMessage();
        String chatId = message.getChatId().toString();
        if (currentProfile == null) {
            if (message.hasContact()) {
                ProfileDTO profile = new ProfileDTO();
                Contact contact = message.getContact();
                profile.setName(contact.getFirstName());
                profile.setSurname(contact.getLastName());
                profile.setPhone(contact.getPhoneNumber());
                profile.setChatId(chatId);
                profile.setUsername("@" + update.getMessage().getChat().getUserName());
                profile.setRole(ProfileRole.USER);
                profile.setAcctiveStatus(AcctiveStatus.ACCTIVE);
                profile.setCurrentStep("Menu");
                profile.setCreatedDateTime(LocalDateTime.now());
                if (profileService.save(profile).getId() != null) {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("Muvaffaqqiyatli ro'yxatdan o'tdingiz!");
                    sendMessage.setChatId(chatId);
                    sendMessage.setReplyMarkup(MarkUps.adminButton());
                    executeMessage(sendMessage);
                    SendMessage sendMessage1 = new SendMessage();
                    sendMessage1.setText("Xizmat turini tanlang:");
                    sendMessage1.setChatId(chatId);
                    sendMessage1.setReplyMarkup(MarkUps.menu());
                    executeMessage(sendMessage1);

                } else {
                    executeMessage(new SendMessage(chatId, "Malumot saqlashda muammo, iltimos qayta urinib ko'ring!"));
                }
            } else {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setReplyMarkup(MarkUps.contactButton());
                sendMessage.setText("Iltimos raqamingizni kiritng!");
                executeMessage(sendMessage);
            }
        } else {
            Message message1 = update.getMessage();
            String text = message1.getText();
            if (text.equals("/start")) {
                profileService.changeStep(chatId, "Menu");
                SendMessage editMessageText = new SendMessage();
                editMessageText.setText("Menu");
                editMessageText.setChatId(chatId);
                editMessageText.setReplyMarkup(MarkUps.menu());
                try {
                    messageSender.execute(editMessageText);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
                return;
            }
            if (currentProfile.getCurrentStep().equals(AutoConstant.AVTO_BOUGHT)) {

                if (text.equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId, AutoConstant.AVTO);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setReplyMarkup(AvtoMarksUps.menu());
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(AutoConstant.AVTO);
                    executeMessage(sendMessage);
                } else {
                    int i = 0;
                    List<AutomobileDTO> avtomobileList = avtoService.getAll();
                    for (AutomobileDTO avtomobile : avtomobileList) {
                        if (avtomobile.getEndTime() == null || avtomobile.getAcctiveStatus().equals(AcctiveStatus.BLOCK)) {
                            continue;
                        }
                        if (avtomobile.getModel().contains(text) && avtomobile.getSalaryType().equals(SelectedPurchaseType.SALE)) {
                            i++;
                            String info = "Shahar :: " + avtomobile.getCity()
                                    + "\nTuman :: " + avtomobile.getDistrict()
                                    + "\nNomi :: " + avtomobile.getBrandName() + "  " + avtomobile.getModel()
                                    + "\nQo'shimcha malumot :: " + avtomobile.getDescription()
                                    + "\nNarxi :: " + avtomobile.getPrice() + " $"
                                    + "\nTelefon raqam :: " + avtomobile.getPhone()
                                    + "\nTelegram :: @" + avtomobile.getUsername();
                            List<AutoMediaEntity> avtoMedia = avtoMediaService.getById(avtomobile.getId());
                            List<InputMedia> inputMediaList = new LinkedList<>();

                            if (avtoMedia != null && avtoMedia.size() > 0) {
                                for (AutoMediaEntity avtoMedia1 : avtoMedia) {
                                    if (avtoMedia1.getMediaType().equals(MediaType.PHOTO)) {
                                        InputMedia photo = new InputMediaPhoto();
                                        photo.setMedia(avtoMedia1.getFId());
                                        inputMediaList.add(photo);
                                    } else if (avtoMedia1.getMediaType().equals(MediaType.VIDEO)) {
                                        InputMedia photo = new InputMediaVideo();
                                        photo.setMedia(avtoMedia1.getFId());
                                        inputMediaList.add(photo);
                                    }
                                }

                                SendMediaGroup sendMediaGroup = new SendMediaGroup();
                                sendMediaGroup.setMedias(inputMediaList);
                                sendMediaGroup.setChatId(message.getChatId());

                                try {
                                    messageSender.execute(sendMediaGroup);
                                } catch (TelegramApiException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                info = "Bu avtomobile uchun media fayllar hali yuklanmadi!\n" + info;
                            }
                            executeMessage(new SendMessage(chatId, info));
                        }
                    }
                    if (i == 0) {
                        int j = 0;
                        try {
                            text = text.trim();
                            text = GeneralService.checkMoneyFromTheString(text);
                            Integer initialPayment = Integer.valueOf(text);
                            List<AutomobileDTO> avtomobiles = avtoService.getAll();
                            for (AutomobileDTO avtomobile : avtomobiles) {
                                if (avtomobile.getEndTime() == null || avtomobile.getAcctiveStatus().equals(AcctiveStatus.BLOCK)) {
                                    continue;
                                }
                                if (avtomobile.getPrice() <= initialPayment && avtomobile.getSalaryType().equals(SelectedPurchaseType.SALE)) {
                                    j++;
                                    String info = "Shahar :: " + avtomobile.getCity()
                                            + "\nTuman :: " + avtomobile.getDistrict()
                                            + "\nNomi :: " + avtomobile.getBrandName() + "  " + avtomobile.getModel()
                                            + "\nQo'shimcha malumot :: " + avtomobile.getDescription()
                                            + "\nNarxi :: " + avtomobile.getPrice() + " $"
                                            + "\nTelefon raqam :: " + avtomobile.getPhone()
                                            + "\nTelegram :: @" + avtomobile.getUsername();
                                    List<AutoMediaEntity> avtoMedia = avtoMediaService.getById(avtomobile.getId());
                                    List<InputMedia> inputMediaList = new LinkedList<>();
                                    if (avtoMedia != null && avtoMedia.size() > 0) {
                                        for (AutoMediaEntity avtoMedia1 : avtoMedia) {
                                            if (avtoMedia1.getMediaType().equals(MediaType.PHOTO)) {
                                                InputMedia photo = new InputMediaPhoto();
                                                photo.setMedia(avtoMedia1.getFId());
                                                inputMediaList.add(photo);
                                            } else if (avtoMedia1.getMediaType().equals(MediaType.VIDEO)) {
                                                InputMedia photo = new InputMediaVideo();
                                                photo.setMedia(avtoMedia1.getFId());
                                                inputMediaList.add(photo);
                                            }
                                        }

                                        SendMediaGroup sendMediaGroup = new SendMediaGroup();
                                        sendMediaGroup.setMedias(inputMediaList);
                                        sendMediaGroup.setChatId(message.getChatId());

                                        try {
                                            messageSender.execute(sendMediaGroup);
                                        } catch (TelegramApiException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else {
                                        info = "Bu avtomobile uchun media fayllar hali yuklanmadi!\n" + info;
                                    }
                                    executeMessage(new SendMessage(chatId, info));
                                }
                            }
                        } catch (Exception e) {
                            try {
                                messageSender.execute(new DeleteMessage(chatId, message1.getMessageId()));
                                messageSender.execute(new SendMessage(chatId, "Qidiruv bo'yicha ma'lumot topilmadi!"));
                                return;
                            } catch (TelegramApiException ex) {
                                e.printStackTrace();
                            }

                        }
                        if (j == 0) {
                            try {
                                messageSender.execute(new DeleteMessage(chatId, message1.getMessageId()));
                                executeMessage(new SendMessage(chatId, "Qidiruv bo'yicha ma'lumot topilmadi!"));
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } else if (currentProfile.getCurrentStep().equals(AutoConstant.AVTO_RENT)) {
                if (text.equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId, AutoConstant.AVTO);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setReplyMarkup(AvtoMarksUps.menu());
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(AutoConstant.AVTO);
                    executeMessage(sendMessage);
                } else {
                    int i = 0;
                    List<AutomobileDTO> avtomobileList = avtoService.getAll();
                    for (AutomobileDTO avtomobile : avtomobileList) {
                        if (avtomobile.getEndTime() == null || avtomobile.getAcctiveStatus().equals(AcctiveStatus.BLOCK)) {
                            continue;
                        }
                        if (avtomobile.getModel().equals(text) && avtomobile.getSalaryType().equals(SelectedPurchaseType.RENT)) {
                            i++;
                            String info = "Shahar :: " + avtomobile.getCity()
                                    + "\nTuman :: " + avtomobile.getDistrict()
                                    + "\nNomi :: " + avtomobile.getBrandName() + "  " + avtomobile.getModel()
                                    + "\nQo'shimcha malumot :: " + avtomobile.getDescription()
                                    + "\nBoshlang'ich to'lov :: " + avtomobile.getPrice() + " $"
                                    + "\nTelefon raqam :: " + avtomobile.getPhone()
                                    + "\nTelegram :: @" + avtomobile.getUsername();
                            List<AutoMediaEntity> avtoMedia = avtoMediaService.getById(avtomobile.getId());
                            List<InputMedia> inputMediaList = new LinkedList<>();
                            if (avtoMedia != null && avtoMedia.size() > 0) {
                                for (AutoMediaEntity avtoMedia1 : avtoMedia) {
                                    if (avtoMedia1.getMediaType().equals(MediaType.PHOTO)) {
                                        InputMedia photo = new InputMediaPhoto();
                                        photo.setMedia(avtoMedia1.getFId());
                                        inputMediaList.add(photo);
                                    } else if (avtoMedia1.getMediaType().equals(MediaType.VIDEO)) {
                                        InputMedia photo = new InputMediaVideo();
                                        photo.setMedia(avtoMedia1.getFId());
                                        inputMediaList.add(photo);
                                    }
                                }

                                SendMediaGroup sendMediaGroup = new SendMediaGroup();
                                sendMediaGroup.setMedias(inputMediaList);
                                sendMediaGroup.setChatId(message.getChatId());

                                try {
                                    messageSender.execute(sendMediaGroup);
                                } catch (TelegramApiException e) {
                                    throw new RuntimeException(e);
                                }
                            } else {
                                info = "Bu avtomobile uchun media fayllar hali yuklanmadi!\n" + info;
                            }
                            executeMessage(new SendMessage(chatId, info));
                        }
                    }
                    if (i == 0) {
                        int j = 0;
                        try {
                            text = text.trim();
                            text = GeneralService.checkMoneyFromTheString(text);
                            Integer initialPayment = Integer.valueOf(text);
                            List<AutomobileDTO> avtomobiles = avtoService.getAll();
                            for (AutomobileDTO avtomobile : avtomobiles) {
                                if (avtomobile.getEndTime() == null || avtomobile.getAcctiveStatus().equals(AcctiveStatus.BLOCK)) {
                                    continue;
                                }
                                if (avtomobile.getPrice() <= initialPayment && avtomobile.getSalaryType().equals(SelectedPurchaseType.RENT)) {
                                    j++;
                                    String info = "Shahar :: " + avtomobile.getCity()
                                            + "\nTuman :: " + avtomobile.getDistrict()
                                            + "\nNomi :: " + avtomobile.getBrandName() + "  " + avtomobile.getModel()
                                            + "\nQo'shimcha malumot :: " + avtomobile.getDescription()
                                            + "\nBoshlang'ich to'lov :: " + avtomobile.getPrice() + " $"
                                            + "\nTelefon raqam :: " + avtomobile.getPhone()
                                            + "\nTelegram :: @" + avtomobile.getUsername();
                                    List<AutoMediaEntity> avtoMedia = avtoMediaService.getById(avtomobile.getId());
                                    List<InputMedia> inputMediaList = new LinkedList<>();
                                    if (avtoMedia != null && avtoMedia.size() > 0) {
                                        for (AutoMediaEntity avtoMedia1 : avtoMedia) {
                                            if (avtoMedia1.getMediaType().equals(MediaType.PHOTO)) {
                                                InputMedia photo = new InputMediaPhoto();
                                                photo.setMedia(avtoMedia1.getFId());
                                                inputMediaList.add(photo);
                                            } else if (avtoMedia1.getMediaType().equals(MediaType.VIDEO)) {
                                                InputMedia photo = new InputMediaVideo();
                                                photo.setMedia(avtoMedia1.getFId());
                                                inputMediaList.add(photo);
                                            }
                                        }

                                        SendMediaGroup sendMediaGroup = new SendMediaGroup();
                                        sendMediaGroup.setMedias(inputMediaList);
                                        sendMediaGroup.setChatId(message.getChatId());

                                        try {
                                            messageSender.execute(sendMediaGroup);
                                        } catch (TelegramApiException e) {
                                            throw new RuntimeException(e);
                                        }
                                    } else {
                                        info = "Bu avtomobile uchun media fayllar hali yuklanmadi!\n" + info;
                                    }
                                    executeMessage(new SendMessage(chatId, info));
                                }
                            }
                        } catch (Exception e) {
                            executeDeleteMessage(new DeleteMessage(chatId, message1.getMessageId()));
                            executeMessage(new SendMessage(chatId, "Qidiruv bo'yicha ma'lumot topilmadi!"));
                            return;
                        }
                        if (j == 0) {
                            executeDeleteMessage(new DeleteMessage(chatId, message1.getMessageId()));
                            executeMessage(new SendMessage(chatId, "Qidiruv bo'yicha ma'lumot topilmadi!"));
                        }
                    }
                }
            } else if (text.equals(CommonConstants.BACK) && (currentProfile.getCurrentStep().equals(PharmacyConstants.PHARMACY_FOR_PEOPLE)
                    || currentProfile.getCurrentStep().equals(PharmacyConstants.PHARMACY_FOR_ANIMALS))) {
                profileService.changeStep(chatId, PharmacyConstants.PHARMACY);
                SendMessage sendMessage = new SendMessage(chatId, PharmacyConstants.PHARMACY);
                sendMessage.setReplyMarkup(PharmacyMarkUpsForUser.menuForUser());
                executeMessage(sendMessage);
            }
        }
    }

    private void messageAdmin(Update update, ProfileDTO currentProfile) {

        Message message = update.getMessage();

        String chatId = message.getChatId().toString();
        if (currentProfile == null || currentProfile.getRole().equals(ProfileRole.USER)) {
            executeMessage(new SendMessage(chatId, "Kechirasiz bu botga faqat adminalar murojat qila oladi!"));
        } else if (currentProfile.getRole().equals(ProfileRole.ADMIN)) {
            if (message.hasLocation()) {
                Location location = message.getLocation();
                Double latitude = location.getLatitude();
                Double longitude = location.getLongitude();
                if (currentProfile.getCurrentStep().equals(PharmacyConstants.ENTERING_LOCATION)) {
                    pharmacyService.setLocation(currentProfile.getChangingElementId(), latitude, longitude);
                    profileService.changeStep(chatId, PharmacyConstants.PHARMACY);
                    executeMessage(new SendMessage(chatId, "Yangi e'lon qo'shish muvaffaqiyatli tugatildi!"));
                    SendMessage sendMessage = new SendMessage(chatId, PharmacyConstants.PHARMACY);
                    sendMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.menuForAdmin());
                    executeMessage(sendMessage);
                    return;
                }
            }

            if (message.hasText() && message.getText().equals("/start")) {
                profileService.changeStep(chatId, "Menu");
                SendMessage sendMessage = new SendMessage(chatId, "Menu");
                sendMessage.setReplyMarkup(MarkUps.menu());
                executeMessage(sendMessage);
                return;
            }

            if (currentProfile.getCurrentStep().equals(AutoConstant.ADD_PHOTO_TO_AVTO)) {
                if (message.hasText()) {
                    String text1 = message.getText();
                    if (text1.equals(CommonConstants.BACK)) {
                        profileService.changeStep(chatId, AutoConstant.AVTO);
                        SendMessage sendMessage = new SendMessage(chatId, AutoConstant.AVTO);
                        sendMessage.setReplyMarkup(AvtoMarkUpsForAdmin.menuForAdmin());
                        executeMessage(new SendMessage(chatId, "Avtomobilga surat joylash bekor qilindi!"));
                        executeMessage(sendMessage);
                        return;
                    } else {
                        String res = GeneralService.checkMoneyFromTheString(text1);
                        Integer avtoId = Integer.valueOf(res);
                        List<AutomobileDTO> avtomobiles = avtoService.getAll();
                        if (avtomobiles != null && avtomobiles.size() > 0) {
                            for (AutomobileDTO avtomobile : avtomobiles) {
                                if (avtomobile.getId().equals(avtoId)) {
                                    String attention = "";
                                    attention = "Siz rusumi: " + avtomobile.getBrandName() + " " + avtomobile.getModel()
                                            + "\n Egasining telefon raqami: " + avtomobile.getPhone()
                                            + "\n Username: @" + avtomobile.getUsername() + " bo'lgan avtomobilega surat joylamoqdasiz!" +
                                            "\nJoylamoqchi bo'lgan suratingizni jo'nating!";
                                    currentProfile.setChangingElementId(avtomobile.getId());
                                    executeMessage(new SendMessage(chatId, attention));
                                    profileService.changingElementId(avtomobile.getId(), chatId);
                                    profileService.changeStep(chatId, AutoConstant.SENDING_PHOTO_TO_AUTO);
                                    return;
                                }
                            }
                        }
                    }
                }
            } else if (currentProfile.getCurrentStep().equals(AutoConstant.ADD_VIDEO_TO_AVTO)) {
                if (message.hasText()) {
                    String text1 = message.getText();
                    if (text1.equals(CommonConstants.BACK)) {
                        profileService.changeStep(chatId, AutoConstant.AVTO);
                        SendMessage sendMessage = new SendMessage(chatId, AutoConstant.AVTO);
                        sendMessage.setReplyMarkup(AvtoMarkUpsForAdmin.menuForAdmin());
                        executeMessage(new SendMessage(chatId, "Avtomobilga video joylash bekor qilindi!"));
                        executeMessage(sendMessage);
                    } else {
                        String res = GeneralService.checkMoneyFromTheString(text1);
                        Integer avtoId = Integer.valueOf(res);
                        List<AutomobileDTO> avtomobiles = avtoService.getAll();
                        if (avtomobiles != null && avtomobiles.size() > 0) {
                            for (AutomobileDTO avtomobile : avtomobiles) {
                                if (avtomobile.getId().equals(avtoId)) {
                                    String attention = "";
                                    attention = "Siz rusumi: " + avtomobile.getBrandName() + " " + avtomobile.getModel()
                                            + "\n Egasining telefon raqami: " + avtomobile.getPhone()
                                            + "\n Username: @" + avtomobile.getUsername() + " bo'lgan avtomobilega video joylamoqdasiz!" +
                                            "\nJoylamoqchi bo'lgan video ingizni jo'nating!";
                                    currentProfile.setChangingElementId(avtomobile.getId());
                                    executeMessage(new SendMessage(chatId, attention));
                                    profileService.changingElementId(avtomobile.getId(), chatId);
                                    profileService.changeStep(chatId, AutoConstant.SENDING_VIDEO_TO_AUTO);
                                    return;
                                }
                            }
                        }
                    }
                }
            } else if (message.hasText() && (currentProfile.getCurrentStep().equals(AutoConstant.SENDING_PHOTO_TO_AUTO)
                    || currentProfile.getCurrentStep().equals(AutoConstant.SENDING_VIDEO_TO_AUTO))) {
                if (message.hasText()) {
                    String text1 = message.getText();
                    if (text1.equals(CommonConstants.BACK)) {
                        profileService.changeStep(chatId, AutoConstant.AVTO);
                        SendMessage sendMessage = new SendMessage(chatId, AutoConstant.AVTO);
                        sendMessage.setReplyMarkup(AvtoMarkUpsForAdmin.menuForAdmin());
                        if (currentProfile.getCurrentStep().equals(AutoConstant.SENDING_PHOTO_TO_AUTO)) {
                            executeMessage(new SendMessage(chatId, "Avtomobilga surat joylash bekor qilindi!"));
                        } else {
                            executeMessage(new SendMessage(chatId, "Avtomobilga video joylash bekor qilindi!"));
                        }
                        executeMessage(sendMessage);
                    }
                }
            } else if (message.hasPhoto()) {
                if (currentProfile.getChangingElementId() != null) {
                    if (currentProfile.getCurrentStep().equals(AutoConstant.SENDING_PHOTO_TO_AUTO)) {
                        List<PhotoSize> photo = message.getPhoto();
                        PhotoSize photoSize1 = photo.get(0);
                        for (PhotoSize photoSize : photo) {
                            if (photoSize.getFileId().length() > photoSize1.getFileId().length()) {
                                photoSize1 = photoSize;
                            }
                        }
                        String fileId = photoSize1.getFileId();
                        AutoMediaEntity avtoFoto = new AutoMediaEntity();
                        avtoFoto.setFId(fileId);
                        avtoFoto.setCarId(currentProfile.getChangingElementId());
                        avtoFoto.setMediaType(MediaType.PHOTO);
                        if (avtoMediaService.save(avtoFoto)) {
                            executeMessage(new SendMessage(chatId, "Rasm muvaffaqiyatli saqlandi!"));
                        } else {
                            executeMessage(new SendMessage(chatId, "Rasm saqlashda muammo!"));
                        }
                    } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.ENTERING_PHOTO)) {
                        if (message.hasText()) {
                            if (message.getText().equals(CommonConstants.BACK)) {
                                profileService.changeStep(chatId, PharmacyConstants.PHARMACY);
                                SendMessage sendMessage = new SendMessage(chatId, PharmacyConstants.PHARMACY);
                                sendMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.menuForAdmin());
                                executeMessage(sendMessage);
                                return;
                            }
                        }
                        if (message.hasPhoto()) {
                            List<PhotoSize> photo = message.getPhoto();
                            pharmacyService.saveMedia(photo.get(photo.size() - 1).getFileId(), MediaType.PHOTO, currentProfile.getChangingElementId());
                            executeMessage(new SendMessage(chatId, translate("uz", currentProfile.getLanguage().name(), "Rasm muvaffaqiyatli saqlandi!!")));
                        } else {
                            executeMessage(new SendMessage(chatId, translate("uz", currentProfile.getLanguage().name(), "Iltimos rasm jo'nating! Yoki ((Ortga)) tugmasini bosib ortga qayting!")));
                        }
                    }
                }
            } else if (message.hasVideo()) {
                if (currentProfile.getChangingElementId() != null) {
                    if (currentProfile.getCurrentStep().equals(AutoConstant.SENDING_VIDEO_TO_AUTO)) {
                        Video video = message.getVideo();
                        String fileId = video.getFileId();
                        AutoMediaEntity avtoFoto = new AutoMediaEntity();
                        avtoFoto.setFId(fileId);
                        avtoFoto.setCarId(currentProfile.getChangingElementId());
                        avtoFoto.setMediaType(MediaType.VIDEO);
                        if (avtoMediaService.save(avtoFoto)) {
                            executeMessage(new SendMessage(chatId, "Video muvaffaqiyatli saqlandi!"));
                        } else {
                            executeMessage(new SendMessage(chatId, "Video saqlashda muammo!"));
                        }
                    } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.ENTERING_VIDEO)) {
                        if (message.hasText()) {
                            if (message.getText().equals(CommonConstants.BACK)) {
                                profileService.changeStep(chatId, PharmacyConstants.PHARMACY);
                                SendMessage sendMessage = new SendMessage(chatId, PharmacyConstants.PHARMACY);
                                sendMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.menuForAdmin());
                                executeMessage(sendMessage);
                                return;
                            }
                        }
                        if (message.hasVideo()) {
                            Video video = message.getVideo();
                            pharmacyService.saveMedia(video.getFileId(), MediaType.VIDEO, currentProfile.getChangingElementId());
                            executeMessage(new SendMessage(chatId, translate("uz", currentProfile.getLanguage().name(), "Video muvaffaqiyatli saqlandi!!")));
                        } else {
                            executeMessage(new SendMessage(chatId, translate("uz", currentProfile.getLanguage().name(), "Iltimos video jo'nating! Yoki ((Ortga)) tugmasini bosib ortga qayting!")));
                        }
                    }
                }
            } else if (currentProfile.getCurrentStep().equals(AutoConstant.ENTERING_BRAND_NAME)) {
                if (message.getText().equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId, AutoConstant.CHOOSING_SALARY_TYPE);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(AutoConstant.CHOOSING_SALARY_TYPE);
                    sendMessage.setReplyMarkup(AvtoMarkUpsForAdmin.chooseCarType());
                    executeMessage(sendMessage);
                    return;
                }
                avtoService.setBrand(currentProfile.getChangingElementId(), message.getText());
                profileService.changeStep(chatId, AutoConstant.ENTERING_MODEL);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(AutoConstant.ENTERING_MODEL);
                executeMessage(sendMessage);
            } else if (currentProfile.getCurrentStep().equals(AutoConstant.ENTERING_MODEL)) {
                if (message.getText().equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId, AutoConstant.ENTERING_BRAND_NAME);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(AutoConstant.ENTERING_BRAND_NAME);
                    executeMessage(sendMessage);
                    return;
                }
                avtoService.setModel(currentProfile.getChangingElementId(), message.getText());
                profileService.changeStep(chatId, AutoConstant.ENTERING_PRICE);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(AutoConstant.ENTERING_PRICE);
                executeMessage(sendMessage);
            } else if (currentProfile.getCurrentStep().equals(AutoConstant.ENTERING_PRICE)) {
                if (message.getText().equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId, AutoConstant.ENTERING_MODEL);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(AutoConstant.ENTERING_MODEL);
                    executeMessage(sendMessage);
                    return;
                }
                Boolean result = avtoService.setPrice(currentProfile.getChangingElementId(), message.getText());
                if (result) {
                    profileService.changeStep(chatId, AutoConstant.ENTERING_PHONE);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(AutoConstant.ENTERING_PHONE);
                    executeMessage(sendMessage);
                } else {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("Iltimos faqat son kiriting!\nYoki Ortga tugmasini bosing!");
                    executeMessage(sendMessage);
                }
            } else if (currentProfile.getCurrentStep().equals(AutoConstant.ENTERING_PHONE)) {
                if (message.getText().equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId, AutoConstant.ENTERING_PRICE);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(AutoConstant.ENTERING_PRICE);
                    executeMessage(sendMessage);
                    return;
                }
                String phoneNumber = message.getText();
                if (!phoneNumber.startsWith("+998")) {
                    executeMessage(new SendMessage(chatId, "Telefon raqam noto'g'ri kiritildi!"));
                    return;
                } else {
                    Pattern p = Pattern.compile("^\\d{12}$");
                    if (!p.matcher(phoneNumber.substring(1)).matches()) {
                        executeMessage(new SendMessage(chatId, "Telefon raqam noto'g'ri kiritildi!"));
                        return;
                    }
                }
                avtoService.setPhone(currentProfile.getChangingElementId(), message.getText());
                profileService.changeStep(chatId, AutoConstant.ENTERING_USERNAME);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(AutoConstant.ENTERING_USERNAME);
                executeMessage(sendMessage);
            } else if (currentProfile.getCurrentStep().equals(AutoConstant.ENTERING_USERNAME)) {
                if (message.getText().equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId, AutoConstant.ENTERING_PHONE);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(AutoConstant.ENTERING_PHONE);
                    executeMessage(sendMessage);
                    return;
                }
                avtoService.setUsername(currentProfile.getChangingElementId(), message.getText());
                profileService.changeStep(chatId, AutoConstant.ENTERING_DESCRIPTION);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(AutoConstant.ENTERING_DESCRIPTION);
                executeMessage(sendMessage);
            } else if (currentProfile.getCurrentStep().equals(AutoConstant.ENTERING_DESCRIPTION)) {
                if (message.getText().equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId, AutoConstant.ENTERING_USERNAME);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(AutoConstant.ENTERING_USERNAME);
                    executeMessage(sendMessage);
                    return;
                }
                avtoService.setDescription(currentProfile.getChangingElementId(), message.getText());
                profileService.changeStep(chatId, AutoConstant.ENTERING_DISTRICT);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(AutoConstant.ENTERING_DISTRICT);
                sendMessage.setReplyMarkup(MarkUps.backWithNextButtons());
                executeMessage(sendMessage);
            } else if (currentProfile.getCurrentStep().equals(AutoConstant.ENTERING_DISTRICT)) {
                if (message.getText().equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId, AutoConstant.ENTERING_PHONE);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(AutoConstant.ENTERING_PHONE);
                    executeMessage(sendMessage);
                    return;
                } else if (message.getText().equals(CommonConstants.NEXT)) {
                    profileService.changeStep(chatId, AutoConstant.ENTERING_START_TIME);
                    avtoService.setDistrict(currentProfile.getChangingElementId(), "Barchasi");
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(AutoConstant.ENTERING_START_TIME);
                    sendMessage.setReplyMarkup(AvtoMarkUpsForAdmin.times());
                    executeMessage(sendMessage);
                    return;
                }
                avtoService.setDistrict(currentProfile.getChangingElementId(), message.getText());
                profileService.changeStep(chatId, AutoConstant.ENTERING_START_TIME);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(AutoConstant.ENTERING_START_TIME);
                sendMessage.setReplyMarkup(AvtoMarkUpsForAdmin.times());
                executeMessage(sendMessage);
            } else if (currentProfile.getCurrentStep().equals(AutoConstant.BLOCKING_AVTO) ||
                    currentProfile.getCurrentStep().equals(AutoConstant.UNBLOCKING_AVTO)) {
                if (message.hasText()) {
                    if (currentProfile.getCurrentStep().equals(AutoConstant.BLOCKING_AVTO)) {
                        executeMessage(new SendMessage(chatId, avtoService.changeAcctiveStatus(message.getText(), AcctiveStatus.BLOCK)));
                    } else {
                        executeMessage(new SendMessage(chatId, avtoService.changeAcctiveStatus(message.getText(), AcctiveStatus.ACCTIVE)));
                    }
                } else {
                    executeMessage(new SendMessage(chatId, "Xato ma'lumot kiritildi iltimos e'tiborli bo'ling!"));
                }
            } else if (currentProfile.getCurrentStep().equals(AutoConstant.GETTING_BY_ID)) {
                try {
                    String id = GeneralService.checkMoneyFromTheString(message.getText());
                    Integer autoId = Integer.valueOf(id);
                    AutomobileDTO automobile = avtoService.getById(autoId);
                    if (automobile == null) {
                        executeMessage(new SendMessage(chatId, "Kiritilgan id ga tegishli e'lon topilmadi!"));
                        return;
                    }
                    List<AutoMediaEntity> autoMediaEntityList = avtoMediaService.getById(autoId);
                    String autoInfo = "Shahar :: " + automobile.getCity()
                            + "\nTuman :: " + automobile.getDistrict()
                            + "\nNomi :: " + automobile.getBrandName() + "  " + automobile.getModel()
                            + "\nQo'shimcha malumot :: " + automobile.getDescription()
                            + "\nNarxi :: " + automobile.getPrice() + " $"
                            + "\nTelefon raqam :: " + automobile.getPhone()
                            + "\nTelegram :: @" + automobile.getUsername();
                    if (autoMediaEntityList == null || autoMediaEntityList.size() == 0) {
                        autoInfo = "Ushbu e'lon uchun media fayllar hali yuklanmagan\n" + autoInfo;
                    } else {
                        List<InputMedia> inputMediaList = new LinkedList<>();
                        for (AutoMediaEntity avtoMedia1 : autoMediaEntityList) {
                            if (avtoMedia1.getMediaType().equals(MediaType.PHOTO)) {
                                InputMedia photo = new InputMediaPhoto();
                                photo.setMedia(avtoMedia1.getFId());
                                inputMediaList.add(photo);
                            } else if (avtoMedia1.getMediaType().equals(MediaType.VIDEO)) {
                                InputMedia photo = new InputMediaVideo();
                                photo.setMedia(avtoMedia1.getFId());
                                inputMediaList.add(photo);
                            }
                        }
                        SendMediaGroup sendMediaGroup = new SendMediaGroup();
                        sendMediaGroup.setMedias(inputMediaList);
                        sendMediaGroup.setChatId(message.getChatId());
                        executeMediaGroup(sendMediaGroup);
                    }
                    executeMessage(new SendMessage(chatId, autoInfo));
                } catch (Exception e) {
                    executeMessage(new SendMessage(chatId, "Id noto'g'ri kiritildi!"));
                }
            } else if (message.getText().equals(CommonConstants.BACK)
                    && (currentProfile.getCurrentStep().equals(PharmacyConstants.PHARMACY_FOR_PEOPLE)
                    || currentProfile.getCurrentStep().equals(PharmacyConstants.PHARMACY_FOR_ANIMALS))) {
                profileService.changeStep(chatId, PharmacyConstants.PHARMACY);
                SendMessage sendMessage = new SendMessage(chatId, PharmacyConstants.PHARMACY);
                sendMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.menuForAdmin());
                executeMessage(sendMessage);
            } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.ENTERING_USERNAME)) {
                String text = message.getText();
                if (text.equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId, PharmacyConstants.ENTERING_END_TIME);
                    SendMessage sendMessage = new SendMessage(chatId, PharmacyConstants.ENTERING_END_TIME);
                    sendMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.times());
                    executeMessage(sendMessage);
                    return;
                }
                profileService.changeStep(chatId, PharmacyConstants.ENTERING_PHONE);
                pharmacyService.setUsername(currentProfile.getChangingElementId(), text);
                executeMessage(new SendMessage(chatId, PharmacyConstants.ENTERING_PHONE));
                return;
            } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.ENTERING_PHONE)) {
                String text = message.getText();
                if (text.equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId, PharmacyConstants.ENTERING_USERNAME);
                    executeMessage(new SendMessage(chatId, PharmacyConstants.ENTERING_USERNAME));
                    return;
                }
                String phoneNumber = message.getText();
                if (!phoneNumber.startsWith("+998")) {
                    executeMessage(new SendMessage(chatId, "Telefon raqam noto'g'ri kiritildi!"));
                    return;
                } else {
                    Pattern p = Pattern.compile("^\\d{12}$");
                    if (!p.matcher(phoneNumber.substring(1)).matches()) {
                        executeMessage(new SendMessage(chatId, "Telefon raqam noto'g'ri kiritildi!"));
                        return;
                    }
                }
                profileService.changeStep(chatId, PharmacyConstants.ENTERING_PHARMACY_NAME);
                pharmacyService.setPhone(currentProfile.getChangingElementId(), text);
                executeMessage(new SendMessage(chatId, PharmacyConstants.ENTERING_PHARMACY_NAME));
                return;
            } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.ENTERING_PHARMACY_NAME)) {
                String text = message.getText();
                if (text.equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId, PharmacyConstants.ENTERING_PHONE);
                    executeMessage(new SendMessage(chatId, PharmacyConstants.ENTERING_PHONE));
                    return;
                }

                profileService.changeStep(chatId, PharmacyConstants.ENTERING_DESCRIPTION);
                pharmacyService.setName(currentProfile.getChangingElementId(), text);
                executeMessage(new SendMessage(chatId, PharmacyConstants.ENTERING_DESCRIPTION));
                return;
            } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.ENTERING_DESCRIPTION)) {
                String text = message.getText();
                if (text.equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId, PharmacyConstants.ENTERING_PHARMACY_NAME);
                    executeMessage(new SendMessage(chatId, PharmacyConstants.ENTERING_PHARMACY_NAME));
                    return;
                }
                profileService.changeStep(chatId, PharmacyConstants.ENTERING_LOCATION);
                pharmacyService.setDescription(currentProfile.getChangingElementId(), text);
                executeMessage(new SendMessage(chatId, PharmacyConstants.ENTERING_LOCATION));
                return;
            } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.ENTERING_LOCATION)) {
                String text = message.getText();
                if (text.equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId, PharmacyConstants.ENTERING_DESCRIPTION);
                    executeMessage(new SendMessage(chatId, PharmacyConstants.ENTERING_DESCRIPTION));
                    return;
                }
                executeMessage(new SendMessage(chatId, PharmacyConstants.ENTERING_LOCATION));
                return;
            } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.ADDING_PHOTO)) {
                String text = message.getText();
                if (text.equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId, PharmacyConstants.PHARMACY);
                    SendMessage sendMessage = new SendMessage(chatId, PharmacyConstants.PHARMACY);
                    sendMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.menuForAdmin());
                    executeMessage(sendMessage);
                    return;
                }
                try {
                    PharmacyDTO dto = pharmacyService.findById(Integer.valueOf(GeneralService.checkMoneyFromTheString(text)));
                    if (dto == null) {
                        executeMessage(new SendMessage(chatId, PharmacyConstants.WRONG_ID));
                        return;
                    }
                    String translate = GoogleTranslate.translate("uz", currentProfile.getLanguage().name(), "Malumoti:") +
                            GoogleTranslate.translate("uz", currentProfile.getLanguage().name(), " Nomi :: " + dto.getPharmacyName() +
                                    " Turi :: " + dto.getPharmacyType() +
                                    " Telefon raqami :: " + dto.getPhone() +
                                    " Telegram nomi :: " + dto.getUsername()) + " " +
                            GoogleTranslate.translate("uz", currentProfile.getLanguage().name(), " Rasmni jo'nating");
//                    String translate = GoogleTranslate.translate("uz", currentProfile.getLanguage().name(), info);
//                    String[] ees = translate.split(" / t ");
                    executeMessage(new SendMessage(chatId, translate));
                    profileService.changeStep(chatId, PharmacyConstants.ENTERING_PHOTO);
                    return;

                } catch (Exception e) {
                    e.printStackTrace();
                    executeMessage(new SendMessage(chatId, PharmacyConstants.WRONG_ID));
                    return;
                }
            } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.ENTERING_PHOTO)
                    || currentProfile.getCurrentStep().equals(PharmacyConstants.ENTERING_VIDEO)) {
                if (message.hasText()) {
                    if (message.getText().equals(CommonConstants.BACK)) {
                        profileService.changeStep(chatId, PharmacyConstants.PHARMACY);
                        SendMessage sendMessage = new SendMessage(chatId, PharmacyConstants.PHARMACY);
                        sendMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.menuForAdmin());
                        executeMessage(sendMessage);
                        return;
                    }
                }
            } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.ADDING_VIDEO)) {
                String text = message.getText();
                if (text.equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId, PharmacyConstants.PHARMACY);
                    SendMessage sendMessage = new SendMessage(chatId, PharmacyConstants.PHARMACY);
                    sendMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.menuForAdmin());
                    executeMessage(sendMessage);
                    return;
                }
                try {
                    PharmacyDTO dto = pharmacyService.findById(Integer.valueOf(GeneralService.checkMoneyFromTheString(text)));
                    if (dto == null) {
                        executeMessage(new SendMessage(chatId, PharmacyConstants.WRONG_ID));
                        return;
                    }
                    String translate = GoogleTranslate.translate("uz", currentProfile.getLanguage().name(), "Malumoti:") +
                            GoogleTranslate.translate("uz", currentProfile.getLanguage().name(), " Nomi :: " + dto.getPharmacyName() +
                                    " Turi :: " + dto.getPharmacyType() +
                                    " Telefon raqami :: " + dto.getPhone() +
                                    " Telegram nomi :: " + dto.getUsername()) + " " +
                            GoogleTranslate.translate("uz", currentProfile.getLanguage().name(), " Rasmni jo'nating");
                    executeMessage(new SendMessage(chatId, translate));
                    profileService.changeStep(chatId, PharmacyConstants.ENTERING_VIDEO);
                    return;

                } catch (Exception e) {
                    e.printStackTrace();
                    executeMessage(new SendMessage(chatId, PharmacyConstants.WRONG_ID));
                    return;
                }
            } else if (message.getText().equals(CommonConstants.BACK)
                    && (currentProfile.getCurrentStep().equals(PharmacyConstants.MAKING_BLOCK)
                    || currentProfile.getCurrentStep().equals(PharmacyConstants.MAKING_UNBLOCK)
                    || currentProfile.getCurrentStep().equals(PharmacyConstants.GETTING_BY_ID))) {
                profileService.changeStep(chatId, PharmacyConstants.PHARMACY);
                SendMessage sendMessage = new SendMessage(chatId, PharmacyConstants.PHARMACY);
                sendMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.menuForAdmin());
                executeMessage(sendMessage);
                return;
            } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.MAKING_BLOCK)) {
                try {
                    Integer id= Integer.valueOf(GeneralService.checkMoneyFromTheString(message.getText()));
                    PharmacyDTO dto = pharmacyService.findById(id);
                    if (dto != null) {
                        pharmacyService.changeStatus(id,AcctiveStatus.BLOCK);
                    }
                    executeMessage(new SendMessage(chatId,translate("uz",currentProfile.getLanguage().name(),"Operatsiya muvaffaqiyatli tugatildi!")));
                }catch (Exception e){
                    executeMessage(new SendMessage(chatId,translate("uz",currentProfile.getLanguage().name(),"Id ga tegishli malumot mavjud emas!")));
                    return;
                }
            } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.MAKING_UNBLOCK)) {
                try {
                    Integer id= Integer.valueOf(GeneralService.checkMoneyFromTheString(message.getText()));
                    PharmacyDTO dto = pharmacyService.findById(id);
                    if (dto != null) {
                        pharmacyService.changeStatus(id,AcctiveStatus.ACCTIVE);
                    }
                    executeMessage(new SendMessage(chatId,translate("uz",currentProfile.getLanguage().name(),"Operatsiya muvaffaqiyatli tugatildi!")));
                }catch (Exception e){
                    executeMessage(new SendMessage(chatId,translate("uz",currentProfile.getLanguage().name(),"Id ga tegishli malumot mavjud emas!")));
                    return;
                }
            } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.GETTING_BY_ID)) {
                try {
                    Integer id= Integer.valueOf(GeneralService.checkMoneyFromTheString(message.getText()));
                    PharmacyDTO dto = pharmacyService.findById(id);
                    String translate = GoogleTranslate.translate("uz", currentProfile.getLanguage().name(), "Malumoti:") +
                            GoogleTranslate.translate("uz", currentProfile.getLanguage().name(), " Nomi :: " + dto.getPharmacyName() + " " +
                                    " Turi :: " + dto.getPharmacyType() + " " +
                                    " Telefon raqami :: " + dto.getPhone() + " " +
                                    " Telegram nomi :: " + dto.getUsername() + " "+
                            " Aktivlik :: "+dto.getAcctiveStatus() + " "+
                            " Qo'shimcha :: "+dto.getDescription());
                    executeMessage(new SendMessage(chatId,translate));
                }catch (Exception e){
                    executeMessage(new SendMessage(chatId,translate("uz",currentProfile.getLanguage().name(),"Id ga tegishli malumot mavjud emas!")));
                    return;
                }
            }
        }
    }

    private void executeMediaGroup(SendMediaGroup sendMediaGroup) {
        try {
            messageSender.execute(sendMediaGroup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void messageSuperAdmin(Update update, ProfileDTO currentProfile) {
    }

    private void callBQUser(Update update, ProfileDTO currentProfile) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();

        CallbackQuery callbackQuery = update.getCallbackQuery();
        String data = callbackQuery.getData();
        if (currentProfile.getCurrentStep().equals("Menu")) {
            if (data.equals(AutoConstant.AVTO)) {
                avtoMenu(update, chatId, currentProfile);
                return;
            } else if (data.equals(AutoServiceConstants.AVTO_SERVICE)) {

            } else if (data.equals(AutoConstant.AVTO_SPARE_PARTS_STORES)) {

            } else if (data.equals(HomeConstants.HOME)) {

            } else if (data.equals(HomeServiceConstants.HOME_SERVICE)) {

            } else if (data.equals(PharmacyConstants.PHARMACY)) {
                pharmacyMenu(update, chatId, currentProfile);
            } else if (data.equals(HospitalConstants.HOSPITAL)) {

            }
        } else if (currentProfile.getCurrentStep().equals(AutoConstant.AVTO)) {
            if (data.equals(AutoConstant.AVTO_BOUGHT)) {
                autoBought(update, chatId, currentProfile);
            } else if (data.equals(AutoConstant.AVTO_RENT)) {
                autoRent(update, chatId, currentProfile);
            } else if (data.equals(CommonConstants.BACK)) {
                comeToMenu(update, chatId, currentProfile);
            }
        } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.PHARMACY)) {//             todo                      Pharmacy
            if (data.equals(CommonConstants.BACK)) {
                profileService.changeStep(chatId, CommonConstants.MENU);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(CommonConstants.MENU);
                editMessage.setReplyMarkup(MarkUps.menu());
                editMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
                executeEditMessageText(editMessage);
                return;
            } else if (data.equals(PharmacyConstants.PHARMACY_FOR_PEOPLE)) {
                profileService.changeStep(chatId, PharmacyConstants.PHARMACY_FOR_PEOPLE);
                executeDeleteMessage(new DeleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId()));
                SendMessage sendMessage = new SendMessage(chatId, PharmacyConstants.SENDING_LOCATION);
                sendMessage.setReplyMarkup(MarkUps.backButton());
                executeMessage(sendMessage);
            } else if (data.equals(PharmacyConstants.PHARMACY_FOR_ANIMALS)) {
                profileService.changeStep(chatId, PharmacyConstants.PHARMACY_FOR_ANIMALS);
                executeDeleteMessage(new DeleteMessage(chatId, update.getCallbackQuery().getMessage().getMessageId()));
                SendMessage sendMessage = new SendMessage(chatId, PharmacyConstants.SENDING_LOCATION);
                sendMessage.setReplyMarkup(MarkUps.backButton());
                executeMessage(sendMessage);
            }

        }
    }

    private void pharmacyMenu(Update update, String chatId, ProfileDTO currentProfile) {
        profileService.changeStep(chatId, PharmacyConstants.PHARMACY);
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setText(PharmacyConstants.PHARMACY);
        editMessageText.setReplyMarkup(PharmacyMarkUpsForUser.menuForUser());
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        executeEditMessageText(editMessageText);
    }

    private void callBQSuperAdmin(Update update, ProfileDTO currentProfile) {
    }

    private void callBQAdmin(Update update, ProfileDTO currentProfile) {
        Message message = update.getCallbackQuery().getMessage();
        String chatId = message.getChatId().toString();
        String text = message.getText();
        String data = update.getCallbackQuery().getData();
        if (text.equals("Menu") && currentProfile.getCurrentStep().equals(CommonConstants.MENU)) {
            if (data.equals(PharmacyConstants.PHARMACY)) {
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setText(PharmacyConstants.PHARMACY);
                editMessageText.setReplyMarkup(PharmacyMarkUpsForAdmin.menuForAdmin());
                editMessageText.setMessageId(message.getMessageId());
                if (executeEditMessageText(editMessageText)) {
                    profileService.changeStep(chatId, PharmacyConstants.PHARMACY);
                }
                return;
            } else if (data.equals(HospitalConstants.HOSPITAL)) {
                profileService.changeStep(chatId, HospitalConstants.HOSPITAL);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setText(HospitalConstants.HOSPITAL);
                editMessageText.setReplyMarkup(HospitalMarkUpsForAdmin.menuForAdmin());
                editMessageText.setMessageId(message.getMessageId());
                executeEditMessageText(editMessageText);
            } else if (data.equals(AutoConstant.AVTO)) {
                profileService.changeStep(chatId, AutoConstant.AVTO);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setText(AutoConstant.AVTO);
                editMessageText.setReplyMarkup(AvtoMarkUpsForAdmin.menuForAdmin());
                editMessageText.setMessageId(message.getMessageId());
                executeEditMessageText(editMessageText);
            }
        } else if (text.equals(AutoConstant.AVTO) && currentProfile.getCurrentStep().equals(AutoConstant.AVTO)) {
            if (data.equals(CommonConstants.BACK)) {
                profileService.changeStep(chatId, CommonConstants.MENU);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setText(CommonConstants.MENU);
                editMessageText.setReplyMarkup(MarkUps.menu());
                editMessageText.setMessageId(message.getMessageId());
                executeEditMessageText(editMessageText);
                return;
            } else if (data.equals(AutoConstant.ADD_AVTO)) {
                profileService.changeStep(chatId, CommonConstants.ACCEPT_TO_CREAT_NEW_ANNOUNCEMENT);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setText(CommonConstants.ACCEPT_TO_CREAT_NEW_ANNOUNCEMENT);
                editMessageText.setReplyMarkup(AvtoMarksUps.acceptToCreat());
                editMessageText.setMessageId(message.getMessageId());
                executeEditMessageText(editMessageText);
                return;
            } else if (data.equals(AutoConstant.ADD_PHOTO_TO_AVTO)) {
                profileService.changeStep(chatId, AutoConstant.ADD_PHOTO_TO_AVTO);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setText(AutoConstant.ADD_PHOTO_TO_AVTO);
                editMessageText.setMessageId(message.getMessageId());

                executeEditMessageText(editMessageText);
                SendMessage sendMessage = new SendMessage(chatId, "Avtomobil Id sini kiriting");
                sendMessage.setReplyMarkup(MarkUps.backButton());
                executeMessage(sendMessage);
            } else if (data.equals(AutoConstant.ADD_VIDEO_TO_AVTO)) {
                profileService.changeStep(chatId, AutoConstant.ADD_VIDEO_TO_AVTO);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setText(AutoConstant.ADD_VIDEO_TO_AVTO);
                editMessageText.setMessageId(message.getMessageId());
                executeEditMessageText(editMessageText);
                SendMessage sendMessage = new SendMessage(chatId, "Avtomobil Id sini kiriting");
                sendMessage.setReplyMarkup(MarkUps.backButton());
                executeMessage(sendMessage);
            } else if (data.equals(AutoConstant.BLOCK_AVTO)) {
                profileService.changeStep(chatId, AutoConstant.BLOCKING_AVTO);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setText(AutoConstant.BLOCKING_AVTO);
                editMessageText.setReplyMarkup(MarkUps.inlineBackButton());
                editMessageText.setMessageId(message.getMessageId());
                executeEditMessageText(editMessageText);
            } else if (data.equals(AutoConstant.UNBLOCK_AVTO)) {
                profileService.changeStep(chatId, AutoConstant.UNBLOCKING_AVTO);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setText(AutoConstant.UNBLOCKING_AVTO);
                editMessageText.setReplyMarkup(MarkUps.inlineBackButton());
                editMessageText.setMessageId(message.getMessageId());
                executeEditMessageText(editMessageText);
            } else if (data.equals(AutoConstant.SHOW_BY_ID)) {
                profileService.changeStep(chatId, AutoConstant.GETTING_BY_ID);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(AutoConstant.GETTING_BY_ID);
                editMessage.setReplyMarkup(MarkUps.inlineBackButton());
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
            } else if (data.equals(AutoConstant.GET_ALL_AVTO)) {
                List<AutomobileDTO> users = avtoService.getAll();
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet autoSheet = workbook.createSheet("auto");

                XSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cellStyle.setFillPattern(FillPatternType.DIAMONDS);
                cellStyle.setFillForegroundColor(IndexedColors.AQUA.index);
                XSSFFont font = workbook.createFont();
                font.setBold(true);
                font.setFamily(FontFamily.ROMAN);
                cellStyle.setFont(font);

                XSSFRow row1 = autoSheet.createRow(0);

                XSSFCell cellId = row1.createCell(0);
                cellId.setCellStyle(cellStyle);
                cellId.setCellValue("Id");

                XSSFCell cellChatId = row1.createCell(1);
                cellChatId.setCellStyle(cellStyle);
                cellChatId.setCellValue("Admin ChatId");

                XSSFCell cellName = row1.createCell(2);
                cellName.setCellStyle(cellStyle);
                cellName.setCellValue("Mashina turi");

                XSSFCell cellSurname = row1.createCell(3);
                cellSurname.setCellStyle(cellStyle);
                cellSurname.setCellValue("Sotuv turi");

                XSSFCell cellCity = row1.createCell(4);
                cellCity.setCellStyle(cellStyle);
                cellCity.setCellValue("Shahar");

                XSSFCell cellRole = row1.createCell(5);
                cellRole.setCellStyle(cellStyle);
                cellRole.setCellValue("Mashina nomi");

                XSSFCell cellPhone = row1.createCell(6);
                cellPhone.setCellStyle(cellStyle);
                cellPhone.setCellValue("Telefon");


                XSSFCell cellCurrentStep = row1.createCell(7);
                cellCurrentStep.setCellStyle(cellStyle);
                cellCurrentStep.setCellValue("Ma'lumot");

                XSSFCell cellAcctive = row1.createCell(8);
                cellAcctive.setCellStyle(cellStyle);
                cellAcctive.setCellValue("Aktivlik");

                XSSFCell cellCreatedDate = row1.createCell(9);
                cellCreatedDate.setCellStyle(cellStyle);
                cellCreatedDate.setCellValue("Ro'yxattan o'tgan vaqti");

                XSSFCell cellPrice = row1.createCell(10);
                cellPrice.setCellStyle(cellStyle);
                cellPrice.setCellValue("Narx/Boshlang'ich narx");


                int i = 0;
                for (
                        AutomobileDTO user : users) {
                    XSSFRow row = autoSheet.createRow(++i);
                    XSSFCell cell = row.createCell(0);
                    cell.setCellValue(user.getId());
                    XSSFCell cell1 = row.createCell(1);
                    cell1.setCellValue(user.getAdCreatorChatId());
                    XSSFCell cell2 = row.createCell(2);
                    cell2.setCellValue(user.getCarType().name());
                    XSSFCell cell3 = row.createCell(3);
                    cell3.setCellValue(user.getSalaryType().name());
                    XSSFCell cell4 = row.createCell(4);
                    cell4.setCellValue(user.getCity() + " " + user.getDistrict());
                    XSSFCell cell5 = row.createCell(5);
                    cell5.setCellValue(user.getBrandName() + " " + user.getModel());
                    XSSFCell cell6 = row.createCell(6);
                    cell6.setCellValue(user.getPhone());
                    XSSFCell cell7 = row.createCell(7);
                    cell7.setCellValue(user.getDescription());
                    XSSFCell cell8 = row.createCell(8);
                    cell8.setCellValue(user.getAcctiveStatus().name());
                    XSSFCell cell9 = row.createCell(9);
                    cell9.setCellValue(user.getCreatedDateTime());
                    XSSFCell cell10 = row.createCell(10);
                    if (user.getPrice() != null) {
                        cell10.setCellValue(user.getPrice());
                    } else {
                        cell10.setCellValue("Narxi kiritilmagan");
                    }
                }
                try {
                    workbook.write(new FileOutputStream("C:\\Projects\\DastyorBot\\src\\main\\resources\\autoInfo.xlsx"));
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SendDocument sendDocument = new SendDocument(chatId, new InputFile(new File("C:\\Projects\\DastyorBot\\src\\main\\resources\\autoInfo.xlsx")));
                executeDocument(sendDocument);
                executeDeleteMessage(new DeleteMessage(chatId, message.getMessageId()));
                SendMessage sendMessage = new SendMessage(chatId, AutoConstant.AVTO);
                sendMessage.setReplyMarkup(AvtoMarkUpsForAdmin.menuForAdmin());
                executeMessage(sendMessage);
            }
        } else if (currentProfile.getCurrentStep().equals(AutoConstant.BLOCKING_AVTO) ||
                currentProfile.getCurrentStep().equals(AutoConstant.UNBLOCKING_AVTO)) {
            if (data.equals(CommonConstants.BACK)) {
                profileService.changeStep(chatId, AutoConstant.AVTO);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setMessageId(message.getMessageId());
                if (currentProfile.getCurrentStep().equals(AutoConstant.BLOCKING_AVTO)) {
                    editMessageText.setText("E'lonni block qilish operatsiyasi bekor qilindi");
                } else {
                    editMessageText.setText("E'lonni blockdan chiqarish operatsiyasi bekor qilindi");
                }
                executeEditMessageText(editMessageText);
                SendMessage sendMessage = new SendMessage(chatId, AutoConstant.AVTO);
                sendMessage.setReplyMarkup(AvtoMarkUpsForAdmin.menuForAdmin());
                executeMessage(sendMessage);
                return;
            }
        } else if (data.equals(CommonConstants.ACCEPT)
                && currentProfile.getCurrentStep().equals(CommonConstants.ACCEPT_TO_CREAT_NEW_ANNOUNCEMENT)) {
            profileService.changeStep(chatId, AutoConstant.CHOOSING_CITY);

            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(chatId);
            editMessageText.setText("Yangi e'lon id raqami: " + avtoService.createAuto(currentProfile.getChatId(), new AutomobileDTO()).getId());
            editMessageText.setMessageId(message.getMessageId());
            executeEditMessageText(editMessageText);

            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(AutoConstant.CHOOSING_CITY);
            sendMessage.setReplyMarkup(AvtoMarkUpsForAdmin.choosingCity());
            executeMessage(sendMessage);
            return;
        } else if (data.equals(CommonConstants.NON_ACCEPTANCE)
                && currentProfile.getCurrentStep().equals(CommonConstants.ACCEPT_TO_CREAT_NEW_ANNOUNCEMENT)) {
            profileService.changeStep(chatId, AutoConstant.AVTO);
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(chatId);
            editMessageText.setText(AutoConstant.AVTO);
            editMessageText.setReplyMarkup(AvtoMarkUpsForAdmin.menuForAdmin());
            editMessageText.setMessageId(message.getMessageId());
            executeEditMessageText(editMessageText);
        } else if (message.getText().equals(AutoConstant.CHOOSING_CITY)
                && currentProfile.getCurrentStep().equals(AutoConstant.CHOOSING_CITY)) {
            if (data.equals(CommonConstants.BACK)) {
                profileService.changeStep(chatId, AutoConstant.AVTO);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(AutoConstant.AVTO);
                editMessage.setReplyMarkup(AvtoMarkUpsForAdmin.menuForAdmin());
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            }
            avtoService.setCity(currentProfile.getChangingElementId(), data);
            profileService.changeStep(chatId, AutoConstant.CHOOSING_CAR_TYPE);

            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(chatId);
            editMessageText.setText(AutoConstant.CHOOSING_CAR_TYPE);
            editMessageText.setReplyMarkup(AvtoMarkUpsForAdmin.chooseCarType());
            editMessageText.setMessageId(message.getMessageId());
            executeEditMessageText(editMessageText);
            return;
        } else if (message.getText().equals(AutoConstant.CHOOSING_CAR_TYPE)
                && currentProfile.getCurrentStep().equals(AutoConstant.CHOOSING_CAR_TYPE)) {
            if (data.equals(CommonConstants.BACK)) {
                profileService.changeStep(chatId, AutoConstant.CHOOSING_CITY);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(AutoConstant.CHOOSING_CITY);
                editMessage.setReplyMarkup(AvtoMarkUpsForAdmin.choosingCity());
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            }
            avtoService.setCarType(currentProfile.getChangingElementId(), data);
            profileService.changeStep(chatId, AutoConstant.CHOOSING_SALARY_TYPE);

            EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(chatId);
            editMessage.setText(AutoConstant.CHOOSING_SALARY_TYPE);
            editMessage.setReplyMarkup(MarkUps.choosingSalary());
            editMessage.setMessageId(message.getMessageId());
            executeEditMessageText(editMessage);
            return;
        } else if (message.getText().equals(AutoConstant.CHOOSING_SALARY_TYPE)
                && currentProfile.getCurrentStep().equals(AutoConstant.CHOOSING_SALARY_TYPE)) {
            if (data.equals(CommonConstants.BACK)) {
                profileService.changeStep(chatId, AutoConstant.CHOOSING_CAR_TYPE);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(AutoConstant.CHOOSING_CAR_TYPE);
                editMessage.setReplyMarkup(AvtoMarkUpsForAdmin.chooseCarType());
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            }
            avtoService.setSalaryType(currentProfile.getChangingElementId(), data);
            profileService.changeStep(chatId, AutoConstant.ENTERING_BRAND_NAME);

            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId);
            deleteMessage.setMessageId(message.getMessageId());

            SendMessage sendMessage = new SendMessage(chatId, AutoConstant.ENTERING_BRAND_NAME);
            sendMessage.setReplyMarkup(MarkUps.backButton());
            executeMessage(sendMessage);
            executeDeleteMessage(deleteMessage);
            return;
        } else if (message.getText().equals(AutoConstant.ENTERING_START_TIME)
                && currentProfile.getCurrentStep().equals(AutoConstant.ENTERING_START_TIME)) {
            if (data.equals(CommonConstants.BACK)) {
                profileService.changeStep(chatId, AutoConstant.ENTERING_DISTRICT);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(AutoConstant.ENTERING_DISTRICT);
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            }
            avtoService.setStartTime(currentProfile.getChangingElementId(), data);
            profileService.changeStep(chatId, AutoConstant.ENTERING_END_TIME);

            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(chatId);
            editMessageText.setText(AutoConstant.ENTERING_END_TIME);
            editMessageText.setReplyMarkup(AvtoMarkUpsForAdmin.times());
            editMessageText.setMessageId(message.getMessageId());
            executeEditMessageText(editMessageText);
        } else if (message.getText().equals(AutoConstant.ENTERING_END_TIME)
                && currentProfile.getCurrentStep().equals(AutoConstant.ENTERING_END_TIME)) {
            if (data.equals(CommonConstants.BACK)) {
                profileService.changeStep(chatId, AutoConstant.ENTERING_START_TIME);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(AutoConstant.ENTERING_START_TIME);
                editMessage.setReplyMarkup(AvtoMarkUpsForAdmin.times());
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            }
            avtoService.setEndTime(currentProfile.getChangingElementId(), data);
            profileService.changeStep(chatId, AutoConstant.AVTO);
            executeDeleteMessage(new DeleteMessage(chatId, message.getMessageId()));
            SendMessage sendMessage = new SendMessage(chatId, AutoConstant.SUCCESSFULLY_FINISHED);
            sendMessage.setReplyMarkup(MarkUps.start());
            executeMessage(sendMessage);
            SendMessage sendMessage1 = new SendMessage(chatId, AutoConstant.AVTO);
            sendMessage1.setReplyMarkup(AvtoMarkUpsForAdmin.menuForAdmin());
            executeMessage(sendMessage1);
            return;
        } else if (currentProfile.getCurrentStep().equals(AutoConstant.GETTING_BY_ID) && data.equals(CommonConstants.BACK)) {
            profileService.changeStep(chatId, AutoConstant.AVTO);
            executeDeleteMessage(new DeleteMessage(chatId, message.getMessageId()));

            SendMessage sendMessage = new SendMessage(chatId, AutoConstant.AVTO);
            sendMessage.setReplyMarkup(AvtoMarkUpsForAdmin.menuForAdmin());
            executeMessage(sendMessage);
        } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.PHARMACY)) {//             todo                      Pharmacy
            if (data.equals(CommonConstants.BACK)) {
                profileService.changeStep(chatId, CommonConstants.MENU);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(CommonConstants.MENU);
                editMessage.setReplyMarkup(MarkUps.menu());
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            } else if (data.equals(PharmacyConstants.CREAT)) {
                profileService.changeStep(chatId, PharmacyConstants.ACCEPT_TO_CREAT);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(PharmacyConstants.ACCEPT_TO_CREAT);
                editMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.acceptToCreat());
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            } else if (data.equals(PharmacyConstants.ADD_PHOTO)) {
                profileService.changeStep(chatId, PharmacyConstants.ADDING_PHOTO);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(PharmacyConstants.ADDING_PHOTO);
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            } else if (data.equals(PharmacyConstants.ADD_VIDEO)) {
                profileService.changeStep(chatId, PharmacyConstants.ADDING_VIDEO);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(PharmacyConstants.ADDING_VIDEO);
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            } else if (data.equals(PharmacyConstants.MAKE_BLOCK)) {
                profileService.changeStep(chatId, PharmacyConstants.MAKING_BLOCK);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(PharmacyConstants.MAKING_BLOCK);
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            } else if (data.equals(PharmacyConstants.MAKE_UNBLOCK)) {// todo////////////////////////////////////////////////////////////////////////////////////////
                profileService.changeStep(chatId, PharmacyConstants.MAKING_UNBLOCK);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(PharmacyConstants.MAKING_UNBLOCK);
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            } else if (data.equals(PharmacyConstants.GET_ALL)) {
                List<PharmacyDTO> pharmacys = pharmacyService.getAll();
                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet autoSheet = workbook.createSheet("auto");

                XSSFCellStyle cellStyle = workbook.createCellStyle();
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cellStyle.setFillPattern(FillPatternType.DIAMONDS);
                cellStyle.setFillForegroundColor(IndexedColors.AQUA.index);
                XSSFFont font = workbook.createFont();
                font.setBold(true);
                font.setFamily(FontFamily.ROMAN);
                cellStyle.setFont(font);

                XSSFRow row1 = autoSheet.createRow(0);

                XSSFCell cellId = row1.createCell(0);
                cellId.setCellStyle(cellStyle);
                cellId.setCellValue("Id");

                XSSFCell cellChatId = row1.createCell(1);
                cellChatId.setCellStyle(cellStyle);
                cellChatId.setCellValue(translate("uz", currentProfile.getLanguage().name(), "Dorixona turi"));

                XSSFCell cellName = row1.createCell(2);
                cellName.setCellStyle(cellStyle);
                cellName.setCellValue(translate("uz", currentProfile.getLanguage().name(), "Ish boshlanish vaqti"));

                XSSFCell cellSurname = row1.createCell(3);
                cellSurname.setCellStyle(cellStyle);
                cellSurname.setCellValue(translate("uz", currentProfile.getLanguage().name(), "Ish tugash vaqti"));

                XSSFCell cellCity = row1.createCell(4);
                cellCity.setCellStyle(cellStyle);
                cellCity.setCellValue(translate("uz", currentProfile.getLanguage().name(), "Telegram nomi"));

                XSSFCell cellRole = row1.createCell(5);
                cellRole.setCellStyle(cellStyle);
                cellRole.setCellValue(translate("uz", currentProfile.getLanguage().name(), "Telefon raqami"));

                XSSFCell cellPhone = row1.createCell(6);
                cellPhone.setCellStyle(cellStyle);
                cellPhone.setCellValue(translate("uz", currentProfile.getLanguage().name(), "Dorixona nomi"));


                XSSFCell cellCurrentStep = row1.createCell(7);
                cellCurrentStep.setCellStyle(cellStyle);
                cellCurrentStep.setCellValue(translate("uz", currentProfile.getLanguage().name(), "Malumot"));

                XSSFCell cellAcctive = row1.createCell(8);
                cellAcctive.setCellStyle(cellStyle);
                cellAcctive.setCellValue(translate("uz", currentProfile.getLanguage().name(), "Aktivlik"));

                XSSFCell cellCreatedDate = row1.createCell(9);
                cellCreatedDate.setCellStyle(cellStyle);
                cellCreatedDate.setCellValue(translate("uz", currentProfile.getLanguage().name(), "Ro'yxattan o'tgan vaqti"));

                int i = 0;
                for (PharmacyDTO pharmacy : pharmacys) {
                    XSSFRow row = autoSheet.createRow(++i);
                    XSSFCell cell = row.createCell(0);
                    cell.setCellValue(pharmacy.getId());
                    XSSFCell cell12 = row.createCell(1);
                    cell12.setCellValue(pharmacy.getPharmacyType());
                    XSSFCell cell1 = row.createCell(2);
                    cell1.setCellValue(pharmacy.getStartTime().toString());
                    XSSFCell cell2 = row.createCell(3);
                    cell2.setCellValue(pharmacy.getEndTime().toString());
                    XSSFCell cell3 = row.createCell(4);
                    cell3.setCellValue(pharmacy.getUsername());
                    XSSFCell cell4 = row.createCell(5);
                    cell4.setCellValue(pharmacy.getPhone());
                    XSSFCell cell5 = row.createCell(6);
                    cell5.setCellValue(pharmacy.getPharmacyName());
                    XSSFCell cell6 = row.createCell(7);
                    cell6.setCellValue(pharmacy.getDescription());
                    XSSFCell cell7 = row.createCell(8);
                    cell7.setCellValue(pharmacy.getAcctiveStatus().name());
                    XSSFCell cell9 = row.createCell(9);
                    cell9.setCellValue(pharmacy.getCreatedDateTime().toString());
                }
                try {
                    workbook.write(new FileOutputStream("C:\\Projects\\DastyorBot\\src\\main\\resources\\pharmacyInfo.xlsx"));
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                SendDocument sendDocument = new SendDocument(chatId, new InputFile(new File("C:\\Projects\\DastyorBot\\src\\main\\resources\\pharmacyInfo.xlsx")));
                executeDocument(sendDocument);
                executeDeleteMessage(new DeleteMessage(chatId, message.getMessageId()));
                SendMessage sendMessage = new SendMessage(chatId, PharmacyConstants.PHARMACY);
                sendMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.menuForAdmin());
                executeMessage(sendMessage);

                return;
            } else if (data.equals(PharmacyConstants.GET_BY_ID)) {
                profileService.changeStep(chatId, PharmacyConstants.GETTING_BY_ID);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(PharmacyConstants.GETTING_BY_ID);
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            }

        } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.ACCEPT_TO_CREAT)) {
            if (data.equals(PharmacyConstants.ACCEPT)) {
                profileService.changeStep(chatId, PharmacyConstants.ENTERING_TYPE);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                Integer id = pharmacyService.creat();
                profileService.changingElementId(id, chatId);
                editMessage.setText("Yangi e'lon yaratish tasdiqlandi!\nE'lon id raqami :: " + id);
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(PharmacyConstants.ENTERING_TYPE);
                sendMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.typeOfPharmacy());
                executeMessage(sendMessage);
                return;
            } else if (data.equals(PharmacyConstants.NON_ACCEPT)) {
                profileService.changeStep(chatId, PharmacyConstants.PHARMACY);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(PharmacyConstants.PHARMACY);
                editMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.menuForAdmin());
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            }
        } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.ENTERING_TYPE)) {
            if (data.equals(CommonConstants.BACK)) {
                profileService.changeStep(chatId, PharmacyConstants.PHARMACY);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(PharmacyConstants.PHARMACY);
                editMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.menuForAdmin());
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            } else {
                profileService.changeStep(chatId, PharmacyConstants.ENTERING_START_TIME);
                pharmacyService.setType(data, currentProfile.getChangingElementId());
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(PharmacyConstants.ENTERING_START_TIME);
                editMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.times());
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            }
        } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.ENTERING_START_TIME)) {
            if (data.equals(CommonConstants.BACK)) {
                profileService.changeStep(chatId, PharmacyConstants.ENTERING_TYPE);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(PharmacyConstants.ENTERING_TYPE);
                editMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.typeOfPharmacy());
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            } else {
                profileService.changeStep(chatId, PharmacyConstants.ENTERING_END_TIME);
                pharmacyService.setStartTime(data, currentProfile.getChangingElementId());
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(PharmacyConstants.ENTERING_END_TIME);
                editMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.times());
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            }
        } else if (currentProfile.getCurrentStep().equals(PharmacyConstants.ENTERING_END_TIME)) {
            if (data.equals(CommonConstants.BACK)) {
                profileService.changeStep(chatId, PharmacyConstants.ENTERING_START_TIME);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(PharmacyConstants.ENTERING_START_TIME);
                editMessage.setReplyMarkup(PharmacyMarkUpsForAdmin.times());
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            } else {
                profileService.changeStep(chatId, PharmacyConstants.ENTERING_USERNAME);
                pharmacyService.setEndTime(data, currentProfile.getChangingElementId());
                executeDeleteMessage(new DeleteMessage(chatId, message.getMessageId()));
                SendMessage sendMessage = new SendMessage(chatId, PharmacyConstants.ENTERING_USERNAME);
                sendMessage.setReplyMarkup(MarkUps.backButton());
                executeMessage(sendMessage);
                return;
            }
        } else if (currentProfile.getCurrentStep().equals(HospitalConstants.HOSPITAL)) {
            if (data.equals(CommonConstants.BACK)) {
                profileService.changeStep(chatId, CommonConstants.MENU);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(CommonConstants.MENU);
                editMessage.setReplyMarkup(MarkUps.menu());
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            }
        }
    }

    private void executeDocument(SendDocument sendDocument) {
        try {
            messageSender.execute(sendDocument);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void avtoMenu(Update update, String chatId, ProfileDTO currentProfile) {
        profileService.changeStep(currentProfile.getChatId(), AutoConstant.AVTO);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setText(AutoConstant.AVTO);
        editMessageText.setReplyMarkup(AvtoMarksUps.menu());
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        executeEditMessageText(editMessageText);
    }

    private void autoBought(Update update, String chatId, ProfileDTO currentProfile) {
        profileService.changeStep(currentProfile.getChatId(), AutoConstant.AVTO_BOUGHT);
        profileService.changePurchaseType(SelectedPurchaseType.SALE, chatId);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageText.setText(AutoConstant.BOUGHT);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Yoki (Ortga) tugmasini bosib ortga qayting!");
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(MarkUps.backButton());
        executeEditMessageText(editMessageText);
        executeMessage(sendMessage);
    }

    private void autoRent(Update update, String chatId, ProfileDTO currentProfile) {
        profileService.changeStep(currentProfile.getChatId(), AutoConstant.AVTO_RENT);
        profileService.changePurchaseType(SelectedPurchaseType.RENT, chatId);

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageText.setText(AutoConstant.RENT);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Yoki (Ortga) tugmasini bosib ortga qayting!");
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(MarkUps.backButton());
        executeEditMessageText(editMessageText);
        executeMessage(sendMessage);
    }

    private void comeToMenu(Update update, String chatId, ProfileDTO currentProfile) {
        profileService.changeStep("Menu", currentProfile.getChatId());

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setReplyMarkup(MarkUps.menu());
        editMessageText.setText("Menu");
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        executeEditMessageText(editMessageText);
    }

    private static String translate(String fromLang, String toLang, String text) {
        try {
            return GoogleTranslate.translate(fromLang, toLang, text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}