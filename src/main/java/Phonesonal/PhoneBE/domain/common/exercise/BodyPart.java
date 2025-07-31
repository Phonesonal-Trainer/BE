package Phonesonal.PhoneBE.domain.common.exercise;

import Phonesonal.PhoneBE.domain.enums.exercise.BodyCategory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class BodyPart {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String nameEn; // 부위 영어 이름

    @Column(nullable = false, length = 20)
    private String nameKo; // 부위 한국어 이름

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BodyCategory bodyCategory;
}

