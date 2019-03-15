package launch.entities;

import lombok.Data;

@Data
public class Subject {
    private String[] time;
    private String dayOfWeek;
    private String subject;
    private String type;
    private String location;
}
