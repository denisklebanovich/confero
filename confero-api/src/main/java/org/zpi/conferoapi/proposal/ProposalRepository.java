package org.zpi.conferoapi.proposal;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalRepository extends JpaRepository<Proposal, String> {
}