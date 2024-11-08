package org.zpi.conferoapi.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.zpi.conferoapi.DataJpaTestBase;
import org.zpi.conferoapi.email.UserEmail;

import static org.junit.Assert.assertEquals;

class UserRepositoryTest extends DataJpaTestBase {


    @Test
    void findByEmail() {
        var user = new User();
        user.setIsAdmin(true);
        userRepository.save(user);
        userEmailRepository.save(new UserEmail("artsi@gmail.com", true, user, null));
        var foundUser = userRepository.findByEmail("artsi@gmail.com");
        Assertions.assertEquals(user, foundUser.get());
    }
}