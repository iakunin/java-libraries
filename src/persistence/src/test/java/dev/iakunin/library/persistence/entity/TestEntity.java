package dev.iakunin.library.persistence.entity;

import dev.iakunin.library.persistence.type.MonetaryAmountInMinorUnitType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CompositeType;
import org.javamoney.moneta.Money;

@Entity
@Table(name = "test_entity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AttributeOverride(
        name = "amount",
        column = @Column(name = "price_amount", precision = 64)
    )
    @AttributeOverride(
        name = "currency",
        column = @Column(name = "price_currency", length = 32)
    )
    @CompositeType(MonetaryAmountInMinorUnitType.class)
    private Money price;

    /**
     * For more info about current `equals(...)` and `hashCode()` implementations see: <a href=
     * "https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier">
     * this article </a>.
     */
    @Override
    public final boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) {
            return false;
        }
        return Objects.equals(id, ((TestEntity) other).id);
    }

    /**
     * For more info about current `equals(...)` and `hashCode()` implementations see: <a href=
     * "https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier">
     * this article </a>.
     */
    @Override
    public final int hashCode() {
        return getClass().hashCode();
    }

}
