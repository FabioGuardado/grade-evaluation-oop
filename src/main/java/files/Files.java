package files;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import gradesmanager.Student;

import java.io.*;
import java.util.ArrayList;
import java.util.stream.Stream;

/**
 * The type Files.
 */
public class Files {

    /**
     * Generate txt.
     *
     * @param subjectName  the subject name
     * @param listOfGrades the list of grades
     * @param results      the results
     */
    public static void generateTxt(String subjectName, ArrayList<Student> listOfGrades, String results) {
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
     * Generate pdf.
     *
     * @param subjectName  the subject name
     * @param listOfGrades the list of grades
     * @param results      the results
     */
    public static void generatePdf(String subjectName, ArrayList<Student> listOfGrades, String results){
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
    private static void addTableHeader(PdfPTable table){
        Stream.of("Name of the student", "Grade obtained")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.ORANGE);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    // Custom function to add cells to a table
    private static void addRows(PdfPTable table, ArrayList<Student> listOfGrades){
        for(Student aStudent : listOfGrades){
            table.addCell(aStudent.getName());
            table.addCell(String.valueOf(aStudent.getGrade()));
        }
    }

}
