package org.zpi.conferoapi.presenter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zpi.conferoapi.proposal.Presenter;

public interface PresenterRepository extends JpaRepository<Presenter, String> {
}