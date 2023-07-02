package fr.uge.users;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class User {
    @Id
    @GeneratedValue
    private long userId;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setUsername(String username){
        this.username = Objects.requireNonNull(username);
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
       this.password =  Objects.requireNonNull(password);
    }

    public String getPassword() {
        return password;
    }
}
