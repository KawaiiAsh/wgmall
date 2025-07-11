package org.wgtech.wgmall_backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRedBad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Double day1;  // 第1天金额
    private Double day2;  // 第2天金额
    private Double day3;  // 第3天金额
    private Double day4;  // 第4天金额
    private Double day5;  // 第5天金额
    private Double day6;  // 第6天金额
    private Double day7;  // 第7天金额
}
