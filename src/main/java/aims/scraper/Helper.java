package aims.scraper;

import aims.util.WaitUtil;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helper {

    public final static Logger logger = LoggerFactory.getLogger(Helper.class);

    private WebDriver driver;

    private WebDriverWait wait;

    protected final int SLEEP_TIME = 4000;

    protected final int WAIT_TIME = 30; // 30초를 180초로 변경. by 우정 2019.10.18

    public Helper(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public boolean isAlertShowed() {
        try {
            Alert alert = new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.alertIsPresent());
            if (alert != null) {
                // driver.switchTo().alert().accept();
                return true;
            } else {
                throw new Throwable();
            }
        } catch (Throwable e) {
            return false;
        }
    }

    public void executeJavascript(String script) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(script);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void waitForLoading(By... by) throws InterruptedException {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='loading']")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[class~='Loading']")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[id~='loading']")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[id~='Loading']")));
        if (by.length > 0) {
            for (By i : by) {
                wait.until(ExpectedConditions.invisibilityOfElementLocated(i));
            }
        }
    }

    public void waitForCSSElement(String css) throws Exception {
        int time = 0;
        boolean result = true;
        try {
            WaitUtil.loading(1);
            while (result) {
                //logger.debug("displayed :: " + driver.findElement(By.cssSelector(css)).isDisplayed());
                if (driver.findElement(By.cssSelector(css)).isDisplayed()) {
                    logger.info("로딩 중....");
                    Thread.sleep(500);
                    time += 500;
                } else {
                    logger.info("로딩 끝....");
                    WaitUtil.loading(2);
                    break;
                }
                if (time > 120000) {
                    result = false;
                    throw new Exception("무한루프 오류 입니다.");
                }
            }
        } catch (Exception e) {
            if (!result) {
                throw new Exception(e);
            }
            logger.info("####### 로딩 끝....");
            WaitUtil.loading(1);
        }
    }


    public void oppositionWaitForLoading(String element) throws InterruptedException {
        int time = 0;
        try {
            while (true) {
                WaitUtil.waitFor();
                if (!(driver.findElement(By.id(element)).isDisplayed())) {
                    logger.info("로딩 중....");
                    time += SLEEP_TIME;
                    if (time > 60000) {
                        throw new Exception("무한루프 오류 입니다.");
                    }
                } else {
                    logger.info("로딩 끝....");
                    break;
                }
            }
            WaitUtil.waitFor();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public void setWait(WebDriverWait wait) {
        this.wait = wait;
    }


    // locator로 element 찾아서 클릭
    public void doClick(By locator) throws Exception {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    // element로 element 클릭
    public void doClick(WebElement webElement) throws InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(webElement)).click();
    }

    // inputBox에 키 입력
    public void doInputBox(By by, String value) throws InterruptedException {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(by));
        element.click();
        // 기존에 쓰여있는 값 지우기.
        element.clear();
        // 아래코드 크롬에서 안먹음
//		Actions builder = new Actions(driver);
//		builder.keyDown(Keys.CONTROL)
//				.sendKeys("a")
//				.keyUp(Keys.CONTROL)
//				.sendKeys(Keys.DELETE)
//				.build().perform();
        WaitUtil.loading(1);
        logger.info("value :: " + value);
        element.sendKeys(value);
    }

    public void doSendKeys(By by, String value) throws InterruptedException {
        logger.debug("value: {}", value);
        waitVisibilityOfElementLocated(by);
        WebElement element = waitElementToBeClickable(by);
        element.click();
        element.clear();
        element.sendKeys(value);
    }

    public void doSendKeys(WebElement element, String value) throws InterruptedException {
        waitVisibilityOf(element);
        element = waitElementToBeClickable(element);
        element.click();
        element.clear();
        element.sendKeys(value);
    }

    // sendKeys()를 사용할 수 없을 때 JavascriptExecutor 사용하여 value 값 세팅
    public void doSendKeys2(By by, String value) throws InterruptedException {
        logger.debug("value: {}", value);
        waitVisibilityOfElementLocated(by);
        WebElement element = driver.findElement(by); // you can use any locator
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("arguments[0].value='" + value + "';", element);
    }

    @SuppressWarnings("unchecked")
    protected void doRadioButton(By by, String value) throws InterruptedException {
        List<WebElement> elements = (List<WebElement>) wait.until(
            ExpectedConditions.elementToBeClickable(by));
        WebElement element = elements.get(Integer.parseInt(value));
        element.click();
        WaitUtil.waitFor("doRadioButton");
    }

    // 라디오버튼의 텍스트 요소를 클릭
    public void doRadioButtonTextClick(By byRadioBtn, String value, By byClickTargetText)
        throws InterruptedException {
        waitVisibilityOfElementLocated(byRadioBtn);
        List<WebElement> radioBtns = driver.findElements(byRadioBtn);
        for (WebElement radioBtn : radioBtns) {
            if (radioBtn.getAttribute("value").equals(value)) {
                logger.info("라디오 버튼 클릭 : " + radioBtn.getAttribute("value"));

                // 해당 요소의 부모 요소 내 지정한 대상 요소를 클릭
                radioBtn.findElement(By.xpath("parent::*")).findElement(byClickTargetText).click();

                break;
            }
        }
    }

    public void doSelectBox(By by, String value) throws Exception {
        boolean isFind = false;
        try {
            WebElement element = driver.findElement(by);
            List<WebElement> elements = element.findElements(By.tagName("option"));

            for (WebElement option : elements) {
                if (option.getAttribute("value").contains(value)
                    || option.getText().equals(value)) {
                    option.click();
                    isFind = true;
                    break;
                }
            }
            WaitUtil.waitFor("doSelectBox");
        } finally {
            if (isFind == false) {
                throw new Exception("해당 value=" + value + " 값을 찾을수 없습니다.");
            }
        }
    }

    public void doSelectBox(WebElement element, String value) throws Exception {
        boolean isFind = false;
        try {
            List<WebElement> elements = element.findElements(By.tagName("option"));

            for (WebElement option : elements) {
                if (option.getAttribute("value").equals(value)
                    || option.getAttribute("numvl").equals(value)
                    || option.getText().equals(value)) {
                    option.click();
                    isFind = true;
                    break;
                }
            }
            WaitUtil.waitFor("doSelectBox");
        } finally {
            if (isFind == false) {
                throw new Exception("해당 value=" + value + " 값을 찾을수 없습니다.");
            }
        }
    }


    public void elementWaitFor(String element) throws Exception {
        int time = 0;
        boolean result = true;
        while (true) {
            try {
                if (driver.findElement(By.cssSelector(element)).isDisplayed()) {
                    logger.info(element + ":element 로딩 끝!");
                    break;
                } else {
                    logger.info(element + ":element 로딩 중....");
                    Thread.sleep(500);
                    time += 500;
                    if (time > 120000) {
                        result = false;
                        break;
                    }
                }
            } catch (Exception e) {
                logger.info(element + ":element 로딩 중....");
                Thread.sleep(500);
                time += 500;
                if (time > 120000) {
                    result = false;
                    break;
                }
            }
        }

        if (!result) {
            throw new Exception("무한루프 오류입니다.");
        }
    }

    // 명시적 대기: 클릭 가능해질 때까지 By
    public WebElement waitElementToBeClickable(By by) {
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    // 명시적 대기: 클릭 가능해질 때까지 WebElement
    public WebElement waitElementToBeClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    // 명시적 대기: 보일 때까지 WebElement
    public WebElement waitVisibilityOf(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    // 명시적 대기: 보일 때까지 By
    public WebElement waitVisibilityOfElementLocated(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    // 명시적 대기: 존재 By
    public WebElement waitPresenceOfElementLocated(By by) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by));
    }

    // 여러개 element 명시적 대기 By
    public List<WebElement> waitVisibilityOfAllElementsLocatedBy(By by) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
    }

    // 여러개 element 명시적 대기 By
    public List<WebElement> waitPesenceOfAllElementsLocatedBy(By by) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
    }

    // 여러개 element 명시적 대기 By
    public List<WebElement> waitVisibilityOfAllElements(List<WebElement> elements) {
        return wait.until(ExpectedConditions.visibilityOfAllElements(elements));
    }


    public void elementWait(String value) {
        try {
            logger.info(value + "element 찾는 중...");
            wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(value)));
        } catch (Exception e) {
            logger.error(value + " 요소를 찾을 수 없습니다.");
        }
    }


    public void switchToWindow(String currentHandle, Set<String> windowId, boolean value) {

        Iterator<String> handles = windowId.iterator();
        // 메인 윈도우 창 확인
        String subHandle = null;

        while (handles.hasNext()) {
            subHandle = handles.next();
            if (subHandle.equals(currentHandle)) {
                continue;
            } else {
                // true : 이전 창을 닫지 않음, false : 이전 창을 닫음
                if (!value) {
                    driver.close();
                }
                driver.switchTo().window(subHandle);
                wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIME));
                break;
            }
        }
    }

    /**
     * 메인페이지 이외의 팝업페이지를 모두 닫는다.
     */
    public void closeOtherWindows() {
        String currentHandle = driver.getWindowHandles().iterator().next();
        // 현재창
        if (driver.getWindowHandles().size() > 1) {

            for (String handle : driver.getWindowHandles()) {
                if (!handle.equals(currentHandle)) {
                    driver.switchTo().window(handle).close();
                }
            }

            driver.switchTo().window(currentHandle);
        }
    }

}
