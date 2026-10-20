package com.scaler.productcatalog.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(indexes = @Index(name = "idx_session_token", columnList = "token", unique = true))
public class Session extends BaseModel{
    @Column(nullable = false, length = 512)
    private String token;

    @ManyToOne
    private User user;
}
