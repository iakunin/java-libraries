package dev.iakunin.library.persistence.converter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.money.CurrencyUnit;
import javax.money.RoundingQueryBuilder;
import org.jadira.usertype.moneyandcurrency.legacyjdk.columnmapper.BigDecimalBigDecimalColumnMapper;
import org.jadira.usertype.moneyandcurrency.moneta.columnmapper.StringColumnCurrencyUnitMapper;
import org.jadira.usertype.spi.shared.AbstractMultiColumnUserType;
import org.jadira.usertype.spi.shared.ColumnMapper;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.spi.DefaultRoundingProvider;

/**
 * Persists the decimal amount and currency from a Money instance. The only difference between this
 * class and {@link org.jadira.usertype.moneyandcurrency.moneta.PersistentMoneyAmountAndCurrency} is
 * the normalisation of the MoneyAmount by using precision of Currency.
 *
 */
public final class MonetaryAmount extends AbstractMultiColumnUserType<javax.money.MonetaryAmount> {

    @Override
    protected ColumnMapper<?, ?>[] getColumnMappers() {
        return new ColumnMapper<?, ?>[] {
            new StringColumnCurrencyUnitMapper(),
            new BigDecimalBigDecimalColumnMapper(),
        };
    }

    @Override
    protected javax.money.MonetaryAmount fromConvertedColumns(Object[] convertedColumns) {
        final CurrencyUnit currencyUnitPart = (CurrencyUnit) convertedColumns[0];
        final BigDecimal amountPart = (BigDecimal) convertedColumns[1];

        return Money.of(
            amountPart,
            currencyUnitPart
        ).divide(
            BigDecimal.valueOf(
                Math.pow(10, currencyUnitPart.getDefaultFractionDigits())
            )
        ).with(
            new DefaultRoundingProvider().getRounding(
                RoundingQueryBuilder.of()
                    .setCurrency(currencyUnitPart)
                    .set(RoundingMode.class, RoundingMode.DOWN)
                    .build()
            )
        );
    }

    @Override
    protected Object[] toConvertedColumns(javax.money.MonetaryAmount value) {
        return new Object[] {
            value.getCurrency(),
            value.multiply(
                BigDecimal.valueOf(
                    Math.pow(10, value.getCurrency().getDefaultFractionDigits())
                )
            ).getNumber()
                .numberValue(BigDecimal.class)
                .setScale(0, RoundingMode.DOWN),
        };
    }

    @Override
    public String[] getPropertyNames() {
        return new String[] {"currency", "amount" };
    }
}
