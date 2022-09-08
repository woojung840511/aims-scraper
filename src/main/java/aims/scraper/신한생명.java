package aims.scraper;

import static aims.util.InsuranceUtil.getBirthday;

import aims.util.WaitUtil;
import aims.vo.TreatyScrapData;
import aims.vo.ScrapResult;

import java.util.*;
import java.util.Map.Entry;

import aims.vo.TypeScrapData;
import com.google.gson.Gson;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class 신한생명 extends TargetCompnay {

    public final static Logger logger = LoggerFactory.getLogger(신한생명.class);

    @AllArgsConstructor
    @Getter
    @ToString
    static class ProductType {
        String key;
        List<String> options;
        ProductType next;
    }

    public 신한생명(String url) {
        super(url);
    }

    public static void main(String[] args) {
        try {

            신한생명 신한생명 = new 신한생명("https://www.shinhanlife.co.kr/hp/cdhi0290.do#");
            신한생명.scrap();

        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("크롤링 에러: " + e.getMessage());
        }
    }

    @Override
    protected void scrapStart() throws Exception {
        super.scrapStart();
        clickFirstCalcBtn();
    }

    private Map<String, List<String>> setScrapRange() {

        Map<String, List<String>> scrapRangeMap = new HashMap<>();

        List<String> categories = new ArrayList<>();
        categories.add("종신/정기");
        categories.add("건강보험");
        categories.add("상해");
        categories.add("어린이");
        categories.forEach(c -> {
            scrapRangeMap.put(c, new ArrayList<>());
        });

        List<String> prdListSelected = null;
/*        prdListSelected = Arrays.asList(
            "특정 상품명",
            "특정 상품명"
        );*/

        try {
            scrapStart();

            for (String category : scrapRangeMap.keySet()) {
                selectCategory(category);

                Select productSelect = new Select(driver.findElement(
                        By.xpath("//div[@class='dataSrchBox']/select[@title='상품명']")));

                List<String> prdList = getOptionStrings(productSelect);

                if (prdListSelected != null && prdListSelected.size() > 0) {
                    prdList.retainAll(prdListSelected);
                }
                scrapRangeMap.put(category, prdList);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            stop();

            int count = 0;
            for (Entry<String, List<String>> entry : scrapRangeMap.entrySet()) {

                logger.info("카테고리: {} ({}개)", entry.getKey(), entry.getValue().size());
                count += entry.getValue().size();
            }
            logger.info("총 {} 개", count);
        }

        return scrapRangeMap;
    }

    protected void scrap() throws Exception {

        // 스크랩 범위
        // key: 카테고리 (ex: 종신, 연금..)
        // value: 카테고리에 속한 상품명 리스트
        Map<String, List<String>> scrapRangeMap = setScrapRange();

        // 스크랩 성공 여부 기록 객체
        List<ScrapResult> scrapResults = new ArrayList<>();

        // 스크랩 범위 loop
        for (String category : scrapRangeMap.keySet()) {
            for (String product : scrapRangeMap.get(category)) {

                logger.info("카테고리: {} \\t 상품: {}", category, product);

                // 스크랩 성공 여부 기록할 스크랩 단위 ?
                ScrapTarget scrapTarget =
                        new ScrapTarget(this.getClass().getSimpleName(), category, product, scrapResults);

                scrapStart();
                boolean success = false;

                try {
                    selectCategory(category);   // 카테고리 설정
                    selectProduct(product);     // 상품 설정
                    setUserInfo();              // 사용자 정보 설정 (가입 가능 연령 설정)

                    // 상품에 속한 타입요소 스크랩후 api 전송함
                    // 상품에 속한 타입요소 중 보험종류, 보험형태 셀렉트박스 옵션 내용만 map 형태로 리턴
                    // key: 보험종류, 혹은 보험형태
                    // value: 보험종류 혹은 보험형태에 속하는 옵션값들
                    Map<String, List<String>> typeMap = scrapTypesOfProduct(category, product);

                    Iterator<Map.Entry<String, List<String>>> iterator = typeMap.entrySet().iterator();

                    ProductType productType = null;
                    while (iterator.hasNext()) {
                        Map.Entry<String, List<String>> current = iterator.next();
                        ProductType p = new ProductType(
                                current.getKey(),
                                current.getValue(),
                                productType);
                        productType = p;
                    }

                    success = scrapTreatyOfProduct(category, product, productType);

                } catch (InterruptedException e) {

                    logger.info("error message {}", e.getMessage());
                    logger.info("실패! 카테고리: {} 상품: {}", category, product);
                    throw new RuntimeException(e);

                } finally {

                    stop();
                    scrapResults.add(
                            new ScrapResult(scrapTarget, success));
                }

            }
        }

        scrapResults.forEach(r -> logger.info(r.toString()));
    }

    private Map<String, List<String>> scrapTypesOfProduct(String category, String product) {
        Map<String, List<String>> typeMap = new HashMap<>();

        try {
            Map<String, List<String>> typeOptionMap = new HashMap<>();
            List<WebElement> liList = helper.waitPesenceOfAllElementsLocatedBy(
                            By.xpath("//div[@id='mnprDivision'] // li")).stream()
                    .filter(WebElement::isDisplayed).toList();

            for (WebElement li : liList) {

                String title = li.findElement(By.cssSelector("div.item")).getText();
                WebElement div = li.findElement(By.cssSelector("div.val"));

                List<String> optionList = null;
                try {
                    optionList = new Select(
                            div.findElement(By.tagName("select")))
                            .getOptions().stream()
                            .map(WebElement::getText).toList();

                    typeOptionMap.put(title, optionList);
                } catch (NoSuchElementException ignore) {
                    logger.info("select tag가 아님", ignore);
                }

                if ((title.equals("보험종류") || title.equals("보험형태")) && optionList != null) {
                    typeMap.put(title, optionList);
                }
            }

            TypeScrapData 신한생명 = new TypeScrapData("신한생명", category, product, typeOptionMap);
            sendTypeOfProduct(신한생명);

        } catch (Exception e) {
            logger.debug(e.getMessage());
        }

        return typeMap;
    }

    private void sendTypeOfProduct(TypeScrapData typeScrapData) {
        String jsonData = new Gson().toJson(typeScrapData);
        String url = "http://127.0.0.1:8081/treatyResearch";
//        String url = "https://api.nuzal.kr/treatyResearch";
        logger.info("전송 데이터: {}", jsonData);
        System.out.println();
//        HttpClientUtil.sendPOSTWithJwtToken(url, jsonData );

    }

    private List<TreatyScrapData> selectTypeAndScrap(ProductType productType, List<Map<String, String>> selectedItemList ) throws Exception {
        List<TreatyScrapData> result;

        if (productType != null) {
            List<String> options = productType.getOptions();

            for (String option : options) {
                String key = productType.getKey();

                logger.info("productType : {}", productType);
                expandMainTreatyAcoBtn();

                // 타입 셀렉트 박스 특정 옵션 선택
                selectProductType(new Select(driver.findElement(
                        By.xpath("//select[@title='" + key + "']"))), option);
                helper.waitForLoading();

                Map<String, String> map = new HashMap<>();
                map.put(key, option);
                selectedItemList.add(map);

                // 재귀 호출
                selectTypeAndScrap(productType.getNext(), selectedItemList);
            }

        }

        // 주계약 확인 버튼
        helper.doClick(
                By.cssSelector("#mnprDivision > div.btnArea.btnLoCtl > span > button"));
        tryResetBirthDayIfNeeded();

        result = getTreatyList(new Gson().toJson(selectedItemList));

        // 특약 긁고 리턴
        return result;

    }

    private boolean scrapTreatyOfProduct(String category, String product, ProductType productType) {

        boolean success = false;

        try {
            List<TreatyScrapData> treatyList = new ArrayList<>();
            List<TreatyScrapData> treatyScrapData = selectTypeAndScrap(productType, new ArrayList<>());

            // todo treatyScrapData 필요정보 셋팅할 것
            // todo 중복제거 프로세스 필요함
            // todo selectTypeAndScrap

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


    private List<TreatyScrapData> getTreatyList(String productType) {
        List<TreatyScrapData> list = new ArrayList<>();
        helper.waitPesenceOfAllElementsLocatedBy(
                        By.xpath("//tbody[@class='intyList']//td[@title='가입상품']"))
                .forEach(td -> {
                    list.add(new TreatyScrapData(productType, td.getText()));
                });
        return list;
    }

    private void selectProduct(String product) {
        selectProductType(new Select(driver.findElement(
                By.xpath("//div[@class='dataSrchBox']/select[@title='상품명']"))), product);
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
