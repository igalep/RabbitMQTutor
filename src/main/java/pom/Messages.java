package pom;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;

public class Messages extends BasePage implements MessagesInterface {

    @AndroidFindBy(id = "start_new_conversation_button")
    private MobileElement new_conversation;

    public Messages(AppiumDriver<MobileElement> driver){
        super(driver);
    }


    public NewChatPage newChat(){
        new_conversation.click();
        return new NewChatPage(driver);
    }


}