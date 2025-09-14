package com.tiezshop.configurations.specification;

import com.tiezshop.entity.Role;
import com.tiezshop.entity.User;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

import java.util.List;

public class UserSpecification {

    public static Specification<User> filter(
            String keyword,
            LocalDateTime createdTimeFrom,
            LocalDateTime createdTimeTo,
            List<String> roleIds
    ) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            // search theo keyword
            if (keyword != null && !keyword.isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("username")), likePattern),
                        cb.like(cb.lower(root.get("email")), likePattern),
                        cb.like(cb.lower(root.get("phoneNumber")), likePattern),
                        cb.like(cb.lower(root.get("fullName")), likePattern)
                ));
            }

            // filter createdTimeFrom
            if (createdTimeFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createdTime"), createdTimeFrom));
            }

            // filter createdTimeTo
            if (createdTimeTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createdTime"), createdTimeTo));
            }

            // filter roleIds
            if (roleIds != null && !roleIds.isEmpty()) {
                Join<User, Role> roles = root.join("roles");
                predicate = cb.and(predicate, roles.get("id").in(roleIds));
                query.distinct(true); // tránh trùng user do join nhiều role
            }

            return predicate;
        };
    }
}
