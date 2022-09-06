package aims.scraper;

import aims.vo.TreatyScrapData;
import com.google.gson.Gson;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TargetCompnay {

    public final static Logger logger = LoggerFactory.getLogger(TargetCompnay.class);
    protected WebDriver driver;
    protected Helper helper;

    protected Condition condition;

    @Getter
    @Setter
    @ToString
    static class Condition {
        @NotNull(message = "url은 반드시 필요합니다.")
        private String url;

        private String categorySelected;
        private String productSelected;
    }

    public TargetCompnay(@Valid TargetCompnay.Condition condition) throws Exception {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        helper = new Helper(driver);
        this.condition = condition;
    }

    protected static final Map<String, List<String>> toDo = new HashMap<>();

    protected void stopDriver() {
        Set<String> windowHandles = driver.getWindowHandles();
        for (String windowHandle : windowHandles) {
            driver.switchTo().window(windowHandle);
            driver.close();
        }
        driver.quit();
    }

    protected void startDriver() {
        driver = new ChromeDriver();
        driver.get(condition.getUrl());
    }

    protected void sendData(List<TreatyScrapData> sendList) {
        String jsonData = new Gson().toJson(sendList);
        String url = "http://127.0.0.1:8081/treatyResearch";
//        String url = "https://api.nuzal.kr/treatyResearch";
        logger.info("전송 데이터: {}", jsonData);
        System.out.println();
//        HttpClientUtil.sendPOSTWithJwtToken(url, jsonData );
    }

}
