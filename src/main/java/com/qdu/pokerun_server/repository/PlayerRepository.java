package com.qdu.pokerun_server.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.qdu.pokerun_server.entity.*;

import java.util.Date;
import java.util.Optional;

public interface PlayerRepository extends CrudRepository<Player, Long> {
    public Optional<Player> findByEmail(String email);
    public Optional<Player> findByPlayerName(String playerName);
    @Modifying
    @Query(value = "update user set user.last_login =?2 where user.uid =?1", nativeQuery = true)
    public void updateLastLogin(Long uid, Date lastLogin);
    @Modifying
    @Query(value = "insert into user_favorite(uid, postid) values(?1, ?2)", nativeQuery = true)
    public void updateUserFavorite(Long uid, Long postid);
    @Modifying
    @Query(value = "delete from user_favorite where uid = ?1 and postid = ?2", nativeQuery = true)
    public void deleteUserFavorite(Long uid, Long postid);
}
