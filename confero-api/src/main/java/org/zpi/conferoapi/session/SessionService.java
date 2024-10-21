package org.zpi.conferoapi.session;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

    public List<SessionDto> getAllSessions() {
        return sessionRepository.findAll().stream()
                .map(sessionMapper::toDto)
                .toList();
    }
}
