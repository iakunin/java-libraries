package dev.iakunin.library.tests;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.MvcResult;

@UtilityClass
public class AssertionUtils {

    private static final int OK_STATUS_CODE = 200;

    public static void assertMvcResultOk(MvcResult result) {
        assertMvcResultExpected(result, OK_STATUS_CODE);
    }

    public static void assertMvcResultExpected(MvcResult result, Integer expectedStatus) {
        Assertions.assertEquals(
            expectedStatus,
            result.getResponse().getStatus(),
            result.getResolvedException() != null
                ? ExceptionUtils.getStackTrace(result.getResolvedException())
                : ""
        );
    }
}
