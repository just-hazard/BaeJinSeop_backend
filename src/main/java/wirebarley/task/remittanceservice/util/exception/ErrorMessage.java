package wirebarley.task.remittanceservice.util.exception;

public class ErrorMessage {
    public static final String DATA_ALREADY_EXISTS = "이미 데이터가 존재합니다";
    public static final String ACCOUNT_NOT_EXISTS = "계좌가 존재하지 않습니다";
    public static final String BALANCE_NOT_EMPTY = "금액이 존재하므로 계좌를 삭제 할 수 없습니다";
    public static final String INSUFFICIENT_BALANCE = "잔액이 부족합니다";
    public static final String WITHDRAWAL_LIMIT_EXCEEDED = "당일 출금 한도를 초과하였습니다";
    public static final String TRANSFER_LIMIT_EXCEEDED = "당일 이체 한도를 초과하였습니다";
}
