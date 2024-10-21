import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.zpi.conferoapi.session.Session;
import org.zpi.conferoapi.session.SessionRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MockDataGenerator implements CommandLineRunner {
    private final SessionRepository sessionRepository;

    @Override
    public void run(String... args) throws Exception {
        // Generate mock data here
        Session session = new Session();
        session.setTitle("Mock Session");
        session.setDescription("This is a mock session");
        session.setDuration(60);
        session.setStartTime(LocalDateTime.now());
        session.setEndTime(LocalDateTime.now().plusMinutes(60));
        sessionRepository.save(session);
    }
}
