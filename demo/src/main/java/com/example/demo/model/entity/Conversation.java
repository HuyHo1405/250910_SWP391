package com.example.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "conversations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Conversation {

    // ================================
    // COLUMNS - Database Fields
    // ================================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Message> messages;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ConversationParticipant> participants;

    // ================================
    // PRE/POST FUNCTIONS - Lifecycle Callbacks
    // ================================

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        initCollections();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private void initCollections() {
        if (messages == null) {
            messages = new HashSet<>();
        }
        if (participants == null) {
            participants = new HashSet<>();
        }
    }

    // ================================
    // HELPER FUNCTIONS - Business Logic
    // ================================

    public void addMessage(Message message) {
        messages.add(message);
        message.setConversation(this);
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        message.setConversation(null);
    }

    public void addParticipant(ConversationParticipant participant) {
        participants.add(participant);
        participant.setConversation(this);
    }

    public void removeParticipant(ConversationParticipant participant) {
        participants.remove(participant);
        participant.setConversation(null);
    }
}
