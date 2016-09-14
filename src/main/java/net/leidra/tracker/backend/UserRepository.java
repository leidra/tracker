
package net.leidra.tracker.backend;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Empty JpaRepository is enough for a simple crud.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    
    /* A version to fetch List instead of Page to avoid extra count query. */
    List<User> findAllBy(Pageable pageable);

    @Query("Select u from User u where u.username = :username")
    User findByUserName(@Param("username") String username);
}
