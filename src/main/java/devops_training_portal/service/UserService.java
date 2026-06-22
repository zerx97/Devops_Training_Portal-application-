package devops_training_portal.service;

import devops_training_portal.entity.User;
import devops_training_portal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {

    if(user.getRegistrationDate() == null){
        user.setRegistrationDate(java.time.LocalDateTime.now());
    }

    return userRepository.save(user);
}
    public User loginUser(String email,
                      String password){

    return userRepository
            .findByEmailAndPassword(email,password)
            .orElse(null);
}
}