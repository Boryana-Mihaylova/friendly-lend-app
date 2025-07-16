package app.web;
import app.exception.UsernameAlreadyExistException;
import app.web.mapper.ExceptionAdvice;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.nio.file.AccessDeniedException;

import static org.assertj.core.api.Assertions.assertThat;
public class ExceptionAdviceTest {

    private final ExceptionAdvice advice = new ExceptionAdvice();

    @Test
    void testHandleUsernameAlreadyExist() {
        HttpServletRequest request = new MockHttpServletRequest();
        RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

        String result = advice.handleUsernameAlreadyExist(
                request,
                redirectAttributes,
                new UsernameAlreadyExistException("Username already exists.")
        );

        assertThat(result).isEqualTo("redirect:/register");
        assertThat(redirectAttributes.getFlashAttributes().get("usernameAlreadyExistMessage"))
                .isEqualTo("This username is already in use!");
    }

    @Test
    void testHandleNotFoundExceptions_withAccessDeniedException() throws Exception {
        ModelAndView result = advice.handleNotFoundExceptions(new AccessDeniedException("Denied"));

        assertThat(result.getViewName()).isEqualTo("not-found");
    }



    @Test
    void testHandleNotFoundExceptions_withTypeMismatch() {
        ModelAndView result = advice.handleNotFoundExceptions(
                new MethodArgumentTypeMismatchException(null, null, "id", null, null)
        );

        assertThat(result.getViewName()).isEqualTo("not-found");
    }

    @Test
    void testHandleAnyException() {
        Exception exception = new NullPointerException("Something went wrong");
        ModelAndView result = advice.handleAnyException(exception);

        assertThat(result.getViewName()).isEqualTo("internal-server-error");
        assertThat(result.getModel()).containsEntry("errorMessage", "NullPointerException");
    }
}
