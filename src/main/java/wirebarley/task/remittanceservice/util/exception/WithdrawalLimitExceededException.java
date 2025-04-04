package wirebarley.task.remittanceservice.util.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class WithdrawalLimitExceededException extends IllegalArgumentException {
    public WithdrawalLimitExceededException(String message) {
        super(message);
    }
}
