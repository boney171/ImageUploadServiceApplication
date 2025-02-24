package com.example.demo.image;

import com.example.demo.image.dto.ImageResponseModel;
import com.example.demo.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name="IMAGES")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(name="imgur_id", nullable=false)
    private String imgurId;

    @Column(name="delete_hash", nullable=false)
    private String deleteHash;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    private String path;


    @Column(nullable = false)
    private Date date;

    public ImageResponseModel toResponseModel() {
        return new ImageResponseModel(id,title, path, date);
    }
}
