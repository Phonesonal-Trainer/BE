package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.Inbody;
import Phonesonal.PhoneBE.domain.RecommendMeal;
import Phonesonal.PhoneBE.domain.enums.CompleteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InbodyImageRepository extends JpaRepository<Inbody,Long> {
    Optional<Inbody> findByUserIdAndGoalPeriodId(Long userId, Long goalPeriodId);
}
