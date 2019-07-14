package pom;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.functions.ExpectedCondition;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.interactions.Actions;

public class NewChatPage extends BasePage implements NewChatPageInterface {

    @AndroidFindBy(id = "recipient_text_view")
    private MobileElement searchContact;

    @AndroidFindBy(id = "contact_list_view")
    private MobileElement contacts;



    Actions action;

    public NewChatPage(AppiumDriver<MobileElement> driver){

        super(driver);
        action = new Actions(driver);
        try {
            customWebDriver.withTimeout(timeOutDuration, ((ExpectedCondition) WebDriver -> {

                if (searchContact.isDisplayed()) {
                    return true;
                } else
                    return false;
            }));
        }
        catch (TimeoutException e) { }

    }

    public void setSearchContact(String device){
        searchContact.click();
        action.sendKeys(device).build().perform();
    }

    public EditMessage chooseContact(){
        contacts.findElementsByClassName("android.widget.FrameLayout").get(0).click();
        return  new EditMessage(driver);
    }



}