package com.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255, message = "Message content must be at most 255 characters")
    @Column(nullable = false, length = 255)
    private String content;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "chatroom_id", nullable = false)
    private Chatroom chatroom;

    @OneToOne
    @JoinColumn(name = "file_id")
    private File file;

    public String getSenderName() {
        return sender != null ? sender.getUsername() : "Unknown";
    }

    public void setFile(File file) { this.file = file; }

}
