package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.User;
import Phonesonal.PhoneBE.domain.enums.SocialType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndSocialType(String email, SocialType socialType);
    Optional<User> findByEmail(String email);
}