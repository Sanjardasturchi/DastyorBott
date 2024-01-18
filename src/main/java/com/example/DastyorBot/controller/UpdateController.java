package com.example.DastyorBot.controller;

import com.example.DastyorBot.constant.*;
import com.example.DastyorBot.dto.AutomobileDTO;
import com.example.DastyorBot.dto.ProfileDTO;
import com.example.DastyorBot.entity.mediaEntity.AutoMediaEntity;
import com.example.DastyorBot.enums.AcctiveStatus;
import com.example.DastyorBot.enums.MediaType;
import com.example.DastyorBot.enums.ProfileRole;
import com.example.DastyorBot.enums.SelectedPurchaseType;
import com.example.DastyorBot.markUps.AvtoMarkUpsForAdmin;
import com.example.DastyorBot.markUps.AvtoMarksUps;
import com.example.DastyorBot.markUps.MarkUps;
import com.example.DastyorBot.service.AutomobileService;
import com.example.DastyorBot.service.GeneralService;
import com.example.DastyorBot.service.ProfileService;
import com.example.DastyorBot.service.mediaService.AvtoMediaService;
import io.github.nazarovctrl.telegrambotspring.bot.MessageSender;
import io.github.nazarovctrl.telegrambotspring.controller.AbstractUpdateController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
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

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Component
public class UpdateController extends AbstractUpdateController {
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private MarkUps markUps;
    @Autowired
    private AutomobileService avtoService;
    @Autowired
    private AvtoMediaService avtoMediaService;

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
            try {
                if (currentProfile.getRole().equals(ProfileRole.SUPER_ADMIN)) {
                    messageSuperAdmin(update, currentProfile);
                } else if (currentProfile.getRole().equals(ProfileRole.ADMIN)) {
                    messageAdmin(update, currentProfile);
                } else {
                    messageUser(update, currentProfile);
                }
            } catch (Exception e) {
                messageUser(update, currentProfile);
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
                profileService.changeStep(chatId,"Menu");
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
                    profileService.changeStep(chatId,AutoConstant.AVTO);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setReplyMarkup(AvtoMarksUps.menu());
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(AutoConstant.AVTO);
                    executeMessage(sendMessage);
                } else {
                    int i = 0;
                    List<AutomobileDTO> avtomobileList = avtoService.getAll();
                    for (AutomobileDTO avtomobile : avtomobileList) {
                        if (avtomobile.getModel().contains(text) && avtomobile.getType().equals(SelectedPurchaseType.SALE)) {
                            i++;
                            String info = "Shahar :: " + avtomobile.getCity()
                                    + "\nTuman :: " + avtomobile.getDistrict()
                                    + "\nNomi :: " + avtomobile.getBrandName() + "  " + avtomobile.getModel()
                                    + "\nQo'shimcha malumot :: " + avtomobile.getDescription()
                                    + "\nNarxi :: " + avtomobile.getPrice() + " $"
                                    + "\nTelefon raqam :: " + avtomobile.getPhone()
                                    + "\nTelegram " + avtomobile.getUsername();
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
                                if (avtomobile.getPrice() <= initialPayment && avtomobile.getType().equals(SelectedPurchaseType.SALE)) {
                                    j++;
                                    String info = "Shahar :: " + avtomobile.getCity()
                                            + "\nTuman :: " + avtomobile.getDistrict()
                                            + "\nNomi :: " + avtomobile.getBrandName() + "  " + avtomobile.getModel()
                                            + "\nQo'shimcha malumot :: " + avtomobile.getDescription()
                                            + "\nNarxi :: " + avtomobile.getPrice() + " $"
                                            + "\nTelefon raqam :: " + avtomobile.getPhone()
                                            + "\nTelegram " + avtomobile.getUsername();
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
                    profileService.changeStep(chatId,AutoConstant.AVTO);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setReplyMarkup(AvtoMarksUps.menu());
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(AutoConstant.AVTO);
                    executeMessage(sendMessage);
                } else {
                    int i = 0;
                    List<AutomobileDTO> avtomobileList = avtoService.getAll();
                    for (AutomobileDTO avtomobile : avtomobileList) {
                        if (avtomobile.getModel().equals(text) && avtomobile.getType().equals(SelectedPurchaseType.RENT)) {
                            i++;
                            String info = "Shahar :: " + avtomobile.getCity()
                                    + "\nTuman :: " + avtomobile.getDistrict()
                                    + "\nNomi :: " + avtomobile.getBrandName() + "  " + avtomobile.getModel()
                                    + "\nQo'shimcha malumot :: " + avtomobile.getDescription()
                                    + "\nBoshlang'ich to'lov :: " + avtomobile.getPrice() + " $"
                                    + "\nTelefon raqam :: " + avtomobile.getPhone()
                                    + "\nTelegram " + avtomobile.getUsername();
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
                                if (avtomobile.getPrice() <= initialPayment && avtomobile.getType().equals(SelectedPurchaseType.RENT)) {
                                    j++;
                                    String info = "Shahar :: " + avtomobile.getCity()
                                            + "\nTuman :: " + avtomobile.getDistrict()
                                            + "\nNomi :: " + avtomobile.getBrandName() + "  " + avtomobile.getModel()
                                            + "\nQo'shimcha malumot :: " + avtomobile.getDescription()
                                            + "\nBoshlang'ich to'lov :: " + avtomobile.getPrice() + " $"
                                            + "\nTelefon raqam :: " + avtomobile.getPhone()
                                            + "\nTelegram " + avtomobile.getUsername();
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
            }
        }
    }

    private void messageAdmin(Update update, ProfileDTO currentProfile) {

        Message message = update.getMessage();

        String chatId = message.getChatId().toString();
        if (currentProfile == null || currentProfile.getRole().equals(ProfileRole.USER)) {
            executeMessage(new SendMessage(chatId, "Kechirasiz bu botga faqat adminalar murojat qila oladi!"));
        } else if (currentProfile.getRole().equals(ProfileRole.ADMIN)) {
            if (message.hasText() && message.getText().equals("/start")) {
                profileService.changeStep(chatId,"Menu");
                SendMessage sendMessage = new SendMessage(chatId, "Menu");
                sendMessage.setReplyMarkup(MarkUps.menu());
                executeMessage(sendMessage);
                return;
            }

            if (currentProfile.getCurrentStep().equals(AutoConstant.ADD_PHOTO_TO_AVTO)) {
                if (message.hasText()) {
                    String text1 = message.getText();
                    if (text1.equals(CommonConstants.BACK)) {
                        profileService.changeStep(chatId,AutoConstant.AVTO);
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
                                            + "\n Username: @" + avtomobile.getUsername() + " bo'lgan avtomobilega surat joylamoqdasiz!";
                                    currentProfile.setChangingElementId(avtomobile.getId());
                                    executeMessage(new SendMessage(chatId, attention));
                                    profileService.changingElementId(avtomobile.getId(),chatId);
                                    return;
                                }
                            }
                        }
                    }
                } else if (message.hasPhoto()) {
                    if (currentProfile.getChangingElementId() != null) {
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
                    }
                }
            } else if (currentProfile.getCurrentStep().equals(AutoConstant.ADD_VIDEO_TO_AVTO)) {
                if (message.hasText()) {
                    String text1 = message.getText();
                    if (text1.equals(CommonConstants.BACK)) {
                        profileService.changeStep(chatId,AutoConstant.AVTO);
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
                                            + "\n Username: @" + avtomobile.getUsername() + " bo'lgan avtomobilega surat joylamoqdasiz!";
                                    currentProfile.setChangingElementId(avtomobile.getId());
                                    executeMessage(new SendMessage(chatId, attention));
                                    profileService.changingElementId(avtomobile.getId(),chatId);
                                    return;
                                }
                            }
                        }
                    }
                } else if (message.hasVideo()) {
                    if (currentProfile.getChangingElementId() != null) {
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
                    }
                }
            } else if (currentProfile.getCurrentStep().equals(AutoConstant.ENTERING_BRAND_NAME)) {
                if (message.getText().equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId,AutoConstant.CHOOSING_CAR_TYPE);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(AutoConstant.CHOOSING_CAR_TYPE);
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
                    profileService.changeStep(chatId,AutoConstant.ENTERING_BRAND_NAME);
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
                    profileService.changeStep(chatId,AutoConstant.ENTERING_MODEL);
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
                    profileService.changeStep(chatId,AutoConstant.ENTERING_PRICE);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(AutoConstant.ENTERING_PRICE);
                    executeMessage(sendMessage);
                    return;
                }
                avtoService.setPhone(currentProfile.getChangingElementId(), message.getText());
                profileService.changeStep(chatId, AutoConstant.ENTERING_USERNAME);
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.setText(AutoConstant.ENTERING_USERNAME);
                executeMessage(sendMessage);
            } else if (currentProfile.getCurrentStep().equals(AutoConstant.ENTERING_USERNAME)) {
                if (message.getText().equals(CommonConstants.BACK)) {
                    profileService.changeStep(chatId,AutoConstant.ENTERING_PHONE);
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
                    profileService.changeStep(chatId,AutoConstant.ENTERING_USERNAME);
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
                    profileService.changeStep(chatId,AutoConstant.ENTERING_PHONE);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText(AutoConstant.ENTERING_PHONE);
                    executeMessage(sendMessage);
                    return;
                } else if (message.getText().equals(CommonConstants.NEXT)) {
                    profileService.changeStep(chatId,AutoConstant.ENTERING_START_TIME);
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
            }
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
            } else if (data.equals(AutoServiceConstants.AVTO_SERVICE)) {

            } else if (data.equals(AutoConstant.AVTO_SPARE_PARTS_STORES)) {

            } else if (data.equals(HomeConstants.HOME)) {

            } else if (data.equals(HomeServiceConstants.HOME_SERVICE)) {

            } else if (data.equals("Apteka 24/7 \uD83D\uDC8A\uD83C\uDFEA")) {

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
        }
    }

    private void callBQSuperAdmin(Update update, ProfileDTO currentProfile) {
    }

    private void callBQAdmin(Update update, ProfileDTO currentProfile) {
        Message message = update.getCallbackQuery().getMessage();
        String chatId = message.getChatId().toString();
        String text = message.getText();
        String data = update.getCallbackQuery().getData();
        if (text.equals("Menu") && currentProfile.getCurrentStep().equals(CommonConstants.MENU)) {
            if (data.equals(AutoConstant.AVTO)) {
                profileService.changeStep(chatId,AutoConstant.AVTO);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setText(AutoConstant.AVTO);
                editMessageText.setReplyMarkup(AvtoMarkUpsForAdmin.menuForAdmin());
                editMessageText.setMessageId(message.getMessageId());
                executeEditMessageText(editMessageText);
            }
        } else if (text.equals(AutoConstant.AVTO) && currentProfile.getCurrentStep().equals(AutoConstant.AVTO)) {
            if (data.equals(AutoConstant.ADD_AVTO)) {
                profileService.changeStep(chatId,CommonConstants.ACCEPT_TO_CREAT_NEW_ANNOUNCEMENT);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setText(CommonConstants.ACCEPT_TO_CREAT_NEW_ANNOUNCEMENT);
                editMessageText.setReplyMarkup(AvtoMarksUps.acceptToCreat());
                editMessageText.setMessageId(message.getMessageId());
                executeEditMessageText(editMessageText);
                return;


            } else if (data.equals(AutoConstant.ADD_PHOTO_TO_AVTO)) {
                profileService.changeStep(chatId,AutoConstant.ADD_PHOTO_TO_AVTO);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setText(AutoConstant.ADD_PHOTO_TO_AVTO);
                editMessageText.setMessageId(message.getMessageId());

                executeEditMessageText(editMessageText);
                executeMessage(new SendMessage(chatId, "Avtomobil Id sini kiriting"));
            } else if (data.equals(AutoConstant.ADD_VIDEO_TO_AVTO)) {
                profileService.changeStep(chatId,AutoConstant.ADD_VIDEO_TO_AVTO);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setText(AutoConstant.ADD_VIDEO_TO_AVTO);
                editMessageText.setMessageId(message.getMessageId());
                executeEditMessageText(editMessageText);
                executeMessage(new SendMessage(chatId, "Avtomobil Id sini kiriting"));
            } else if (data.equals(AutoConstant.BLOCK_AVTO)) {
                profileService.changeStep(chatId,AutoConstant.ADD_VIDEO_TO_AVTO);
                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setText(AutoConstant.ADD_VIDEO_TO_AVTO);
                editMessageText.setMessageId(message.getMessageId());

            }
        }else if (data.equals(CommonConstants.ACCEPT)
                &&currentProfile.getCurrentStep().equals(CommonConstants.ACCEPT_TO_CREAT_NEW_ANNOUNCEMENT)) {
            profileService.changeStep(chatId,AutoConstant.CHOOSING_CITY);

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
                &&currentProfile.getCurrentStep().equals(CommonConstants.ACCEPT_TO_CREAT_NEW_ANNOUNCEMENT)) {
            profileService.changeStep(chatId,AutoConstant.AVTO);
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(chatId);
            editMessageText.setText(AutoConstant.AVTO);
            editMessageText.setReplyMarkup(AvtoMarkUpsForAdmin.menuForAdmin());
            editMessageText.setMessageId(message.getMessageId());
            executeEditMessageText(editMessageText);
        } else if (message.getText().equals(AutoConstant.CHOOSING_CITY)
                && currentProfile.getCurrentStep().equals(AutoConstant.CHOOSING_CITY)) {
            if (data.equals(CommonConstants.BACK)) {
                profileService.changeStep(chatId,AutoConstant.AVTO);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(AutoConstant.AVTO);
                editMessage.setReplyMarkup(AvtoMarkUpsForAdmin.menuForAdmin());
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            }
            avtoService.setCity(currentProfile.getChangingElementId(), data);
            profileService.changeStep(chatId,AutoConstant.CHOOSING_CAR_TYPE);

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
                profileService.changeStep(chatId,AutoConstant.CHOOSING_CITY);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(AutoConstant.CHOOSING_CITY);
                editMessage.setReplyMarkup(AvtoMarkUpsForAdmin.choosingCity());
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            }
            avtoService.setCarType(currentProfile.getChangingElementId(), data);
            profileService.changeStep(chatId,AutoConstant.ENTERING_BRAND_NAME);

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
                profileService.changeStep(chatId,AutoConstant.ENTERING_DISTRICT);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(AutoConstant.ENTERING_DISTRICT);
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            }
            avtoService.setStartTime(currentProfile.getChangingElementId(), data);
            profileService.changeStep(chatId,AutoConstant.ENTERING_END_TIME);

            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(chatId);
            editMessageText.setText(AutoConstant.ENTERING_END_TIME);
            editMessageText.setReplyMarkup(AvtoMarkUpsForAdmin.times());
            editMessageText.setMessageId(message.getMessageId());
            executeEditMessageText(editMessageText);
        } else if (message.getText().equals(AutoConstant.ENTERING_END_TIME)
                && currentProfile.getCurrentStep().equals(AutoConstant.ENTERING_END_TIME)) {
            if (data.equals(CommonConstants.BACK)) {
                profileService.changeStep(chatId,AutoConstant.ENTERING_START_TIME);
                EditMessageText editMessage = new EditMessageText();
                editMessage.setChatId(chatId);
                editMessage.setText(AutoConstant.ENTERING_START_TIME);
                editMessage.setReplyMarkup(AvtoMarkUpsForAdmin.times());
                editMessage.setMessageId(message.getMessageId());
                executeEditMessageText(editMessage);
                return;
            }
            avtoService.setEndTime(currentProfile.getChangingElementId(), data);
            profileService.changeStep(chatId,AutoConstant.AVTO);
            executeDeleteMessage(new DeleteMessage(chatId,message.getMessageId()));
            SendMessage sendMessage = new SendMessage(chatId,AutoConstant.SUCCESSFULLY_FINISHED);
            sendMessage.setReplyMarkup(MarkUps.start());
            executeMessage(sendMessage);
            SendMessage sendMessage1=new SendMessage(chatId,AutoConstant.AVTO);
            sendMessage1.setReplyMarkup(AvtoMarkUpsForAdmin.menuForAdmin());
            executeMessage(sendMessage1);
            return;
        }
    }

    private void avtoMenu(Update update, String chatId, ProfileDTO currentProfile) {
        profileService.changeStep(AutoConstant.AVTO, currentProfile.getChatId());

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setReplyMarkup(AvtoMarksUps.menu());
        editMessageReplyMarkup.setMessageId(update.getCallbackQuery().getMessage().getMessageId());

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setText(AutoConstant.AVTO);
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        try {
            messageSender.execute(editMessageText);
            messageSender.execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void autoBought(Update update, String chatId, ProfileDTO currentProfile) {
        profileService.changeStep(AutoConstant.AVTO_BOUGHT, currentProfile.getChatId());
        profileService.changePurchaseType(SelectedPurchaseType.SALE,chatId);

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
        profileService.changeStep(AutoConstant.AVTO_RENT, currentProfile.getChatId());
        profileService.changePurchaseType(SelectedPurchaseType.RENT,chatId);

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
}
