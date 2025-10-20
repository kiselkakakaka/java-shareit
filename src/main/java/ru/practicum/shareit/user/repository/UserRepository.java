package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    @Query("""
        select case when count(u)>0 then true else false end
        from User u
        where u.email = :email and u.id <> :excludeId
    """)
    boolean existsByEmailForOther(String email, Long excludeId);
}