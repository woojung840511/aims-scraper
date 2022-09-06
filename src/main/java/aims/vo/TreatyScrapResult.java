package aims.vo;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class TreatyScrapResult {

    String company;
    String category;
    String product;
    boolean success;

    public TreatyScrapResult(String company, String category, String product, boolean success) {
        this.company = company;
        this.category = category;
        this.product = product;
        this.success = success;
    }

    String errorMsg;

}
