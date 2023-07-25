package dev.iakunin.library.persistence.converter;

import dev.iakunin.library.persistence.AbstractIntegrationTest;
import dev.iakunin.library.persistence.entity.TestEntity;
import dev.iakunin.library.persistence.repository.TestEntityRepository;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.stream.Stream;
import javax.money.CurrencyUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.javamoney.moneta.CurrencyUnitBuilder;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Slf4j
@SuppressWarnings(
    {
        "PMD.AvoidThrowingRawExceptionTypes",
        "PMD.ExcessiveMethodLength",
    }
)
public final class MonetaryAmountIntegrationTest extends AbstractIntegrationTest {

    private static final String USD_CURRENCY_CODE = "USD";

    private static final CurrencyUnit BITCOIN = CurrencyUnitBuilder.of(
        "XBT",
        "default"
    )
        .setDefaultFractionDigits(8)
        .build(true);

    private static final CurrencyUnit CURRENCY_SIXTEEN_PRECISION = CurrencyUnitBuilder.of(
        "SIXTEEN",
        "default"
    )
        .setDefaultFractionDigits(16)
        .build(true);

    private static final CurrencyUnit CURRENCY_THIRTY_TWO_PRECISION = CurrencyUnitBuilder.of(
        "THIRTY_TWO",
        "default"
    )
        .setDefaultFractionDigits(32)
        .build(true);

    @Autowired
    private TestEntityRepository repository;

    private static Stream<Arguments> testAmountSource() {
        return Stream.of(
            Arguments.of(
                Money.of(BigDecimal.valueOf(100), USD_CURRENCY_CODE),
                BigDecimal.valueOf(10_000)
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(0), USD_CURRENCY_CODE),
                BigDecimal.valueOf(0)
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(1), USD_CURRENCY_CODE),
                BigDecimal.valueOf(100)
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(123.99), USD_CURRENCY_CODE),
                BigDecimal.valueOf(12_399)
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(456.11), USD_CURRENCY_CODE),
                BigDecimal.valueOf(45_611)
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(123_456.78), USD_CURRENCY_CODE),
                BigDecimal.valueOf(12_345_678)
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(123_456.789_999), USD_CURRENCY_CODE),
                BigDecimal.valueOf(12_345_678)
            ),
            Arguments.of(
                Money.of(
                    BigDecimal.valueOf(2.234_567_89),
                    MonetaryAmountIntegrationTest.BITCOIN
                ),
                BigDecimal.valueOf(223_456_789)
            ),
            Arguments.of(
                Money.of(
                    BigDecimal.valueOf(9.234_567_891_23),
                    MonetaryAmountIntegrationTest.BITCOIN
                ),
                BigDecimal.valueOf(923_456_789)
            ),
            Arguments.of(
                Money.of(
                    BigDecimal.valueOf(11.23),
                    MonetaryAmountIntegrationTest.BITCOIN
                ),
                BigDecimal.valueOf(1_123_000_000)
            ),
            Arguments.of(
                Money.of(
                    BigDecimal.valueOf(3),
                    MonetaryAmountIntegrationTest.CURRENCY_SIXTEEN_PRECISION
                ),
                new BigDecimal("3_0000_0000_0000_0000".replace("_", ""))
            ),
            Arguments.of(
                Money.of(
                    BigDecimal.valueOf(2.1234),
                    MonetaryAmountIntegrationTest.CURRENCY_SIXTEEN_PRECISION
                ),
                new BigDecimal("2_1234_0000_0000_0000".replace("_", ""))
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234".replace("_", "")),
                    MonetaryAmountIntegrationTest.CURRENCY_SIXTEEN_PRECISION
                ),
                new BigDecimal("2_1234_5678_1234_1234".replace("_", ""))
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234_9999".replace("_", "")),
                    MonetaryAmountIntegrationTest.CURRENCY_SIXTEEN_PRECISION
                ),
                new BigDecimal("2_1234_5678_1234_1234".replace("_", ""))
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234_9999".replace("_", "")),
                    MonetaryAmountIntegrationTest.CURRENCY_SIXTEEN_PRECISION
                ),
                new BigDecimal("2_1234_5678_1234_1234".replace("_", ""))
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234".replace("_", "")),
                    MonetaryAmountIntegrationTest.CURRENCY_THIRTY_TWO_PRECISION
                ),
                new BigDecimal("2_1234_5678_1234_1234_0000_0000_0000_0000".replace("_", ""))
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal("2.1234_5678_1234_5678_1234_5678_1234_5678".replace("_", "")),
                    MonetaryAmountIntegrationTest.CURRENCY_THIRTY_TWO_PRECISION
                ),
                new BigDecimal("2_1234_5678_1234_5678_1234_5678_1234_5678".replace("_", ""))
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal(
                        "2.1234_5678_1234_5678_1234_5678_1234_5678_9999".replace("_", "")
                    ),
                    MonetaryAmountIntegrationTest.CURRENCY_THIRTY_TWO_PRECISION
                ),
                new BigDecimal("2_1234_5678_1234_5678_1234_5678_1234_5678".replace("_", ""))
            )
        );
    }

    @ParameterizedTest
    @MethodSource("testAmountSource")
    public void testAmount(Money moneyAmount, BigDecimal expectedAmount) {
        final TestEntity entity = createEntity(moneyAmount);

        saveEntity(entity);

        assertAmount(entity, expectedAmount);
        assertCurrency(entity, moneyAmount.getCurrency());
    }

    private TestEntity createEntity(Money moneyAmount) {
        return new TestEntity().setMoneyAmount(moneyAmount);
    }

    private void saveEntity(TestEntity entity) {
        repository.save(entity);
        repository.flush();
    }

    @SneakyThrows
    private void assertAmount(TestEntity entity, BigDecimal expectedAmount) {
        final String sql = "SELECT amount FROM test_entity WHERE id = ?";

        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(sql)) {
            preparedStatement.setLong(1, entity.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                final BigDecimal actualAmount = resultSet.getBigDecimal("amount");
                MatcherAssert.assertThat(
                    actualAmount,
                    Matchers.comparesEqualTo(expectedAmount)
                );
            }
        }
    }

    @SneakyThrows
    private void assertCurrency(TestEntity entity, CurrencyUnit expectedCurrency) {
        final String sql = "SELECT currency FROM test_entity WHERE id = ?";

        try (PreparedStatement preparedStatement = CONNECTION.prepareStatement(sql)) {
            preparedStatement.setLong(1, entity.getId());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                final String actualCurrencyCode = resultSet.getString("currency");
                MatcherAssert.assertThat(
                    actualCurrencyCode,
                    Matchers.equalTo(expectedCurrency.getCurrencyCode())
                );
            }
        }
    }
}
