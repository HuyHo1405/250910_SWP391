package com.example.demo.repo;

import com.example.demo.model.entity.FeedbackTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepo extends JpaRepository<FeedbackTag, Long> {
}
