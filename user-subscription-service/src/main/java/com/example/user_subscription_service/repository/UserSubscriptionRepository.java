package com.example.user_subscription_service.repository;

import com.example.user_subscription_service.model.UserSubscription;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {
  List<UserSubscription> findByUserId(Long userId);
}
