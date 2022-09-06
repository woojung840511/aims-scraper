package aims.vo;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class ScrapResult {

    Object o;
    boolean success;

    public ScrapResult(Object o, boolean success) {
        this.o = o;
        this.success = success;
    }

    String errorMsg;

}
