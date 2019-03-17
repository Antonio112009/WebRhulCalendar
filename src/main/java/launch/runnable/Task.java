package launch.runnable;

import launch.cal_actions.Cutter;
import launch.entities.User;
import launch.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Configuration
@EnableScheduling
public class Task {

    private Cutter cutter = new Cutter();

    @Autowired
    private UserServices userServices;

    @Scheduled(cron = "0 0 0 * * ?")
    void downloadCalendars(){
        try {
            List<User> users = userServices.findAllUsers();
            for (User user : users) {
                new Thread(() -> {
                    try {
                        cutter.updateCalendar(user);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (user != null) {
                            System.out.println("проблема в user " + user.getId());
                        }
                    }
                }).start();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
