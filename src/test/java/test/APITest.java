package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.CardInfo;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;

import static api.RequestGenerator.*;
import static data.SQLHelper.*;
import static data.DataHelper.generateValidCardInfo;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class APITest {
    private final String expectedApproved = "APPROVED";
    private final String expectedDeclined = "DECLINED";
    private final String expectedAmount = "4500000";
    private final int indexStatusApproved = 0;
    private final int indexStatusDeclined = 1;

    @BeforeAll
    static void setupAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterEach
    public void tearDown(){
        cleanDB();
    }

    @AfterAll
    public static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Оплата тура по карте со статусом “APPROVED”")
    public void payByCardStatusApproved() {
        CardInfo approvedCard = generateValidCardInfo(indexStatusApproved);

        String statusResponse = postPaymentByCard(approvedCard);
        String statusFromDB = getStatusFromPaymentEntity();
        String paymentAmount = getAmountFromPaymentEntity();

        assertAll(
                () -> assertEquals(expectedApproved, statusResponse),
                () -> assertEquals(expectedApproved, statusFromDB),
                () -> assertEquals(expectedAmount, paymentAmount)
        );
    }

    @Test
    @DisplayName("Оплата тура по карте со статусом “DECLINED”")
    public void payByCardStatusDeclined() {
        CardInfo approvedCard = generateValidCardInfo(indexStatusDeclined);

        String statusResponse = postPaymentByCard(approvedCard);
        String statusFromDB = getStatusFromPaymentEntity();
        String paymentAmount = getAmountFromPaymentEntity();

        assertAll(
                () -> assertEquals(expectedDeclined, statusResponse),
                () -> assertEquals(expectedDeclined, statusFromDB),
                () -> assertEquals(expectedAmount, paymentAmount)
        );
    }

    @Test
    @DisplayName("Оплата тура в кредит, карта со статусом “APPROVED”")
    public void payByCreditStatusApproved() {
        CardInfo approvedCard = generateValidCardInfo(indexStatusApproved);

        String paymentStatusResponse = postPaymentByCredit(approvedCard);
        String paymentStatusDB = getStatusFromCreditEntity();

        assertAll(
                () -> assertEquals(expectedApproved, paymentStatusResponse),
                () -> assertEquals(expectedApproved, paymentStatusDB)
        );
    }

    @Test
    @DisplayName("Оплата тура в кредит, карта со статусом “DECLINED”")
    public void payByCreditStatusDeclined() {
        CardInfo approvedCard = generateValidCardInfo(indexStatusDeclined);

        String paymentStatusResponse = postPaymentByCredit(approvedCard);
        String paymentStatusDB = getStatusFromCreditEntity();

        assertAll(
                () -> assertEquals(expectedDeclined, paymentStatusResponse),
                () -> assertEquals(expectedDeclined, paymentStatusDB)
        );
    }
}
