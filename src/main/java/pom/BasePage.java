package pom;

import connection.CustomWebDriver;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.functions.ExpectedCondition;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.logging.Logger;


public class BasePage implements BasePageInterface {

    protected AppiumDriver<MobileElement> driver;
    protected CustomWebDriver customWebDriver;
    protected int timeOutDuration;

    public BasePage(AppiumDriver<MobileElement> driver){
        this.driver = driver;
        this.customWebDriver = new CustomWebDriver(driver);
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(15)), this);
        timeOutDuration = customWebDriver.timeOutDuration();
    }

    protected Logger getLogger() {
        return Logger.getLogger(getClass().getName());
    }

    protected boolean waitForAlertToBePresent(AppiumDriver driver, int timeoutSeconds) {
        try {
            FluentWait<WebDriver> wait = new WebDriverWait(driver, timeoutSeconds).
                    ignoring(WebDriverException.class);
            wait.until(ExpectedConditions.alertIsPresent());
            getLogger().info("Alert is visible");
            return true;
        } catch (WebDriverException e) {
            getLogger().warning("Alert was not visible: " + e.getMessage());
            return false;
        }
    }

    public void setNumberTextBox(MobileElement numberTextBox, String insertedNumber) {
        numberTextBox.sendKeys(insertedNumber);
    }

    protected void checkExistenceAndClick(MobileElement mb){
        try {
            customWebDriver.withTimeout(timeOutDuration, ((ExpectedCondition) WebDriver -> {

                if (mb.isDisplayed()) {
                    mb.click();
                    return true;
                } else
                    return false;
            }));
        }
        catch (TimeoutException e) { }

    }

    public void setDriver(AppiumDriver<MobileElement> driver) {
        this.driver = driver;
    }
}
