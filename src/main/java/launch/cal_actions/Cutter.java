package launch.cal_actions;

import launch.entities.Subject;
import launch.entities.User;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Cutter {

    public void downloadCalendar(String url, String id) {
        try {
            System.out.println("downloading for " + id);
            URL website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream("webCal/ICS_" + id + ".txt");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            rbc.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    //Удалить ненужную информацию из calendars-initial
    public void cutNotImportantInfo(String id){
        try {
            File inputFile = new File("webCal/ICS_" + id + ".txt");
            File tempFile = new File("webCal/ICS_" + id + "_templ.txt");

            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

            String currentLine;

            while ((currentLine = reader.readLine()) != null) {
                if(currentLine.contains("X-LIC-LOCATION")){
                    continue;
                }

                if (currentLine.contains("LOCATION")) {
                    writer.write("location::" + currentLine.substring(9) + System.getProperty("line.separator") + System.getProperty("line.separator"));
                }

                if(currentLine.contains("DTEND;TZID=Europe/London"))
                    writer.write("end::" + currentLine.split(":")[1] + System.getProperty("line.separator"));

                if(currentLine.contains("DTSTART;TZID=Europe/London"))
                    writer.write("start::" + currentLine.split(":")[1] + System.getProperty("line.separator"));

                if (currentLine.contains("SUMMARY"))
                    writer.write("title::" + currentLine.split(":")[1] + System.getProperty("line.separator"));

                if(currentLine.contains("DESCRIPTION"))
                    writer.write("description::" + currentLine.split(":")[1] + System.getProperty("line.separator"));
            }
            writer.close();
            reader.close();
            boolean successful = tempFile.renameTo(inputFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void cutWeek(String id) {
        LocalDate weekStart = getMondayDate();

        boolean read = false;
        try {
            File inputFile = new File("webCal/ICS_" + id + ".txt");
            File tempFile = new File("webCal/week/ICS_" + id + ".txt");

            BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
            String currentLine;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            for (int i = 0; i <= 4; i++) {
                String date = weekStart.plusDays(i).format(formatter);
                BufferedReader reader = new BufferedReader(new FileReader(inputFile));
                while ((currentLine = reader.readLine()) != null) {
                    if (currentLine.startsWith("start::" + date))
                        read = true;

                    if (currentLine.equals("") && read)
                        read = false;

                    if (read) {
                        writer.write(currentLine + System.getProperty("line.separator"));
                        if (currentLine.startsWith("location::"))
                            writer.write(System.getProperty("line.separator"));
                    }
                }
                reader.close();
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private LocalDate getMondayDate(){
        LocalDate today = LocalDate.now();
        switch (today.getDayOfWeek().getValue()){
            case 2:
                today = today.minusDays(1L);
                break;
            case 3:
                today = today.minusDays(2L);
                break;
            case 4:
                today = today.minusDays(3L);
                break;
            case 5:
                today = today.minusDays(4L);
                break;
            case 6:
                today = today.plusDays(2L);
                break;
            case 7:
                today = today.plusDays(1L);
                break;
        }
        return today;
    }


    public String timeConverterTimerString(String timeInit){
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HHmmss");
        LocalTime time = LocalTime.parse(timeInit, formatter1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

    public Subject arrayToSubjectObject(List<String> arrayData){
        Subject subject = new Subject();
        String[] time = new String[2];
        time[0] = timeConverterTimerString(arrayData.get(0).substring(16));
        time[1] = timeConverterTimerString(arrayData.get(1).substring(14));
        subject.setTime(time);
        subject.setType(arrayData.get(2).split(" ")[2]);
        subject.setSubject(arrayData.get(3).split("\\|")[1].split("\\\\")[0].substring(1));
        subject.setLocation(arrayData.get(4).split("::")[1]);
        return subject;
    }



    private String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};

    public List<Subject[]> showTimetable(String id){
        try {
            BufferedReader reader = null;
            Subject day = new Subject();
            String currentLine;
            boolean read = false;

            List<String> arrayData = new ArrayList<>();
            Subject[] subjects;
            List<Subject[]> finArray = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");


            for (int i = 0; i <= 4; i++){

                subjects = new Subject[20];

                int time = 0;
                int timeEnd = 0;

                String date = getMondayDate().plusDays(i).format(formatter);
                reader = new BufferedReader(new FileReader(new File("webCal/week/ICS_" + id + ".txt")));
                Subject dayWeek = new Subject();
                dayWeek.setDayOfWeek(days[i]);
                subjects[2] = dayWeek;

                while ((currentLine = reader.readLine()) != null) {
                    if(currentLine.startsWith("start::")) {
                        if (currentLine.split("::")[1].split("T")[0].equals(date)){
                            arrayData = new ArrayList<>();
                            time = Integer.valueOf(currentLine.split("T")[1].substring(0,2));
                            read = true;
                        }
                    }

                    if(currentLine.startsWith("end::")) {
                        timeEnd = Integer.valueOf(currentLine.split("T")[1].substring(0,2));
                    }

                    if(read){
                        arrayData.add(currentLine);
                    }

                    if(currentLine.equals("") && read){
                        int diff = timeEnd - time;
                        Subject subject = arrayToSubjectObject(arrayData);
                        for (int j = 0; j < diff; j++){
                            subjects[time + j] = subject;
                        }
                        read = false;
                    }
                }
                finArray.add(subjects);
            }

            return finArray;
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public void updateCalendar(User user){
        String id = String.valueOf(user.getId());
        downloadCalendar(user.getUrl(), id);
        cutNotImportantInfo(id);
        cutWeek(id);
    }
}
