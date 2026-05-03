package io.orkidea.organizations.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.orkidea.organizations.model.OrganizationEntity;

/**
 * JPA repository for organization persistence.
 * Used by OrganizationProjection to materialize read-side projections from domain events.
 */
@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, String> {
}
