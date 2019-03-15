package launch.services;

import launch.entities.User;
import launch.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServices {

    private UserRepository userRepository;

    @Autowired
    public UserServices(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserByUrl(String url){
        return userRepository.findByUrl(url);
    }

    public List<User> findAllUsers(){
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        user.setUrl(user.getUrl());
        return userRepository.save(user);
    }

}
