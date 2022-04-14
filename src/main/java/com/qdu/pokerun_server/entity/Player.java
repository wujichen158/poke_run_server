package com.qdu.pokerun_server.entity;


import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity // This tells Hibernate to make a table out of this class
@DynamicUpdate
@Table(name = "player")
public class Player {

    public interface WithoutPassword{};

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long uuid;
    @Column(nullable = false, unique = true)
    private String playerName;

    @Column(nullable = false, unique = true)
    private String email;

    private short permission;

    @Column(nullable = false)
    private String pwd;
//    private byte[] password;

    @Column
    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

//    public byte[] getPassword() {
//        return password;
//    }
//
//    public void setPassword(byte[] password) {
//        this.password = password;
//    }
    public String getPwd() {
        return pwd;
    }

    public void setPwd(String password) {
        this.pwd = password;
    }

    @JsonView(WithoutPassword.class)
    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    @JsonView(WithoutPassword.class)
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    @JsonView(WithoutPassword.class)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @JsonView(WithoutPassword.class)
    public short getPermission() {
        return permission;
    }

    public void setPermission(short permission) {
        this.permission = permission;
    }
}
