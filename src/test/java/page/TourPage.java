package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class TourPage {
    private final ElementsCollection buttons = $$(".button__text");
    private final SelenideElement btnBuy = buttons.get(0);
    private final SelenideElement btnBuyCredit = buttons.get(1);

    public TourPage() {
        SelenideElement travelCard = $("h2.heading");
        travelCard.shouldBe(Condition.visible);
    }

    public PaymentPage payByCard(){
        btnBuy.click();
        return new PaymentPage();
    }

    public PaymentPage payByCredit(){
        btnBuyCredit.click();
        return new PaymentPage();
    }
}
