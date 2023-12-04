package dev.iakunin.library.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Base class for all user-defined entities (field `id` IS updatable).
 */
@MappedSuperclass
@Getter
@Setter
@ToString
@SuppressWarnings({"PMD.AbstractClassWithoutAbstractMethod", "checkstyle:LineLength", })
public abstract class AbstractEntityWithUpdatableId {

    @Id
    @UuidGenerator
    @Column(name = "id", nullable = false)
    protected UUID id;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    protected ZonedDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    protected ZonedDateTime updatedAt;

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
        return Objects.equals(id, ((AbstractEntityWithUpdatableId) other).id);
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
