package org.zpi.conferoapi.user;

import org.junit.jupiter.api.Test;
import org.zpi.conferoapi.DataJpaTestBase;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class UserRepositoryTest extends DataJpaTestBase {

    @Test
    void should_save_and_find() {
        var user = User.builder()
                .email("artsi@gmail.com")
                .isAdmin(true)
                .build();

        userRepository.save(user);

        var foundUser = userRepository.findByEmail("artsi@gmail.com");

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("artsi@gmail.com");
    }
}