package com.example.DastyorBot.repository;

import com.example.DastyorBot.entity.ProfileEntity;
import com.example.DastyorBot.enums.SelectedPurchaseType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends CrudRepository<ProfileEntity, Integer> {
    @Query("from ProfileEntity where chatId=:chatIdInput")
    ProfileEntity findByChatId(@Param("chatIdInput")String chatId);

    @Transactional
    @Modifying
    @Query("update ProfileEntity p set p.currentStep=?1 where p.chatId=?2")
    void changeStep(String step,String chatId);

    @Transactional
    @Modifying
    @Query("update ProfileEntity p set p.changingElementId=:inputId where p.chatId=:inputChatId")
    void changingElementId( @Param("inputId")Integer id, @Param("inputChatId") String chatId);

    @Transactional
    @Modifying
    @Query("update ProfileEntity p set p.selectedPurchaseType=:inputType where p.chatId=:inputChatId")
    void changePurchaseType(@Param("inputType")SelectedPurchaseType selectedPurchaseType,@Param("inputChatId") String chatId);
}
