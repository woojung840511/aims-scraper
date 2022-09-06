package aims.scraper;

import aims.vo.ScrapResult;
import aims.vo.TreatyScrapData;
import com.google.gson.Gson;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TargetCompnay {

    public final static Logger logger = LoggerFactory.getLogger(TargetCompnay.class);
    protected static WebDriver driver;
    protected static Helper helper;
    protected String url;

    protected static final Map<String, List<String>> categoryProductsMap = new HashMap<>();

    @Getter
    @AllArgsConstructor
    @ToString
    static class ScrapTarget {
        String company;
        String category;
        String product;
        List<ScrapResult> resultList;
    }

    public TargetCompnay(String url) {
        this.url = url;
    }

    protected void stop() {
        Set<String> windowHandles = driver.getWindowHandles();
        for (String windowHandle : windowHandles) {
            driver.switchTo().window(windowHandle);
            driver.close();
        }
        driver.quit();
    }

    protected void start() throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        helper = new Helper(driver);
        driver.get(url);
    }

    protected static void sendData(List<TreatyScrapData> sendList) {
        String jsonData = new Gson().toJson(sendList);
        String url = "http://127.0.0.1:8081/treatyResearch";
//        String url = "https://api.nuzal.kr/treatyResearch";
        logger.info("전송 데이터: {}", jsonData);
        System.out.println();
//        HttpClientUtil.sendPOSTWithJwtToken(url, jsonData );
    }

}
