package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.WeightRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface WeightRecordRepository extends JpaRepository<WeightRecord, Long> {
    @Query(value = "SELECT * FROM weight_record WHERE user_id = :userId ORDER BY record_date DESC LIMIT 1", nativeQuery = true)
    Optional<WeightRecord> findLatestByUserId(@Param("userId") Long userId);

    List<WeightRecord> findByUserIdAndGoalPeriodIdAndRecordDateBetween(
            Long userId,
            Long goalPeriodId,
            LocalDateTime start,
            LocalDateTime end
    );
}
