package aims.scraper;

import static aims.util.InsuranceUtil.getBirthday;

import aims.util.WaitUtil;
import aims.vo.TreatyScrapData;
import aims.vo.ScrapResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import aims.vo.TypeScrapData;
import com.google.gson.Gson;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class 신한생명 extends TargetCompnay {

    public final static Logger logger = LoggerFactory.getLogger(신한생명.class);

    protected final static Map<String, List<String>> prdTypeMap = new HashMap<>();

    static {
        List<String> categories = new ArrayList<>();
        categories.add("종신/정기");
        categories.add("건강보험");
        categories.add("상해");
        categories.add("어린이");
        categories.forEach(c -> {
            categoryProductsMap.put(c, new ArrayList<>());
        });
    }

    public 신한생명(String url) {
        super(url);
    }

    public static void main(String[] args) {
        try {

            new 신한생명("https://www.shinhanlife.co.kr/hp/cdhi0290.do#").scrap();

        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("크롤링 에러: " + e.getMessage());
        }
    }

    @Override
    protected void start() throws Exception {
        super.start();
        clickFirstCalcBtn();
    }

    protected void scrap() throws Exception {

        setDefaultScrapRange(); // 스크랩 범위 설정 (category에 따른 product 목록 설정)

        List<ScrapResult> resultList = new ArrayList<>();
        for (String category : categoryProductsMap.keySet()) {
            for (String product : categoryProductsMap.get(category)) {

                logger.info("카테고리: {} \\t 상품: {}", category, product);
                ScrapTarget scrapTarget =
                        new ScrapTarget(this.getClass().getSimpleName(), category, product, resultList);

                start();
                boolean success = false;

                try {
                    selectCategory(category);
                    selectProduct(product);
                    setUserInfo();

//                    success = scrapTreatyOfProduct(category, product);
                    success = scrapTypesOfProduct(category, product);

                } catch (InterruptedException e) {

                    logger.info("error message {}", e.getMessage());
                    logger.info("실패! 카테고리: {} 상품: {}", category, product);
                    throw new RuntimeException(e);

                } finally {

                    stop();
                    resultList.add(
                            new ScrapResult(scrapTarget, success));
                }

            }
        }

        resultList.forEach(r -> logger.info(r.toString()));
    }

    private boolean scrapTypesOfProduct(String category, String product) {
        boolean success = false;

        try {
            Map<String, List<String>> typeOptionMap = new HashMap<>();
            TypeScrapData 신한생명 = new TypeScrapData("신한생명", category, product, typeOptionMap);

            helper.waitPesenceOfAllElementsLocatedBy(
                            By.xpath("//div[@id='mnprDivision'] // li")).stream()
                    .filter(WebElement::isDisplayed)
                    .forEach(li -> {
                        String title = li.findElement(By.cssSelector("div.item")).getText();
                        WebElement div = li.findElement(By.cssSelector("div.val"));
                        try {
                            List<String> optionList = new Select(
                                    div.findElement(By.tagName("select")))
                                    .getOptions().stream()
                                    .map(WebElement::getText).toList();

                            typeOptionMap.put(title, optionList);
                        } catch (NoSuchElementException ignore) {
                            logger.info("select tag가 아님", ignore);

                        }
                    });

            sendTypeOfProduct(신한생명);

            success = true;
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }

        return success;
    }

    private void sendTypeOfProduct(TypeScrapData typeScrapData) {
        String jsonData = new Gson().toJson(typeScrapData);
        String url = "http://127.0.0.1:8081/treatyResearch";
//        String url = "https://api.nuzal.kr/treatyResearch";
        logger.info("전송 데이터: {}", jsonData);
        System.out.println();
//        HttpClientUtil.sendPOSTWithJwtToken(url, jsonData );

    }

    private boolean scrapTreatyOfProduct(String category, String product) {

        boolean success = false;

        try {
            List<TreatyScrapData> treatyList = new ArrayList<>();
            List<String> types = getOptionStrings(new Select(
                    driver.findElement(By.xpath("//select[@title='보험종류']"))));

            for (String productType : types) {
                logger.info("productType : {}", productType);
                expandMainTreatyAcoBtn();
                selectProductType(new Select(driver.findElement(
                        By.xpath("//select[@title='보험종류']"))), productType);
                helper.waitForLoading();

                // 주계약 확인 버튼
                helper.doClick(
                        By.cssSelector("#mnprDivision > div.btnArea.btnLoCtl > span > button"));
                tryResetBirthDayIfNeeded();

                treatyList.addAll(getTreatyList(category, product, productType));

            }

            sendData(treatyList);
            success = true;

        } catch (Exception e) {
            logger.info(e.getMessage());

        }

        return success;
    }


    private void selectProductType(Select driver, String productType) {
        driver.selectByVisibleText(productType);
    }

    private static void expandMainTreatyAcoBtn() throws InterruptedException {
        WebElement acoBtn = helper.waitElementToBeClickable(By.xpath(
                "//div[@class='basicCell'][contains(.,'주계약계산')]/ancestor::li//div[@class='accoBtnCell']//button"));
        if (acoBtn.getAttribute("aria-expanded").equals("false")) {
            helper.doClick(acoBtn);
        }
    }

    private List<String> getOptionStrings(Select select) {
        return select.getOptions()
                .stream()
                .map(WebElement::getText).toList();
    }


    private List<TreatyScrapData> getTreatyList(String category, String product, String productType) {
        List<TreatyScrapData> list = new ArrayList<>();
        helper.waitPesenceOfAllElementsLocatedBy(
                        By.xpath("//tbody[@class='intyList']//td[@title='가입상품']"))
                .forEach(td -> {
                    list.add(new TreatyScrapData("신한생명", category, product, productType, td.getText()));
                });
        return list;
    }

    private void selectProduct(String product) {
        selectProductType(new Select(driver.findElement(
                By.xpath("//div[@class='dataSrchBox']/select[@title='상품명']"))), product);
    }

    private void setDefaultScrapRange() {

        try {
            start();

            categoryProductsMap.keySet().forEach(category -> {
                        try {
                            selectCategory(category);

                            Select productSelect = new Select(driver.findElement(
                                    By.xpath("//div[@class='dataSrchBox']/select[@title='상품명']")));

                            List<String> prdList = getOptionStrings(productSelect);
                            categoryProductsMap.put(category, prdList);

                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            stop();

            int count = 0;
            for (Entry<String, List<String>> entry : categoryProductsMap.entrySet()) {

                logger.info("카테고리: {} ({}개)", entry.getKey(), entry.getValue().size());
                count += entry.getValue().size();
            }
            logger.info("총 {} 개", count);
        }
    }

    private void selectCategory(String category) throws InterruptedException {
        Select categorySelect = new Select(driver.findElement(
                By.xpath("//div[@class='dataSrchBox']/select[@title='상품종류']")));
        selectProductType(categorySelect, category);
        WaitUtil.loading(1);
    }

    private void clickFirstCalcBtn() throws Exception {
        WaitUtil.loading(1);
        helper.waitForLoading();
        WaitUtil.loading(2);

        helper.doClick(By.xpath("//span[text()='보험료계산']/parent::button"));

        WaitUtil.loading(1);
        helper.waitForLoading();
    }

    private void setUserInfo() throws Exception {

        for (WebElement el : helper.waitVisibilityOfAllElementsLocatedBy(
                By.xpath("//input[@title='생년월일']"))) { // 어린이 보험일 경우 여러개

            helper.doSendKeys(el, getBirthday(30));
        }

        helper.doClick(By.xpath("//label[text()='남']"));
        helper.doSelectBox(By.id("vhclKdCd"), "승용차(자가용)");

        // 직업검색 버튼
        helper.doClick(By.cssSelector(
                "#csinDivision > div.infoRowArea > ul > li:nth-child(5) > div.val > button"));
        helper.doSendKeys(By.id("jobNmPop"), "회사");
        helper.doClick(By.id("btnJobSearch"));
        helper.doClick(By.xpath("//*[@id='JobSearchList']/li/a[contains(.,'회사 사무직 종사자')]"));

        confirmUserInfo(); // 확인 버튼 & 가입 가능범위를 벗어날 경우 새 생년월일 입력
    }

    private void confirmUserInfo() throws Exception {
        //확인버튼
        helper.doClick(By.cssSelector("#csinDivision > div.btnArea.btnLoCtl > span > button"));

        helper.waitForLoading();
        WaitUtil.loading(2);

        tryResetBirthDayIfNeeded(); // 새 생년월일 입력
    }

    private void tryResetBirthDayIfNeeded() throws Exception {

        try {
            if (driver.findElement(By.id("messagePopWrap")).isDisplayed()) { // 알람창이 떴을 경우

                String text = driver.findElement(
                        By.xpath("//*[@id='messagePopWrap']//div[@title='알림']/p")).getText();

                int startAge = Integer.parseInt(text.substring(
                        text.lastIndexOf("가입 가능한 연령은 ") + "가입 가능한 연령은 ".length(),
                        text.lastIndexOf("세 ~ ")
                ).replaceAll("\\D", ""));

                int endAge = Integer.parseInt(text.substring(
                        text.lastIndexOf("세 ~ ") + "세 ~ ".length(),
                        text.lastIndexOf("까지 가능")
                ).replaceAll("\\D", ""));

                helper.doClick(By.id("popupBtn2")); // 닫기 버튼

                int newAge = (startAge + endAge) / 2; // 중간나이 구하기

                String birthday = getBirthday(newAge);
                logger.info("가입가능한 연령 {}", newAge);
                logger.info("가입가능한 연령의 생년월일 {}", birthday);

                for (WebElement el : helper.waitVisibilityOfAllElementsLocatedBy(
                        By.xpath("//input[@title='생년월일']"))) {

                    helper.doSendKeys(el, birthday); // 새 생년월일 입력
                }

                helper.doClick(
                        By.cssSelector("#csinDivision > div.btnArea.btnLoCtl > span > button")); //확인버튼
                helper.waitForLoading();
            }
        } catch (Exception e) {

        }


    }
}
