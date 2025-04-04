package wirebarley.task.remittanceservice.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BankUtil {
    private final static BigDecimal TRANSFER_FEE_RATE = new BigDecimal("0.01");

    public static BigDecimal calculateTransferFee(BigDecimal transferAmount) {
        return transferAmount.multiply(TRANSFER_FEE_RATE);
    }

    public static BigDecimal removeDecimalPoint(BigDecimal amount) {
        return amount.setScale(0, RoundingMode.HALF_UP);
    }
}
