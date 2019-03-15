package launch.runnable;

import launch.cal_actions.Cutter;
import launch.entities.User;
import launch.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.util.List;

public class Task {

    private Cutter cutter = new Cutter();

    @Autowired
    private UserServices userServices;

    void downloadCalendars(){
        LocalTime time = LocalTime.now();
        if(time.getHour() == 0 && time.getMinute() == 0) {
            List<User> users = userServices.findAllUsers();
            for(User user : users){
                new Thread(() -> {
                    try {
                        cutter.updateCalendar(user);
                    } catch (Exception e){
                        e.printStackTrace();
                        if(user != null){
                            System.out.println("проблема в user " + user.getId());
                        }
                    }
                }).start();
            }
        }
    }
}
