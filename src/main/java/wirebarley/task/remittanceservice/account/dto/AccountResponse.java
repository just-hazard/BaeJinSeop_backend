package wirebarley.task.remittanceservice.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wirebarley.task.remittanceservice.account.domain.Account;
import wirebarley.task.remittanceservice.util.TimeUtil;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Schema(description = "계좌 생성 응답")
public class AccountResponse {
    @Schema(description = "PK", example = "1")
    private Long id;
    @Schema(description = "생성된 계좌 번호", example = "1234")
    private String accountNumber;
    @Schema(description = "초기 입금 금액", example = "100000")
    private BigDecimal balance;
    @Schema(description = "생성된 계좌의 소유자 이름", example = "홍길동")
    private String name;
    @Schema(description = "생성 날짜", example = "2025-04-06T00:00:00.513618+09:00")
    private String createdDate;

    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getName(),
                TimeUtil.convertToZonedDateTime(account.getCreatedDate())
        );
    }
}
