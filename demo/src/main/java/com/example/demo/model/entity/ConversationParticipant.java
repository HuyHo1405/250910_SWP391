package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_participants",
       uniqueConstraints = @UniqueConstraint(columnNames = {"conversation_id", "user_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ConversationParticipant {

    // ================================
    // COLUMNS - Database Fields
    // ================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    // ================================
    // PRE/POST FUNCTIONS - Lifecycle Callbacks
    // ================================

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }

    // ================================
    // HELPER FUNCTIONS - Business Logic
    // ================================

    public ConversationParticipant(Conversation conversation, User user) {
        this.conversation = conversation;
        this.user = user;
    }
}
