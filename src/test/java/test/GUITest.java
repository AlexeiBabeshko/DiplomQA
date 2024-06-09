package test;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import data.CardInfo;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import page.PaymentPage;
import page.TourPage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static data.DataHelper.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GUITest {

    private final String approvedCardMsg = "Успешно";
    private final String declinedCardMsg = "Ошибка";
    private final String wrongFormatMsg = "Неверный формат";
    private final String expiredCard = "Истёк срок действия карты";
    private final String wrongDate = "Неверно указан срок действия карты";
    private final int cvcCount = 3;
    private final int cardNumberCount = 16;
    private final int monthCount = 12;
    private final int shift = 3;
    private final int indexStatusApproved = 0;
    private final int indexStatusDeclined = 1;
    TourPage tourPage;

    @BeforeAll
    static void setupAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void setup() {
        tourPage = open(System.getProperty("sut.url"), TourPage.class);
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Оплата тура картой со статусом «APPROVED»")
    public void paymentForTourByCardStatusApproved() {
        CardInfo approvedCard = generateValidCardInfo(indexStatusApproved);

        PaymentPage paymentPage = tourPage.payByCard();
        String notificationMsg = paymentPage.validPayment(approvedCard);

        assertTrue(notificationMsg.contains(approvedCardMsg));
    }
    @Test
    @DisplayName("Оплата тура картой со статусом «DECLINED»")
    public void paymentForTourByCardStatusDeclined() {
        CardInfo declinedCard = generateValidCardInfo(indexStatusDeclined);

        PaymentPage paymentPage = tourPage.payByCard();
        String notificationMsg = paymentPage.validPayment(declinedCard);

        assertTrue(notificationMsg.contains(declinedCardMsg));
    }
    @Test
    @DisplayName("Оплата тура c незарегистрированной картой")
    public void paymentForTourByNotRegisteredCard() {
        CardInfo unregisteredCard = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        String notificationMsg = paymentPage.validPayment(unregisteredCard);

        assertTrue(notificationMsg.contains(declinedCardMsg));
    }
    @Test
    @DisplayName("Отправка пустой формы")
    public void sendEmptyForm() {
        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.sendForm();
        ElementsCollection invalidMsgCollection = paymentPage.getCollectionInvalidMsg();

        assertEquals(5, invalidMsgCollection.size());
    }

    @Test
    @DisplayName("Валидация поля «Номер карты»: 16 цифр»")
    public void validationFieldCardNumber16Digits() {
        CardInfo validCardNumber = new CardInfo(generateNumber(16), "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(validCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля «Номер карты»: 15 цифр»")
    public void validationFieldCardNumber15Digits() {
        CardInfo invalidCardNumber = new CardInfo(generateNumber(cardNumberCount - 1), "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(invalidCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }
    @Test
    @DisplayName("Валидация поля «Номер карты»: 17 цифр»")
    public void validationFieldCardNumber17Digits() {
        CardInfo invalidCardNumber = new CardInfo(generateNumber(cardNumberCount + 1), "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(invalidCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();

        assertEquals(cardNumberCount + 3, paymentPage.getFieldValueCardNumber().length());
        invalidMsg.shouldNot(exist);
    }

    @Test
    @DisplayName("Валидация данных в поле «Номер карты»: буквы")
    public void validationFieldCardNumberLetters() {
        String cardNumber = generateName();
        CardInfo invalidCardNumber = new CardInfo(cardNumber, "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(invalidCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();

        assertEquals(0, paymentPage.getFieldValueCardNumber().length());
        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация поля “Месяц”: число от 01 до 12")
    public void validationFieldMonthValidValue() {
        CardInfo validMonth = new CardInfo("", generateMonth(), "", "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(validMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля “Месяц”: число 00")
    public void validationFieldMonth00Value() {
        CardInfo invalidMonth = new CardInfo("", "00", "", "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }
    @Test
    @DisplayName("Валидация поля “Месяц”: число 13")
    public void validationFieldMonth13Value() {
        CardInfo invalidMonth = new CardInfo(generateNumber(cardNumberCount), String.valueOf(monthCount + 1), generateYear(shift), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongDate));
    }
    @Test
    @DisplayName("Валидация поля “Месяц”: цифры от 1 до 9")
    public void validationFieldMonthDigitsValue() {
        CardInfo invalidMonth = new CardInfo(generateNumber(cardNumberCount), generateNumber(1), generateYear(shift), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }
    @Test
    @DisplayName("Валидация поля “Месяц”: буквы")
    public void validationFieldMonthLettersValue() {
        CardInfo invalidMonth = new CardInfo("", generateName(), "", "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация поля “Год”: текущий год")
    public void validationFieldYearCurrent() {
        CardInfo currentYear = new CardInfo("", "", generateYear(0), "", "");

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(currentYear);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля “Год”: предыдущий год")
    public void validationFieldYearPrev() {
        CardInfo prevYear = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(-1), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(prevYear);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(expiredCard));
    }
    @Test
    @DisplayName("Валидация поля “Год”: плюс 5 лет")
    public void validationFieldYearPlus5() {
        CardInfo plus5Year = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(5), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(plus5Year);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля “Год”: плюс 6 лет")
    public void validationFieldYearPlus6() {
        CardInfo plus6Year = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(6), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(plus6Year);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongDate));
    }

    @Test
    @DisplayName("Валидация поля “Владелец”: имя и фамилия латиница")
    public void validationFieldOwnerLatin() {
        CardInfo latinOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(latinOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля “Владелец”: дефис в имени")
    public void validationFieldOwnerHyphenInName() {
        String hyphenInName = generateName() + "-" + generateName() + " " + generateLastName();
        CardInfo hyphenOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), hyphenInName, generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(hyphenOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля “Владелец”: только имя")
    public void validationFieldOwnerOnlyName() {
        CardInfo onlyNameOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateName(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(onlyNameOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }
    @Test
    @DisplayName("Валидация поля “Владелец”: доп. пробел в начале")
    public void validationFieldOwnerOnlyStartExtraSpace() {
        CardInfo extraSpaceOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), " " + generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(extraSpaceOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля “Владелец”: доп. пробел в конце")
    public void validationFieldOwnerOnlyStartEndExtraSpace() {
        CardInfo extraSpaceOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateOwner() + " ", generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(extraSpaceOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля “Владелец”: кириллица")
    public void validationFieldOwnerCyrillic() {
        CardInfo cyrillicOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateCyrillicOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(cyrillicOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }
    @Test
    @DisplayName("Валидация поля “Владелец”: цифры")
    public void validationFieldDigits() {
        CardInfo digitsOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateNumber(5), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(digitsOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }
    @Test
    @DisplayName("Валидация поля “Владелец”: спец. символы")
    public void validationFieldSpecialSymbol() {
        CardInfo specialSymbolOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), "IVANOV!$", generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(specialSymbolOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация поля “CVC/CVV”: 3 цифры")
    public void validationFieldCVCValidValue() {
        CardInfo validCVC = new CardInfo("", "", "", "", generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(validCVC);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCVC();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля “CVC/CVV”: 2 цифры")
    public void validationFieldCVC2digits() {
        CardInfo invalidCVC = new CardInfo("", "", "", "", generateNumber(cvcCount - 1));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(invalidCVC);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCVC();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }
    @Test
    @DisplayName("Валидация данных в поле “CVC/CVV”: 4 цифры")
    public void validationFieldCVC4digits() {
        CardInfo invalidCVC = new CardInfo("", "", "", "", generateNumber(cvcCount + 1));

        PaymentPage paymentPage = tourPage.payByCard();
        paymentPage.fillCard(invalidCVC);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCVC();

        assertEquals(cvcCount, paymentPage.getFieldValueCVC().length());
        invalidMsg.shouldNot(exist);
    }

    //Кредитная карта
    @Test
    @DisplayName("Оплата тура в кредит со статусом «APPROVED»")
    public void creditForTourByCardStatusApproved() {
        CardInfo approvedCard = generateValidCardInfo(indexStatusApproved);

        PaymentPage paymentPage = tourPage.payByCredit();
        String notificationMsg = paymentPage.validPayment(approvedCard);

        assertTrue(notificationMsg.contains(approvedCardMsg));
    }
    @Test
    @DisplayName("Оплата тура в кредит со статусом «DECLINED»")
    public void creditForTourByCardStatusDeclined() {
        CardInfo declinedCard = generateValidCardInfo(indexStatusDeclined);

        PaymentPage paymentPage = tourPage.payByCredit();
        String notificationMsg = paymentPage.validPayment(declinedCard);

        assertTrue(notificationMsg.contains(declinedCardMsg));
    }
    @Test
    @DisplayName("Оплата тура в кредит, карта незарегистрирова")
    public void creditForTourByNotRegisteredCard() {
        CardInfo unregisteredCard = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        String notificationMsg = paymentPage.validPayment(unregisteredCard);

        assertTrue(notificationMsg.contains(declinedCardMsg));
    }
    @Test
    @DisplayName("Отправка в заявке на кредит пустой формы")
    public void sendCreditEmptyForm() {
        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.sendForm();
        ElementsCollection invalidMsgCollection = paymentPage.getCollectionInvalidMsg();

        assertEquals(5, invalidMsgCollection.size());
    }

    @Test
    @DisplayName("Валидация поля Кредит - «Номер карты»: 16 цифр»")
    public void validationCreditFieldCardNumber16Digits() {
        CardInfo validCardNumber = new CardInfo(generateNumber(16), "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(validCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля Кредит - «Номер карты»: 15 цифр»")
    public void validationCreditFieldCardNumber15Digits() {
        CardInfo invalidCardNumber = new CardInfo(generateNumber(cardNumberCount - 1), "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(invalidCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }
    @Test
    @DisplayName("Валидация поля Кредит - «Номер карты»: 17 цифр»")
    public void validationCreditFieldCardNumber17Digits() {
        CardInfo invalidCardNumber = new CardInfo(generateNumber(cardNumberCount + 1), "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(invalidCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();

        assertEquals(cardNumberCount + 3, paymentPage.getFieldValueCardNumber().length());
        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля Кредит - «Номер карты»: буквы")
    public void validationCreditFieldCardNumberLetters() {
        String cardNumber = generateLastName();
        CardInfo invalidCardNumber = new CardInfo(cardNumber, "", "", "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(invalidCardNumber);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCardNumber();

        assertEquals(0, paymentPage.getFieldValueCardNumber().length());
        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация поля Кредит - “Месяц”: число от 01 до 12")
    public void validationCreditFieldMonthValidValue() {
        CardInfo validMonth = new CardInfo("", generateMonth(), "", "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(validMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля Кредит - “Месяц”: число 00")
    public void validationCreditFieldMonth00Value() {
        CardInfo invalidMonth = new CardInfo("", "00", "", "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }
    @Test
    @DisplayName("Валидация поля Кредит - “Месяц”: число 13")
    public void validationCreditFieldMonth13Value() {
        CardInfo invalidMonth = new CardInfo(generateNumber(cardNumberCount), String.valueOf(monthCount + 1), generateYear(shift), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongDate));
    }
    @Test
    @DisplayName("Валидация поля Кредит - “Месяц”: цифры от 1 до 9")
    public void validationCreditFieldMonthDigitsValue() {
        CardInfo invalidMonth = new CardInfo(generateNumber(cardNumberCount), generateNumber(1), generateYear(shift), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }
    @Test
    @DisplayName("Валидация поля Кредит - “Месяц”: буквы")
    public void validationCreditFieldMonthLettersValue() {
        CardInfo invalidMonth = new CardInfo("", generateName(), "", "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(invalidMonth);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgMonth();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация поля Кредит - “Год”: текущий год")
    public void validationCreditFieldYearCurrent() {
        CardInfo currentYear = new CardInfo("", "", generateYear(0), "", "");

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(currentYear);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля Кредит - “Год”: предыдущий год")
    public void validationCreditFieldYearPrev() {
        CardInfo prevYear = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(-1), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(prevYear);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(expiredCard));
    }
    @Test
    @DisplayName("Валидация поля Кредит - “Год”: плюс 5 лет")
    public void validationCreditFieldYearPlus5() {
        CardInfo plus5Year = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(5), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(plus5Year);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля Кредит - “Год”: плюс 6 лет")
    public void validationCreditFieldYearPlus6() {
        CardInfo plus6Year = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(6), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(plus6Year);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongDate));
    }
    @Test
    @DisplayName("Валидация поля Кредит - “Год”: буквы")
    public void validationCreditFieldYearLetters() {
        CardInfo lettersYear = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateLastName(), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(lettersYear);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgYear();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация поля Кредит - “Владелец”: имя и фамилия латиница")
    public void validationCreditFieldOwnerLatin() {
        CardInfo latinOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(latinOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля Кредит - “Владелец”: дефис в имени")
    public void validationCreditFieldOwnerHyphenInName() {
        String hyphenInName = generateName() + "-" + generateName() + " " + generateLastName();
        CardInfo hyphenOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), hyphenInName, generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(hyphenOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля Кредит - “Владелец”: только имя")
    public void validationCreditFieldOwnerOnlyName() {
        CardInfo onlyNameOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateName(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(onlyNameOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }
    @Test
    @DisplayName("Валидация поля Кредит - “Владелец”: лишний пробел в начале")
    public void validationCreditFieldOwnerOnlyStartExtraSpace() {
        CardInfo extraSpaceOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), " " + generateOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(extraSpaceOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля Кредит - “Владелец”: лишний пробел в конце")
    public void validationCreditFieldOwnerOnlyStartEndExtraSpace() {
        CardInfo extraSpaceOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateOwner() + " ", generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(extraSpaceOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля Кредит - “Владелец”: кириллица")
    public void validationCreditFieldOwnerCyrillic() {
        CardInfo cyrillicOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateCyrillicOwner(), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(cyrillicOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }
    @Test
    @DisplayName("Валидация поля Кредит - “Владелец”: спец. символы")
    public void validationCreditFieldSpecialSymbol() {
        CardInfo specialSymbolOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), "IVANOV$%^*", generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(specialSymbolOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }
    @Test
    @DisplayName("Валидация поля Кредит - “Владелец”: цифры")
    public void validationCreditFieldDigits() {
        CardInfo digitsOwner = new CardInfo(generateNumber(cardNumberCount), generateMonth(), generateYear(shift), generateNumber(5), generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(digitsOwner);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgOwner();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }

    @Test
    @DisplayName("Валидация поля Кредит - “CVC/CVV”: 3 цифры")
    public void validationCreditFieldCVCValidValue() {
        CardInfo validCVC = new CardInfo("", "", "", "", generateNumber(cvcCount));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(validCVC);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCVC();

        invalidMsg.shouldNot(exist);
    }
    @Test
    @DisplayName("Валидация поля Кредит - “CVC/CVV”: 2 цифры")
    public void validationCreditFieldCVC2digits() {
        CardInfo invalidCVC = new CardInfo("", "", "", "", generateNumber(cvcCount - 1));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(invalidCVC);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCVC();

        invalidMsg
                .shouldBe(visible)
                .shouldHave(text(wrongFormatMsg));
    }
    @Test
    @DisplayName("Валидация поля Кредит - “CVC/CVV”: 4 цифры")
    public void validationCreditFieldCVC4digits() {
        CardInfo invalidCVC = new CardInfo("", "", "", "", generateNumber(cvcCount + 1));

        PaymentPage paymentPage = tourPage.payByCredit();
        paymentPage.fillCard(invalidCVC);
        SelenideElement invalidMsg = paymentPage.getInvalidMsgCVC();

        assertEquals(cvcCount, paymentPage.getFieldValueCVC().length());
        invalidMsg.shouldNot(exist);
    }
}