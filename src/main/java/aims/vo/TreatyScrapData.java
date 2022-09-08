package aims.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;


@Setter
public class TreatyScrapData {

    String company;

    String category;

    String product;

    String productType;

    String treaty;

    public TreatyScrapData(String productType, String treaty) {
        this.productType = productType;
        this.treaty = treaty;
    }
}


