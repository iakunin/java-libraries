package dev.iakunin.library.persistence.type;

import java.math.BigDecimal;
import java.util.stream.Stream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.javamoney.moneta.CurrencyUnitBuilder;
import org.javamoney.moneta.Money;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings(
    {
        "checkstyle:MethodLength",
        "PMD.ExcessiveMethodLength",
        "PMD.AvoidDuplicateLiterals",
    }
)
public final class MonetaryAmountInMinorUnitTypeTest {

    private static final String USD_CURRENCY_CODE = "USD";
    private static final String BITCOIN_CURRENCY_CODE = "XBT";
    private static final String SIXTEEN_CURRENCY_CODE = "SIXTEEN";
    private static final String THIRTY_TWO_CURRENCY_CODE = "THIRTY_TWO";

    static {
        CurrencyUnitBuilder.of(BITCOIN_CURRENCY_CODE, "default")
            .setDefaultFractionDigits(8)
            .build(true);

        CurrencyUnitBuilder.of(SIXTEEN_CURRENCY_CODE, "default")
            .setDefaultFractionDigits(16)
            .build(true);

        CurrencyUnitBuilder.of(THIRTY_TWO_CURRENCY_CODE, "default")
            .setDefaultFractionDigits(32)
            .build(true);
    }

    private static Stream<Arguments> toConvertedColumnsSource() {
        return Stream.of(
            Arguments.of(
                Money.of(BigDecimal.valueOf(100), USD_CURRENCY_CODE),
                new Object[] {BigDecimal.valueOf(10_000), USD_CURRENCY_CODE, }
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(0), USD_CURRENCY_CODE),
                new Object[] {BigDecimal.valueOf(0), USD_CURRENCY_CODE, }
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(1), USD_CURRENCY_CODE),
                new Object[] {BigDecimal.valueOf(100), USD_CURRENCY_CODE, }
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(123.99), USD_CURRENCY_CODE),
                new Object[] {BigDecimal.valueOf(12_399), USD_CURRENCY_CODE, }
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(456.11), USD_CURRENCY_CODE),
                new Object[] {BigDecimal.valueOf(45_611), USD_CURRENCY_CODE, }
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(123_456.78), USD_CURRENCY_CODE),
                new Object[] {BigDecimal.valueOf(12_345_678), USD_CURRENCY_CODE, }
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(123_456.789_999), USD_CURRENCY_CODE),
                new Object[] {BigDecimal.valueOf(12_345_678), USD_CURRENCY_CODE, }
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(2.234_567_89), BITCOIN_CURRENCY_CODE),
                new Object[] {BigDecimal.valueOf(223_456_789), BITCOIN_CURRENCY_CODE, }
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(9.234_567_891_23), BITCOIN_CURRENCY_CODE),
                new Object[] {BigDecimal.valueOf(923_456_789), BITCOIN_CURRENCY_CODE }
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(11.23), BITCOIN_CURRENCY_CODE),
                new Object[] {BigDecimal.valueOf(1_123_000_000), BITCOIN_CURRENCY_CODE }
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(3), SIXTEEN_CURRENCY_CODE),
                new Object[] {
                    new BigDecimal("3_0000_0000_0000_0000".replace("_", "")),
                    SIXTEEN_CURRENCY_CODE,
                }
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(2.1234), SIXTEEN_CURRENCY_CODE),
                new Object[] {
                    new BigDecimal("2_1234_0000_0000_0000".replace("_", "")),
                    SIXTEEN_CURRENCY_CODE,
                }
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234".replace("_", "")),
                    SIXTEEN_CURRENCY_CODE
                ),
                new Object[] {
                    new BigDecimal("2_1234_5678_1234_1234".replace("_", "")),
                    SIXTEEN_CURRENCY_CODE,
                }
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234_9999".replace("_", "")),
                    SIXTEEN_CURRENCY_CODE
                ),
                new Object[] {
                    new BigDecimal("2_1234_5678_1234_1234".replace("_", "")),
                    SIXTEEN_CURRENCY_CODE,
                }
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234_9999".replace("_", "")),
                    SIXTEEN_CURRENCY_CODE
                ),
                new Object[] {
                    new BigDecimal("2_1234_5678_1234_1234".replace("_", "")),
                    SIXTEEN_CURRENCY_CODE,
                }
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234".replace("_", "")),
                    THIRTY_TWO_CURRENCY_CODE
                ),
                new Object[] {
                    new BigDecimal("2_1234_5678_1234_1234_0000_0000_0000_0000".replace("_", "")),
                    THIRTY_TWO_CURRENCY_CODE,
                }
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal("2.1234_5678_1234_5678_1234_5678_1234_5678".replace("_", "")),
                    THIRTY_TWO_CURRENCY_CODE
                ),
                new Object[] {
                    new BigDecimal("2_1234_5678_1234_5678_1234_5678_1234_5678".replace("_", "")),
                    THIRTY_TWO_CURRENCY_CODE,
                }
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal(
                        "2.1234_5678_1234_5678_1234_5678_1234_5678_9999".replace("_", "")
                    ),
                    THIRTY_TWO_CURRENCY_CODE
                ),
                new Object[] {
                    new BigDecimal("2_1234_5678_1234_5678_1234_5678_1234_5678".replace("_", "")),
                    THIRTY_TWO_CURRENCY_CODE,
                }
            )
        );
    }

    @ParameterizedTest
    @MethodSource("toConvertedColumnsSource")
    @SuppressWarnings({"unchecked", "rawtypes", "PMD.UseVarargs", })
    public void toConvertedColumns(javax.money.MonetaryAmount money, Object[] expectedResult) {
        final MonetaryAmountInMinorUnitType type = new MonetaryAmountInMinorUnitType();

        for (int idx = 0; idx < expectedResult.length; idx++) {
            MatcherAssert.assertThat(
                type.getPropertyValue(money, idx),
                Matchers.comparesEqualTo((Comparable) expectedResult[idx])
            );
        }
    }

    private static Stream<Arguments> fromConvertedColumnsSource() {
        return Stream.of(
            Arguments.of(
                new Object[] {BigDecimal.valueOf(10_000), USD_CURRENCY_CODE, },
                Money.of(BigDecimal.valueOf(100), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {BigDecimal.valueOf(0), USD_CURRENCY_CODE, },
                Money.of(BigDecimal.valueOf(0), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {BigDecimal.valueOf(100), USD_CURRENCY_CODE, },
                Money.of(BigDecimal.valueOf(1), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {BigDecimal.valueOf(12_399), USD_CURRENCY_CODE, },
                Money.of(BigDecimal.valueOf(123.99), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {BigDecimal.valueOf(45_611), USD_CURRENCY_CODE, },
                Money.of(BigDecimal.valueOf(456.11), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {BigDecimal.valueOf(12_345_678), USD_CURRENCY_CODE, },
                Money.of(BigDecimal.valueOf(123_456.78), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {BigDecimal.valueOf(1_234_567_899), USD_CURRENCY_CODE, },
                Money.of(BigDecimal.valueOf(12_345_678.99), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {BigDecimal.valueOf(223_456_789), BITCOIN_CURRENCY_CODE, },
                Money.of(BigDecimal.valueOf(2.234_567_89), BITCOIN_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {BigDecimal.valueOf(923_456_789), BITCOIN_CURRENCY_CODE, },
                Money.of(BigDecimal.valueOf(9.234_567_89), BITCOIN_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {BigDecimal.valueOf(1_123_000_000), BITCOIN_CURRENCY_CODE, },
                Money.of(BigDecimal.valueOf(11.23), BITCOIN_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {
                    new BigDecimal("3_0000_0000_0000_0000".replace("_", "")),
                    SIXTEEN_CURRENCY_CODE,
                },
                Money.of(BigDecimal.valueOf(3), SIXTEEN_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {
                    new BigDecimal("2_1234_0000_0000_0000".replace("_", "")),
                    SIXTEEN_CURRENCY_CODE,
                },
                Money.of(BigDecimal.valueOf(2.1234), SIXTEEN_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {
                    new BigDecimal("2_1234_5678_1234_1234".replace("_", "")),
                    SIXTEEN_CURRENCY_CODE,
                },
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234".replace("_", "")),
                    SIXTEEN_CURRENCY_CODE
                )
            ),
            Arguments.of(
                new Object[] {
                    new BigDecimal("2_1234_5678_1234_1234".replace("_", "")),
                    SIXTEEN_CURRENCY_CODE,
                },
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234".replace("_", "")),
                    SIXTEEN_CURRENCY_CODE
                )
            ),
            Arguments.of(
                new Object[] {
                    new BigDecimal("2_1234_5678_1234_1234_0000_0000_0000_0000".replace("_", "")),
                    THIRTY_TWO_CURRENCY_CODE,
                },
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234_0000_0000_0000_0000".replace("_", "")),
                    THIRTY_TWO_CURRENCY_CODE
                )
            ),
            Arguments.of(
                new Object[] {
                    new BigDecimal("2_1234_5678_1234_5678_1234_5678_1234_5678".replace("_", "")),
                    THIRTY_TWO_CURRENCY_CODE,
                },
                Money.of(
                    new BigDecimal("2.1234_5678_1234_5678_1234_5678_1234_5678".replace("_", "")),
                    THIRTY_TWO_CURRENCY_CODE
                )
            ),
            Arguments.of(
                new Object[] {
                    new BigDecimal(
                        "2_1234_5678_1234_5678_1234_5678_1234_5678_9999".replace("_", "")
                    ),
                    THIRTY_TWO_CURRENCY_CODE,
                },
                Money.of(
                    new BigDecimal(
                        "2_1234.5678_1234_5678_1234_5678_1234_5678_9999".replace("_", "")
                    ),
                    THIRTY_TWO_CURRENCY_CODE
                )
            ),
            Arguments.of(
                new Object[] {BigDecimal.valueOf(1.1), USD_CURRENCY_CODE, },
                Money.of(BigDecimal.valueOf(0.01), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {BigDecimal.valueOf(1.9), USD_CURRENCY_CODE, },
                Money.of(BigDecimal.valueOf(0.01), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {
                    new BigDecimal(
                        "2_1234_5678_1234_5678_1234_5678_1234_5678_9999.1".replace("_", "")
                    ),
                    THIRTY_TWO_CURRENCY_CODE,
                },
                Money.of(
                    new BigDecimal(
                        "2_1234.5678_1234_5678_1234_5678_1234_5678_9999".replace("_", "")
                    ),
                    THIRTY_TWO_CURRENCY_CODE
                )
            ),
            Arguments.of(
                new Object[] {
                    new BigDecimal(
                        "2_1234_5678_1234_5678_1234_5678_1234_5678_9999.9".replace("_", "")
                    ),
                    THIRTY_TWO_CURRENCY_CODE,
                },
                Money.of(
                    new BigDecimal(
                        "2_1234.5678_1234_5678_1234_5678_1234_5678_9999".replace("_", "")
                    ),
                    THIRTY_TWO_CURRENCY_CODE
                )
            ),
            Arguments.of(
                new Object[] {
                    new BigDecimal(
                        "2_1234_5678_1234_5678_1234_5678_1234_5678_9999.9999".replace("_", "")
                    ),
                    THIRTY_TWO_CURRENCY_CODE,
                },
                Money.of(
                    new BigDecimal(
                        "2_1234.5678_1234_5678_1234_5678_1234_5678_9999".replace("_", "")
                    ),
                    THIRTY_TWO_CURRENCY_CODE
                )
            )
        );
    }

    @ParameterizedTest
    @MethodSource("fromConvertedColumnsSource")
    public void fromConvertedColumns(
        Object[] convertedColumns,
        javax.money.MonetaryAmount expectedResult
    ) {
        final MonetaryAmountInMinorUnitType type = new MonetaryAmountInMinorUnitType();

        final javax.money.MonetaryAmount actualResult =
            type.instantiate(
                () -> convertedColumns,
                null
            );

        MatcherAssert.assertThat(actualResult, Matchers.comparesEqualTo(expectedResult));
    }
}
