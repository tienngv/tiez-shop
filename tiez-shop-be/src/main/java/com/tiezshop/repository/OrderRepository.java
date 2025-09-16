package com.tiezshop.repository;

import com.tiezshop.entity.Order;
import com.tiezshop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {
    
    Optional<Order> findByOrderNumber(String orderNumber);
    
    List<Order> findByUserId(String userId);
    
    Page<Order> findByUserId(String userId, Pageable pageable);
    
    List<Order> findByStatus(Order.OrderStatus status);
    
    List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status")
    List<Order> findByUserIdAndStatus(@Param("userId") String userId, @Param("status") Order.OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.createdTime >= :startDate AND o.createdTime <= :endDate")
    List<Order> findByCreatedTimeBetween(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    Long countByStatus(@Param("status") Order.OrderStatus status);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.status = :status AND o.createdTime >= :startDate AND o.createdTime <= :endDate")
    Double sumTotalAmountByStatusAndDateRange(@Param("status") Order.OrderStatus status,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdTime DESC")
    List<Order> findRecentOrdersByUserId(@Param("userId") String userId, Pageable pageable);
}
