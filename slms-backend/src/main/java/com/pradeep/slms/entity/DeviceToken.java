package com.pradeep.slms.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.OffsetDateTime;

@Entity
@Table(name = "device_tokens", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "push_token"})
})
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class DeviceToken extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Platform platform;

    @Column(name = "push_token", nullable = false, length = 500)
    private String pushToken;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    public enum Platform { ANDROID, IOS, WEB }
}
