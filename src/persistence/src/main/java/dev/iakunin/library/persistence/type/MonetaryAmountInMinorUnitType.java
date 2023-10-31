package dev.iakunin.library.persistence.type;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.RoundingQueryBuilder;
import lombok.NoArgsConstructor;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.spi.ValueAccess;
import org.hibernate.usertype.CompositeUserType;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.spi.DefaultRoundingProvider;

/**
 * Represents MonetaryAmount in minor currency units.<br/>
 * For example:
 * <ul>
 * <li>Money.of("12.34", "USD") will be saved as a pair of "1234" and "USD"
 * <li>Money.of("0.00000001", "BTC") will be saved as a pair of "1" and "BTC"
 * </ul>
 */
@NoArgsConstructor
public class MonetaryAmountInMinorUnitType implements CompositeUserType<MonetaryAmount> {

    @Override
    public Object getPropertyValue(
        MonetaryAmount component,
        int property
    ) throws HibernateException {
        // alphabetical (amount, currency)
        return switch (property) {
            case 0 -> component
                .multiply(
                    BigDecimal.valueOf(
                        Math.pow(10, component.getCurrency().getDefaultFractionDigits())
                    )
                )
                .getNumber()
                .numberValue(BigDecimal.class)
                .setScale(0, RoundingMode.DOWN);
            case 1 -> component
                .getCurrency()
                .getCurrencyCode();
            default -> throw new HibernateException("Illegal property index: " + property);
        };
    }

    @Override
    public MonetaryAmount instantiate(
        ValueAccess values,
        SessionFactoryImplementor sessionFactory
    ) {
        // alphabetical (amount, currency)
        final BigDecimal amount = values.getValue(0, BigDecimal.class);
        final CurrencyUnit currency = Monetary.getCurrency(values.getValue(1, String.class));
        return Money.of(amount, currency)
            .divide(
                BigDecimal.valueOf(
                    Math.pow(10, currency.getDefaultFractionDigits())
                )
            )
            .with(
                new DefaultRoundingProvider().getRounding(
                    RoundingQueryBuilder.of()
                        .setCurrency(currency)
                        .set(RoundingMode.class, RoundingMode.DOWN)
                        .build()
                )
            );
    }

    @Override
    public Class<?> embeddable() {
        return MonetaryAmountEmbeddable.class;
    }

    @Override
    public Class<MonetaryAmount> returnedClass() {
        return MonetaryAmount.class;
    }

    @Override
    public boolean equals(MonetaryAmount first, MonetaryAmount second) {
        return Objects.equals(first, second);
    }

    @Override
    public int hashCode(MonetaryAmount value) {
        return value.hashCode();
    }

    @Override
    public MonetaryAmount deepCopy(MonetaryAmount value) {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(MonetaryAmount value) {
        return (Serializable) value;
    }

    @Override
    public MonetaryAmount assemble(Serializable cached, Object owner) {
        return (MonetaryAmount) cached;
    }

    @Override
    public MonetaryAmount replace(MonetaryAmount detached, MonetaryAmount managed, Object owner) {
        return detached;
    }

    // the embeddable class which acts as a source of metadata
    public static class MonetaryAmountEmbeddable {
        BigDecimal amount;
        String currency;
    }
}
