package aims.scraper;

import static aims.util.InsuranceUtil.getBirthday;

import aims.util.WaitUtil;
import aims.vo.TreatyScrapData;
import aims.vo.TreatyScrapResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class 신한생명 extends TargetCompnay {

    static {
        List<String> categories = new ArrayList<>();
        categories.add("종신/정기");
        categories.add("건강보험");
        categories.add("상해");
        categories.add("어린이");
        categories.forEach(c -> {
            toDo.put(c, new ArrayList<>());
        });
    }

    public 신한생명(Condition condition) throws Exception {
        super(condition);
    }

    public static void main(String[] args) {
        try {
            Condition condition = new Condition();
            condition.setUrl("https://www.shinhanlife.co.kr/hp/cdhi0290.do#");

            new 신한생명(condition).scrap();

        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("크롤링 에러: " + e.getMessage());
        }
    }

    protected void scrap() throws Exception {

        List<TreatyScrapResult> resultList = new ArrayList<>();

        setToDo(); // 스크랩 범위 설정 (category에 따른 product 목록 설정)

        for (String category : toDo.keySet()) {
            for (String product : toDo.get(category)) {

                List<TreatyScrapData> sendList = new ArrayList<>();
                boolean success = false;

                logger.info("카테고리: {} \\t 상품: {}", category, product);

                startDriver();

                try {

                    clickFirstCalcBtn();
                    setCategory(category);
                    setProduct(product);
                    setUserInfo();

                    List<String> types = new Select(
                        driver.findElement(By.xpath("//select[@title='보험종류']"))).getOptions()
                        .stream()
                        .map(WebElement::getText).toList();

                    for (String productType : types) {

                        WebElement acoBtn = helper.waitElementToBeClickable(By.xpath(
                            "//div[@class='basicCell'][contains(.,'주계약계산')]/ancestor::li//div[@class='accoBtnCell']//button"));
                        if (acoBtn.getAttribute("aria-expanded").equals("false")) {
                            helper.doClick(acoBtn);
                        }

                        logger.info("productType : {}", productType);
                        new Select(driver.findElement(
                            By.xpath("//select[@title='보험종류']"))).selectByVisibleText(productType);

                        WaitUtil.loading();
                        tryResetBirthDay();
                        // 주계약 확인 버튼
                        helper.doClick(
                            By.cssSelector("#mnprDivision > div.btnArea.btnLoCtl > span > button"));

                        List<TreatyScrapData> addList = getTreatyList(category, product, productType);
                        sendList.addAll(addList);

                        success = true;
                    }
                } catch (Exception e) {

                    logger.info("error message {}", e.getMessage());
                    logger.info("실패! 카테고리: {} 상품: {}", category, product);

                } finally {
                    stopDriver();
//                    sendData(sendList);

                    resultList.add(new TreatyScrapResult("신한생명", category, product, success));
                }
            }
        }

        resultList.forEach(r -> logger.info(r.toString()));
    }



    private List<TreatyScrapData> getTreatyList(String category, String product, String productType) {
        List<TreatyScrapData> sendList = new ArrayList<>();
        helper.waitPesenceOfAllElementsLocatedBy(
                By.xpath("//tbody[@class='intyList']//td[@title='가입상품']"))
            .forEach(td -> {
                sendList.add(new TreatyScrapData("신한생명", category, product, productType, td.getText()));
            });
        return sendList;
    }

    private void setProduct(String product) {
        new Select(driver.findElement(
            By.xpath("//div[@class='dataSrchBox']/select[@title='상품명']")))
            .selectByVisibleText(product);
    }

    private void setToDo() throws Exception {

//        startDriver(info);
        clickFirstCalcBtn(); // 모달 진입

        toDo.keySet().forEach(category -> {
                List<String> products = toDo.get(category);

                try {
                    setCategory(category);

                    Select productSelect = new Select(driver.findElement(
                        By.xpath("//div[@class='dataSrchBox']/select[@title='상품명']")));

                    List<String> collect = productSelect.getOptions().stream()
                        .map(WebElement::getText).collect(Collectors.toList());

                    products.addAll(collect);

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        );

//        stopDriver(info);

        int count = 0;
        for (Entry<String, List<String>> entry : toDo.entrySet()) {

            logger.info("카테고리: {} ({}개)", entry.getKey(), entry.getValue().size());
            count += entry.getValue().size();
        }
        logger.info("총 {} 개", count);
    }

    private void setCategory(String category) throws InterruptedException {
        Select categorySelect = new Select(driver.findElement(
            By.xpath("//div[@class='dataSrchBox']/select[@title='상품종류']")));
        categorySelect.selectByVisibleText(category);
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

        tryResetBirthDay(); // 새 생년월일 입력
    }

    private void tryResetBirthDay() throws Exception {

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
