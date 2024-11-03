package org.zpi.conferoapi.application;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.zpi.conferoapi.session.Session;

@Repository
public interface ApplicationRepository extends JpaRepository<Session, Long> {

}
