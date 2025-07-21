package Phonesonal.PhoneBE.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long foodId;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(length = 50)
    private String servingSize;

    private Float calorie;
    private Float carb;
    private Float protein;
    private Float fat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @Column(nullable = false)
    private Boolean isCustom; // true: 사용자 직접 입력, false: 추천 음식

    @PrePersist
    public void prePersist() {
        if (isCustom == null) {
            isCustom = false; // 사용자 직접 입력은 일회성 데이터.
        }
    }
}
