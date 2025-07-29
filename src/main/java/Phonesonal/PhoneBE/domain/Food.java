package Phonesonal.PhoneBE.domain;

import Phonesonal.PhoneBE.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 모든 음식 (식단 플랜에 넣을 세트, 사용자가 직접 추가한 음식 both)

public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foodId;

    //음식 이름
    @Column(length = 100, nullable = false)
    private String name;

    // 양
    @Column(length = 50)
    private String servingSize;

    // 영양소
    private Float calorie;
    private Float carb;
    private Float protein;
    private Float fat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    // true: 사용자 직접 입력, false: 추천 음식
    @Column(nullable = false)
    private Boolean isCustom;

    @PrePersist
    public void prePersist() {
        if (isCustom == null) {
            isCustom = false; // 사용자 직접 입력은 일회성 데이터.
        }
    }
}
