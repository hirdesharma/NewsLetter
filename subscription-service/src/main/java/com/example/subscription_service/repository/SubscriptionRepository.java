package com.example.subscription_service.repository;

import com.example.subscription_service.model.Subscription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
  Optional<Subscription> findByid(Long Id);

  List<Subscription> findByNameContainingIgnoreCaseOrId(String name, Long id);

  List<Subscription> findByNameContainingIgnoreCase(String name);
}
