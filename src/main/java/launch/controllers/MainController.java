package launch.controllers;

import launch.cal_actions.Cutter;
import launch.config.WebConfig;
import launch.entities.User;
import launch.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequestMapping("/")
public class MainController {

    private Cutter cutter = new Cutter();

    @Autowired
    private UserServices userServices;

    @GetMapping
    public ModelAndView mainGet(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("main");
        return modelAndView;
    }

    @PostMapping
    public ModelAndView mainPost(@Valid User user, BindingResult bindingResult){
        ModelAndView modelAndView = new ModelAndView();

        User foundUser;

        if((foundUser = userServices.findUserByUrl(user.getUrl())) != null){
            modelAndView.addObject("idshka", "Your permanent id is: " + foundUser.getId());
            modelAndView.addObject("link", "http://" + WebConfig.URL + "/calendar?id=" + foundUser.getId());
            bindingResult.rejectValue("url", "error", "User already exist in the system.");

        } else if(user.getUrl().startsWith("https://webtimetables.royalholloway.ac.uk/ical/default.aspx?studentical&p1=")) {
            foundUser = userServices.saveUser(user);

            cutter.updateCalendar(user);

            modelAndView.addObject("success", "Successfully added you to the system. Your permanent id is - " + foundUser.getId());
            modelAndView.addObject("link", "http://" + WebConfig.URL + "/calendar?id=" + foundUser.getId());

        } else {
            bindingResult.rejectValue("url", "error", "Error! Please enter link to iCalendar one more time");
        }

        List<User> table = userServices.findAllUsers();
        modelAndView.addObject("table", table);

        modelAndView.setViewName("main");
        return modelAndView;
    }
}
