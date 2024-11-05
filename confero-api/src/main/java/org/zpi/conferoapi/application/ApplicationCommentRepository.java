package org.zpi.conferoapi.application;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationCommentRepository extends JpaRepository<ApplicationComment, Long> {
}