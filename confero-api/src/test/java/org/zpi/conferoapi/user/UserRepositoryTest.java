package org.zpi.conferoapi.user;

import org.junit.jupiter.api.Test;
import org.zpi.conferoapi.DataJpaTestBase;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryTest extends DataJpaTestBase {


    @Test
    void findByEmail() {
        var user = new User("artsi@gmail.com");
        user.setIsAdmin(true);
        userRepository.save(user);
        var foundUser = userRepository.findByEmail("artsi@gmail.com");
        assertEquals(user.getEmail(), foundUser.get().getEmail());
    }
}