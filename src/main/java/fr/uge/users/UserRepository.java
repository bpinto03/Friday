package fr.uge.users;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {

    public User getUserByUsername(String username){
        return find("username", username).firstResult();  //username field is unique
    }

    public User checkUsernameAndPassword(String usernameToLog, String password){
        var userInBase = getUserByUsername(usernameToLog); //if username not in database return null
        if(userInBase != null && userInBase.getPassword().equals(password)){
            return userInBase;
        }
        return null;
    }
}
