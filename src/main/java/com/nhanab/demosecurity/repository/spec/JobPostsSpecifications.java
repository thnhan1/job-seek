package com.nhanab.demosecurity.repository.spec;

import com.nhanab.demosecurity.entity.JobPost;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

public class JobPostsSpecifications {
    public static Specification<JobPost> hasJobTitle(String jobTitle) {
        return (root, query, criteriaBuilder) -> Optional.ofNullable(jobTitle).filter(s -> !s.trim().isEmpty()).map(s -> criteriaBuilder.like(criteriaBuilder.lower(root.get("jobTitle")), "%" + s.toLowerCase() + "%")).orElse(null);
    }

    public static Specification<JobPost> thisMonth() {
        return (root, query, criteriaBuilder) -> {
            LocalDate now = LocalDate.now();
            LocalDate startOfMonth = now.withDayOfMonth(1);
            LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
            return criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startOfMonth), criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endOfMonth));
        };
    }

    public static Specification<JobPost> thisWeek() {
        return (root, query, cb) -> Optional.of(LocalDate.now()).map(date -> {
            LocalDate startOfWeek = date.with(java.time.DayOfWeek.MONDAY);
            LocalDate endOfWeek = date.with(java.time.DayOfWeek.SUNDAY);
            return cb.and(cb.greaterThanOrEqualTo(root.get("createdAt"), startOfWeek), cb.lessThanOrEqualTo(root.get("createdAt"), endOfWeek));
        }).orElse(cb.conjunction());
    }

    public static Specification<JobPost> hasMajor(String major) {
        return (root, query, criteriaBuilder) -> Optional.ofNullable(major).map(m -> criteriaBuilder.equal(root.get("major"), m)).orElse(criteriaBuilder.conjunction());
    }

    public static Specification<JobPost> createdInMonthAndYear(Month month, int year) {
        return (root, query, criteriaBuilder) -> {
            LocalDate startOfMonth = LocalDate.of(year, month, 1);
            LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

            return criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startOfMonth), criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endOfMonth));
        };
    }

    public static Specification<JobPost> isActive(Boolean isActive) {
        return (root, query, cb) -> Optional.ofNullable(isActive).map(active -> cb.equal(root.get("isActive"), active)).orElse(cb.conjunction());
    }

    public static Specification<JobPost> createAfter(LocalDate date) {
        return (root, query, cb) -> Optional.ofNullable(date).map(d -> cb.greaterThanOrEqualTo(root.get("createdAt"), date)).orElse(cb.conjunction());
    }

}
