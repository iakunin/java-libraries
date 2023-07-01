package dev.iakunin.library.persistence.repository;

import dev.iakunin.library.persistence.entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestEntityRepository extends JpaRepository<TestEntity, Long> {
    /* _ */
}
