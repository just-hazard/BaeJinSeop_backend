package wirebarley.task.remittanceservice.util.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDuplicateKeyException(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorMessage.DATA_ALREADY_EXISTS);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorMessage.ACCOUNT_NOT_EXISTS);
    }

    @ExceptionHandler(BalanceNotEmptyException.class)
    public ResponseEntity<String> handleBalanceNotEmptyException(BalanceNotEmptyException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorMessage.BALANCE_NOT_EMPTY);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<String> handleInsufficientBalanceException(InsufficientBalanceException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorMessage.INSUFFICIENT_BALANCE);
    }

    @ExceptionHandler(WithdrawalLimitExceededException.class)
    public ResponseEntity<String> handleWithdrawalLimitExceededException(WithdrawalLimitExceededException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorMessage.WITHDRAWAL_LIMIT_EXCEEDED);
    }

    @ExceptionHandler(TransferLimitExceededException.class)
    public ResponseEntity<String> handleTransferLimitExceededException(TransferLimitExceededException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorMessage.TRANSFER_LIMIT_EXCEEDED);
    }
}
