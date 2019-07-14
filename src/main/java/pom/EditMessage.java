package pom;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.functions.ExpectedCondition;
import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.interactions.Actions;

public class EditMessage extends BasePage implements EditMessageInterface {

    @AndroidFindBy(id = "compose_message_text")
    private MobileElement textBox;

    @AndroidFindBy(id = "send_message_button_icon")
    private MobileElement sendButton;


    public EditMessage(AppiumDriver<MobileElement> driver){

        super(driver);
    }

    public void addMessage(String message){
        textBox.click();
        textBox.sendKeys(message);
        sendButton.click();
    }



}