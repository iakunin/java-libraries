package dev.iakunin.library.persistence.converter;

import java.math.BigDecimal;
import java.util.stream.Stream;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
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
public final class MonetaryAmountTest {

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

    private static Stream<Arguments> toConvertedColumnsSource() {
        return Stream.of(
            Arguments.of(
                Money.of(BigDecimal.valueOf(100), USD_CURRENCY_CODE),
                new Object[] {
                    Monetary.getCurrency(USD_CURRENCY_CODE),
                    BigDecimal.valueOf(10000),
                }
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(0), USD_CURRENCY_CODE),
                new Object[] {
                    Monetary.getCurrency(USD_CURRENCY_CODE),
                    BigDecimal.valueOf(0),
                }
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(1), USD_CURRENCY_CODE),
                new Object[] {
                    Monetary.getCurrency(USD_CURRENCY_CODE),
                    BigDecimal.valueOf(100),
                }
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(123.99), USD_CURRENCY_CODE),
                new Object[] {
                    Monetary.getCurrency(USD_CURRENCY_CODE),
                    BigDecimal.valueOf(12399),
                }
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(456.11), USD_CURRENCY_CODE),
                new Object[] {
                    Monetary.getCurrency(USD_CURRENCY_CODE),
                    BigDecimal.valueOf(45611),
                }
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(123456.78), USD_CURRENCY_CODE),
                new Object[] {
                    Monetary.getCurrency(USD_CURRENCY_CODE),
                    BigDecimal.valueOf(12345678),
                }
            ),
            Arguments.of(
                Money.of(BigDecimal.valueOf(123_456.789_999), USD_CURRENCY_CODE),
                new Object[] {
                    Monetary.getCurrency(USD_CURRENCY_CODE),
                    BigDecimal.valueOf(123_456_78),
                }
            ),
            Arguments.of(
                Money.of(
                    BigDecimal.valueOf(2.2345_6789),
                    MonetaryAmountTest.BITCOIN
                ),
                new Object[] {
                    MonetaryAmountTest.BITCOIN,
                    BigDecimal.valueOf(2_2345_6789),
                }
            ),
            Arguments.of(
                Money.of(
                    BigDecimal.valueOf(9.2345_6789_123),
                    MonetaryAmountTest.BITCOIN
                ),
                new Object[] {
                    MonetaryAmountTest.BITCOIN,
                    BigDecimal.valueOf(9_2345_6789),
                }
            ),
            Arguments.of(
                Money.of(
                    BigDecimal.valueOf(11.23),
                    MonetaryAmountTest.BITCOIN
                ),
                new Object[] {
                    MonetaryAmountTest.BITCOIN,
                    BigDecimal.valueOf(11_23_000_000),
                }
            ),
            Arguments.of(
                Money.of(
                    BigDecimal.valueOf(3),
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION
                ),
                new Object[] {
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION,
                    new BigDecimal("3_0000_0000_0000_0000".replace("_", "")),
                }
            ),
            Arguments.of(
                Money.of(
                    BigDecimal.valueOf(2.1234),
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION
                ),
                new Object[] {
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION,
                    new BigDecimal("2_1234_0000_0000_0000".replace("_", "")),
                }
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234".replace("_", "")),
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION
                ),
                new Object[] {
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION,
                    new BigDecimal("2_1234_5678_1234_1234".replace("_", "")),
                }
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234_9999".replace("_", "")),
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION
                ),
                new Object[] {
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION,
                    new BigDecimal("2_1234_5678_1234_1234".replace("_", "")),
                }
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234_9999".replace("_", "")),
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION
                ),
                new Object[] {
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION,
                    new BigDecimal("2_1234_5678_1234_1234".replace("_", "")),
                }
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234".replace("_", "")),
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION
                ),
                new Object[] {
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION,
                    new BigDecimal("2_1234_5678_1234_1234_0000_0000_0000_0000".replace("_", "")),
                }
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal("2.1234_5678_1234_5678_1234_5678_1234_5678".replace("_", "")),
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION
                ),
                new Object[] {
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION,
                    new BigDecimal("2_1234_5678_1234_5678_1234_5678_1234_5678".replace("_", "")),
                }
            ),
            Arguments.of(
                Money.of(
                    new BigDecimal(
                        "2.1234_5678_1234_5678_1234_5678_1234_5678_9999".replace("_", "")
                    ),
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION
                ),
                new Object[] {
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION,
                    new BigDecimal("2_1234_5678_1234_5678_1234_5678_1234_5678".replace("_", "")),
                }
            )
        );
    }

    @ParameterizedTest
    @MethodSource("toConvertedColumnsSource")
    @SuppressWarnings({"unchecked", "rawtypes", "PMD.UseVarargs", })
    public void toConvertedColumns(javax.money.MonetaryAmount money, Object[] expectedResult) {
        final MonetaryAmount converter = new MonetaryAmount();

        final Object[] actualResult = converter.toConvertedColumns(money);

        for (int idx = 0; idx < actualResult.length; idx++) {
            MatcherAssert.assertThat(
                actualResult[idx],
                Matchers.comparesEqualTo((Comparable) expectedResult[idx])
            );
        }
    }

    private static Stream<Arguments> fromConvertedColumnsSource() {
        return Stream.of(
            Arguments.of(
                new Object[] {
                    Monetary.getCurrency(USD_CURRENCY_CODE),
                    BigDecimal.valueOf(10000),
                },
                Money.of(BigDecimal.valueOf(100), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {
                    Monetary.getCurrency(USD_CURRENCY_CODE),
                    BigDecimal.valueOf(0),
                },
                Money.of(BigDecimal.valueOf(0), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {
                    Monetary.getCurrency(USD_CURRENCY_CODE),
                    BigDecimal.valueOf(100),
                },
                Money.of(BigDecimal.valueOf(1), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {
                    Monetary.getCurrency(USD_CURRENCY_CODE),
                    BigDecimal.valueOf(12399),
                },
                Money.of(BigDecimal.valueOf(123.99), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {
                    Monetary.getCurrency(USD_CURRENCY_CODE),
                    BigDecimal.valueOf(45611),
                },
                Money.of(BigDecimal.valueOf(456.11), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {
                    Monetary.getCurrency(USD_CURRENCY_CODE),
                    BigDecimal.valueOf(12345678),
                },
                Money.of(BigDecimal.valueOf(123456.78), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {
                    Monetary.getCurrency(USD_CURRENCY_CODE),
                    BigDecimal.valueOf(12345678_99),
                },
                Money.of(BigDecimal.valueOf(12345678.99), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {
                    MonetaryAmountTest.BITCOIN,
                    BigDecimal.valueOf(2_2345_6789),
                },
                Money.of(
                    BigDecimal.valueOf(2.2345_6789),
                    MonetaryAmountTest.BITCOIN
                )
            ),
            Arguments.of(
                new Object[] {
                    MonetaryAmountTest.BITCOIN,
                    BigDecimal.valueOf(9_2345_6789),
                },
                Money.of(
                    BigDecimal.valueOf(9.2345_6789),
                    MonetaryAmountTest.BITCOIN
                )
            ),
            Arguments.of(
                new Object[] {
                    MonetaryAmountTest.BITCOIN,
                    BigDecimal.valueOf(11_23_000_000),
                },
                Money.of(
                    BigDecimal.valueOf(11.23),
                    MonetaryAmountTest.BITCOIN
                )
            ),
            Arguments.of(
                new Object[] {
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION,
                    new BigDecimal("3_0000_0000_0000_0000".replace("_", "")),
                },
                Money.of(
                    BigDecimal.valueOf(3),
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION
                )
            ),
            Arguments.of(
                new Object[] {
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION,
                    new BigDecimal("2_1234_0000_0000_0000".replace("_", "")),
                },
                Money.of(
                    BigDecimal.valueOf(2.1234),
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION
                )
            ),
            Arguments.of(
                new Object[] {
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION,
                    new BigDecimal("2_1234_5678_1234_1234".replace("_", "")),
                },
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234".replace("_", "")),
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION
                )
            ),
            Arguments.of(
                new Object[] {
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION,
                    new BigDecimal("2_1234_5678_1234_1234".replace("_", "")),
                },
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234".replace("_", "")),
                    MonetaryAmountTest.CURRENCY_SIXTEEN_PRECISION
                )
            ),
            Arguments.of(
                new Object[] {
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION,
                    new BigDecimal("2_1234_5678_1234_1234_0000_0000_0000_0000".replace("_", "")),
                },
                Money.of(
                    new BigDecimal("2.1234_5678_1234_1234_0000_0000_0000_0000".replace("_", "")),
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION
                )
            ),
            Arguments.of(
                new Object[] {
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION,
                    new BigDecimal("2_1234_5678_1234_5678_1234_5678_1234_5678".replace("_", "")),
                },
                Money.of(
                    new BigDecimal("2.1234_5678_1234_5678_1234_5678_1234_5678".replace("_", "")),
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION
                )
            ),
            Arguments.of(
                new Object[] {
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION,
                    new BigDecimal(
                        "2_1234_5678_1234_5678_1234_5678_1234_5678_9999".replace("_", "")
                    ),
                },
                Money.of(
                    new BigDecimal(
                        "2_1234.5678_1234_5678_1234_5678_1234_5678_9999".replace("_", "")
                    ),
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION
                )
            ),
            Arguments.of(
                new Object[] {
                    Monetary.getCurrency(USD_CURRENCY_CODE),
                    BigDecimal.valueOf(1.1),
                },
                Money.of(BigDecimal.valueOf(0.01), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {
                    Monetary.getCurrency(USD_CURRENCY_CODE),
                    BigDecimal.valueOf(1.9),
                },
                Money.of(BigDecimal.valueOf(0.01), USD_CURRENCY_CODE)
            ),
            Arguments.of(
                new Object[] {
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION,
                    new BigDecimal(
                        "2_1234_5678_1234_5678_1234_5678_1234_5678_9999.1".replace("_", "")
                    ),
                },
                Money.of(
                    new BigDecimal(
                        "2_1234.5678_1234_5678_1234_5678_1234_5678_9999".replace("_", "")
                    ),
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION
                )
            ),
            Arguments.of(
                new Object[] {
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION,
                    new BigDecimal(
                        "2_1234_5678_1234_5678_1234_5678_1234_5678_9999.9".replace("_", "")
                    ),
                },
                Money.of(
                    new BigDecimal(
                        "2_1234.5678_1234_5678_1234_5678_1234_5678_9999".replace("_", "")
                    ),
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION
                )
            ),
            Arguments.of(
                new Object[] {
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION,
                    new BigDecimal(
                        "2_1234_5678_1234_5678_1234_5678_1234_5678_9999.9999".replace("_", "")
                    ),
                },
                Money.of(
                    new BigDecimal(
                        "2_1234.5678_1234_5678_1234_5678_1234_5678_9999".replace("_", "")
                    ),
                    MonetaryAmountTest.CURRENCY_THIRTY_TWO_PRECISION
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
        final MonetaryAmount converter = new MonetaryAmount();

        final javax.money.MonetaryAmount actualResult =
            converter.fromConvertedColumns(convertedColumns);

        MatcherAssert.assertThat(actualResult, Matchers.comparesEqualTo(expectedResult));
    }
}
