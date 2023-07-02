package fr.uge.users;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserTest {
    @Test
    public void canSetNullToUserWithNullFieldsShouldFail(){
        var user = new User();
        assertThrows(NullPointerException.class, () -> user.setUsername(null));
        assertThrows(NullPointerException.class, () -> user.setPassword(null));
    }
}
