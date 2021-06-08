package gradesmanager;

import lombok.Data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        String results = getStatistics(gradesList);
        saveReport(gradesList, results, subjectName);
        sendReport(emailDestination);
    }

    /**
     * Get statistics string.
     *
     * @param gradesList the grades list
     * @return the string
     */
    public String getStatistics(ArrayList<Student> gradesList){
        Student maxGrade = new Student();
        Student minGrade = new Student();
        double max = 0, min = 10, sum = 0, average;
        HashMap<Double, Integer> repeatedGrades = new HashMap<>();
        StringBuilder results = new StringBuilder();

        for(Student aStudent : gradesList){
            sum += aStudent.getGrade();
            if(aStudent.getGrade() > max) {
                max = aStudent.getGrade();
                maxGrade.setName(aStudent.getName());
                maxGrade.setGrade(aStudent.getGrade());
            }
            if(aStudent.getGrade() < min){
                min = aStudent.getGrade();
                minGrade.setName(aStudent.getName());
                minGrade.setGrade(aStudent.getGrade());
            }

            if(repeatedGrades.containsKey(aStudent.getGrade())){
                repeatedGrades.put(aStudent.getGrade(), repeatedGrades.get(aStudent.getGrade()) + 1);
            } else {
                repeatedGrades.put(aStudent.getGrade(), 1);
            }
        }
        average = Math.floor((sum/(gradesList.size())) * 100 ) / 100;

        results.append("The max grade was: ").append(maxGrade.getGrade()).append(" by: ").append(maxGrade.getName()).append("\n");
        results.append("The min grade was: ").append(minGrade.getGrade()).append(" by: ").append(minGrade.getName()).append("\n");
        results.append("The average is: ").append(average).append("\n");
        results.append("The most repeated grades are: \n");
        for(Map.Entry<Double, Integer> entry : repeatedGrades.entrySet()){
            if(entry.getValue() > 1) {
                results.append("-> ").append(entry.getKey()).append(" repeated ").append(entry.getValue()).append(" times\n");
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
    public void saveReport(ArrayList<Student> listOfGrades, String results, String subjectName){
        try {
            File report = new File("reports/" + subjectName + ".txt");
            if(report.createNewFile()){
                System.out.println("Report created as a text document (.txt) on: " + report.getAbsolutePath());
            } else {
                System.out.println("An existing file for this subject has been founded on: " + report.getAbsolutePath());
            }
        } catch(IOException e){
            System.out.println("An error has occurred while creating the text file...\n");
        }

        try{
            FileWriter reportWriter = new FileWriter("reports/" + subjectName + ".txt");
            reportWriter.write("--------------- STUDENTS AND GRADES ----------------\n");
            for(Student aStudent : listOfGrades){
                reportWriter.write(aStudent.getName() + " = " + aStudent.getGrade() + "\n");
                reportWriter.write("---------------------------\n");
            }
            reportWriter.write("-------------------- STATISTICS --------------------\n");
            reportWriter.write(results);
            reportWriter.close();
            System.out.println("Report successfully wrote!");
        } catch (IOException e) {
            System.out.println("An error has occurred while writing in the text file...\n");
        }
    }

    /**
     * Send report.
     *
     * @param emailDestination the email destination
     */
    public void sendReport(String emailDestination){

    }
}
