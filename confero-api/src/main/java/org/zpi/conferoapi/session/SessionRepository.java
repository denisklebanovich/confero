package org.zpi.conferoapi.session;

import org.openapitools.model.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByIdAndCreatorIdAndStatusIsIn(Long id, Long creatorId, List<ApplicationStatus> statuses);

    Optional<Session> findByIdAndStatusIsIn(Long id, List<ApplicationStatus> statuses);

    Optional<Session> findByIdAndCreatorId(Long id, Long creatorId);

    List<Session> findAllByCreatorIdAndStatusNot(Long creatorId, ApplicationStatus status);

    List<Session> findAllByStatusNot(ApplicationStatus status);

    List<Session> findAllByStatus(ApplicationStatus status);

    @Query(value = """
                SELECT s FROM Session s
                JOIN s.presentations p
                JOIN p.presenters pr
                WHERE (:orcid IS NULL OR pr.orcid = :orcid)
                  OR (:emails IS NULL OR pr.email IN :emails)
            """)
    List<Session> findUsersParticipations(@Param("orcid") String orcid, @Param("emails") List<String> emails);


    @Query(value = """
                SELECT CASE WHEN COUNT(s) > 0 THEN TRUE ELSE FALSE END
                FROM Session s
                JOIN s.presentations p
                JOIN p.presenters pr
                WHERE s.id = :sessionId
                  AND ((:orcid IS NULL OR pr.orcid = :orcid)
                  OR (:emails IS NULL OR pr.email IN :emails))
            """)
    boolean isUserParticipantForSession(@Param("sessionId") Long sessionId, @Param("orcid") String orcid, @Param("emails") List<String> emails);


    @Query(value = """
                SELECT s FROM Session s
                JOIN s.presentations p
                JOIN p.presenters pr
                WHERE pr.id = :orginizerId
            """)
    List<Session> findParticipationsByOrganizerId(@Param("orginizerId")Long organizerId);
}