package aims.vo;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@ToString
public class TypeScrapData {

    String company;
    String category;
    String product;
    Map<String, List<String>> typeOptionMap;
}
