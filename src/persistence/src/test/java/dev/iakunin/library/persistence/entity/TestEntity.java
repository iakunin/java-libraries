package dev.iakunin.library.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.Type;
import org.javamoney.moneta.Money;

@Entity
@Table(name = "test_entity")
@Proxy(lazy = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Columns(
        columns = {
            @Column(name = "currency", length = 32),
            @Column(name = "amount", precision = 64),
        }
    )
    @Type(type = "monetaryAmount")
    private Money moneyAmount;
}
