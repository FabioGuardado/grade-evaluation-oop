package gradesmanager;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.Data;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.*;
import java.util.*;
import java.util.stream.Stream;

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
        saveReport(gradesList, results, subjectName);
        sendReport(subjectName);
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
        //Creating the .txt report
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

        // Creating the .pdf report using iTextPdf
        try{
            Document document = new Document(PageSize.LETTER);
            PdfWriter.getInstance(document, new FileOutputStream("reports/" + subjectName + ".pdf"));

            document.open();
            Font titles = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
            Font body = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

            Chunk title = new Chunk("Grade evaluation app", titles);
            document.add(new Paragraph(title));

            Chunk intro = new Chunk("This is a resume of the grades received and processed in " + subjectName, body);
            document.add(new Paragraph(intro));

            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(2);
            addTableHeader(table);
            addRows(table, listOfGrades);
            document.add(table);

            document.add(Chunk.NEWLINE);

            Chunk statisticsTitle = new Chunk("Statistics", titles);
            document.add(statisticsTitle);

            document.add(Chunk.NEWLINE);

            Chunk statistics = new Chunk(results, body);
            document.add(statistics);

            document.close();
            System.out.println("PDF report successfully created in the same location... ");
        } catch (DocumentException | FileNotFoundException e) {
            System.out.println("An error has occurred while creating the pdf report...");
        }
    }

    // Custom function to add table headers
    private void addTableHeader(PdfPTable table){
        Stream.of("Name of the student", "Grade obtained")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.ORANGE);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private void addRows(PdfPTable table, ArrayList<Student> listOfGrades){
        for(Student aStudent : listOfGrades){
            table.addCell(aStudent.getName());
            table.addCell(String.valueOf(aStudent.getGrade()));
        }
    }

    /**
     * Send report.
     *
     */
    public void sendReport(String subjectName){
        String fromEmail = "from@example.com";
        String username = "c6403443ca78bc";
        String password = "7bc22a7bbaff52";
        String toEmail = getEmailDestination();

        String host = "smtp.mailtrap.io";

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

        try{
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subjectName + " reports");

            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("These are the reports generated by Grades manager App for " + subjectName);

            MimeBodyPart txtAttachment = new MimeBodyPart();
            txtAttachment.attachFile(new File("reports/" + subjectName + ".txt"));
            MimeBodyPart pdfAttachment = new MimeBodyPart();
            pdfAttachment.attachFile(new File("reports/" + subjectName + ".pdf"));

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(txtAttachment);
            multipart.addBodyPart(pdfAttachment);
            message.setContent(multipart);
            Transport.send(message);
            System.out.println("Email sent successfully!");
        }catch (Exception e){
            System.out.println("An error has occurred, couldn't send the message.");
        }
    }
}
