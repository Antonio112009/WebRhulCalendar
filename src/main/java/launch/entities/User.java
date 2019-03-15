package launch.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "users_all")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "calendar_url")
    private String url;
}
