package gradesmanager;

import files.Files;
import lombok.Data;

import mails.EmailUtil;
import javax.mail.*;

import java.util.*;

/**
 * The type Report.
 */
@Data
public class Report {
    private String emailDestination;

    /**
     * Instantiates a new Report.
     *
     * @param emailDestination the email destination
     * @param subjectName      the subject name
     * @param gradesList       the grades list
     */
    public Report(String emailDestination, String subjectName, ArrayList<Student> gradesList) {
        this.emailDestination = emailDestination;

        String results = getStatistics(gradesList);
        saveReport(subjectName, gradesList, results);
        sendReport(subjectName);
    }

    /**
     * Get statistics string.
     *
     * @param gradesList the grades list
     * @return the string
     */
    public String getStatistics(ArrayList<Student> gradesList){
        double max = 0, min = 10, sum = 0, average;
        HashMap<Double, Integer> repeatedGrades = new HashMap<>();
        StringBuilder results = new StringBuilder();

        // setting the max and min grade; adding all grades; counting repeated grades
        for(Student aStudent : gradesList){
            sum += aStudent.getGrade();
            if(aStudent.getGrade() > max) {
                max = aStudent.getGrade();
            }
            if(aStudent.getGrade() < min){
                min = aStudent.getGrade();
            }

            if(repeatedGrades.containsKey(aStudent.getGrade())){
                repeatedGrades.put(aStudent.getGrade(), repeatedGrades.get(aStudent.getGrade()) + 1);
            } else {
                repeatedGrades.put(aStudent.getGrade(), 1);
            }
        }

        // Get the average of the grades
        average = Math.floor((sum/(gradesList.size())) * 100 ) / 100;

        //Build a String with all the statistics
        results.append("The max grade was: ").append(max).append(" by: \n");
        for(Student aStudent : gradesList){
            if(aStudent.getGrade() == max){
                results.append("--> ").append(aStudent.getName()).append("\n");
            }
        }
        results.append("The min grade was: ").append(min).append(" by: \n");
        for (Student aStudent : gradesList){
            if(aStudent.getGrade() == min){
                results.append("--> ").append(aStudent.getName()).append("\n");
            }
        }
        results.append("The average is: ").append(average).append("\n");
        results.append("The most repeated grades are: \n");
        for(Map.Entry<Double, Integer> entry : repeatedGrades.entrySet()){
            if(entry.getValue() > 1) {
                results.append("--> ").append(entry.getKey()).append(" repeated ").append(entry.getValue()).append(" times\n");
            }
        }
        return results.toString();
    }

    /**
     * Save report.
     *
     * @param listOfGrades the list of grades
     * @param results      the results
     * @param subjectName  the subject name
     */
    public void saveReport(String subjectName, ArrayList<Student> listOfGrades, String results ){
        //Creating the .txt report
        Files.generateTxt(subjectName, listOfGrades, results);

        // Creating the .pdf report using iTextPdf
        Files.generatePdf(subjectName, listOfGrades, results);
    }

    /**
     * Send report.
     *
     */
    public void sendReport(String subjectName){
        String fromEmail = "from@example.com";
        String username = "c6403443ca78bc";
        String password = "7bc22a7bbaff52";
        String host = "smtp.mailtrap.io";
        String toEmail = getEmailDestination();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "2525");

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
        Session session = Session.getInstance(props, auth);
        EmailUtil.sendEmailWithReports(session, fromEmail, toEmail, subjectName);
    }
}