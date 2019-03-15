package launch.controllers;

import launch.cal_actions.Cutter;
import launch.entities.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    @GetMapping
    public ModelAndView calendarGet(@RequestParam(name = "id") String id){
        ModelAndView modelAndView = new ModelAndView();

        List<Subject[]> subjects = new Cutter().showTimetable(id);

        modelAndView.addObject("subjects", subjects);
        modelAndView.setViewName("calendar");
        return modelAndView;
    }
}
