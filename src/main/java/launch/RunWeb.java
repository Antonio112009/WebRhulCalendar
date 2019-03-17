package launch;

import launch.runnable.Threads;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RunWeb {

    public static void main(String[] args) {
        SpringApplication.run(RunWeb.class, args);
//        new Threads().start();
    }
}
