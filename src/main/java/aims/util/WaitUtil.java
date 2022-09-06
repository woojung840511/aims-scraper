package aims.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaitUtil {

    public static final Logger logger = LoggerFactory.getLogger(WaitUtil.class);

    // 기본 time sleep=4초
    private static final int DEFAULT_SLEEP_TIME = 4;

    // 기본 milli second=1000
    private static final int DEFAULT_MILLI_SEC = 1000;

    /**
     * 기본 4초를 카운트하며, time sleep 한다.
     *
     * @throws InterruptedException
     */
    public static void loading() throws InterruptedException {
        loading(DEFAULT_SLEEP_TIME);
    }

    /**
     * 원하는 시간(초)동안 를 카운트하며, time sleep 한다.
     *
     * @throws InterruptedException
     */
    public static void loading(int sec) throws InterruptedException {
        for (int i = 1; i <= sec; i++) {
            logger.debug("loading.. " + i);
            Thread.sleep(DEFAULT_MILLI_SEC);
        }
    }

    /**
     * 기본 4초를 time sleep 한다.
     *
     * @throws InterruptedException
     */
    public static void waitFor() throws InterruptedException {
        int waitTime = DEFAULT_SLEEP_TIME;
        waitFor(waitTime);
    }

    /**
     * 호출 위치(loc)를 표시하여 4초를 대기한다.
     */
    public static void waitFor(String loc) throws InterruptedException {
        logger.debug("{}.wait for...{}s", loc, (DEFAULT_SLEEP_TIME));
        Thread.sleep(DEFAULT_SLEEP_TIME * DEFAULT_MILLI_SEC);
    }

    /**
     * 원하는 시간(초)동안 time sleep 한다.
     *
     * @throws InterruptedException
     */
    public static void waitFor(int sec) throws InterruptedException {
        logger.debug("waiting for.. {}s", sec);
        Thread.sleep(sec * DEFAULT_MILLI_SEC);
    }

    public static void mSecLoading(int mSec) throws InterruptedException {
        int count = mSec / 100;
        logger.debug("Loading ");
        for (int i = 1; i <= count; i++) {
            System.out.print(".");
            Thread.sleep(100);
        }
        logger.debug("");
    }

}
