package fr.uge.users;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.wildfly.common.Assert.assertTrue;

@QuarkusTest
public class UserRepositoryTest {

    @Test
    @Transactional
    public void addAnUserToRepositoryShouldSucceed(){
        var repository = new UserRepository();
        var user = new User();
        user.setUsername("username");
        user.setPassword("password");
        repository.persist(user);
        assertTrue(repository.isPersistent(user));
        assertEquals(user, repository.getUserByUsername("username"));
    }

    @Test
    @Transactional
    public void addAnUserToRepositoryAndModifyHisIdShouldFail(){
        var repository = new UserRepository();
        var user = new User();
        user.setUserId(5);
        user.setUsername("username2");
        user.setPassword("password");
        assertThrows(PersistenceException.class,() -> repository.persist(user));
    }

    @Test
    public void getUserWithUsernameNotInBase(){
        var repository = new UserRepository();
        assertNull(repository.getUserByUsername("usernameNotInBase"));
    }

    @Test
    @Transactional
    public void checkLoginShouldBeTrue(){
        var repository = new UserRepository();
        var user = new User();
        user.setUsername("username3");
        user.setPassword("password");
        repository.persist(user);
        assertTrue(repository.isPersistent(user));
        assertNotNull(repository.checkUsernameAndPassword("username3", "password"));
    }

    @Test
    @Transactional
    public void checkLoginShouldBeFalse(){
        var repository = new UserRepository();
        var user = new User();
        user.setUsername("username4");
        user.setPassword("password");
        repository.persist(user);
        assertTrue(repository.isPersistent(user));
        assertNull(repository.checkUsernameAndPassword("username4", "wrongPassword"));
        assertNull(repository.checkUsernameAndPassword("usernae4", "password"));
    }
}
