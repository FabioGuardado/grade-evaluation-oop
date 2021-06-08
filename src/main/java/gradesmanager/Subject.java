package gradesmanager;

import lombok.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * The type Subject.
 */
@Data
public class Subject {
    private String name, emailDestination;
    private ArrayList<Student> listOfGrades = new ArrayList<>();

    /**
     * Instantiates a new Subject.
     *
     * @param name      the name
     */
    public Subject( String name){
        this.name = name;
    }

    /**
     * Load file.
     */
    public void loadFile(){
        String fileName, aLine;
        String[] lineParts;
        double gradeConversion;
        boolean validFileName;
        Scanner loadInput = new Scanner(System.in);

        System.out.println("------------------------------------------------------");
        System.out.println("------------------- LOADING A FILE -------------------");
        System.out.println("Put the grades text file for this subject inside the");
        System.out.println("project directory (SecondProject/).");
        System.out.println("\nRemember to add an email destination on the top");
        System.out.println("of the file, and separate each student and grade using");
        System.out.println("the following structure: (student=grade).");
        System.out.println("------------------------------------------------------");
        do {
            System.out.print("\n-> Type the name of the file (without extension): ");
            fileName = loadInput.nextLine();
            try{
                File gradesFile = new File(fileName + ".txt");
                Scanner fileInput = new Scanner(gradesFile);
                validFileName = true;
                while(fileInput.hasNextLine()){
                    aLine = fileInput.nextLine();
                    if(aLine.matches("^(.+)@(.+)$")){
                        setEmailDestination(aLine);
                    } else {
                        lineParts = aLine.split("=");
                        gradeConversion = Double.parseDouble(lineParts[1]);
                        listOfGrades.add(new Student(lineParts[0], gradeConversion));
                    }
                }
                System.out.println("------------- FILE LOADED SUCCESSFULLY! --------------\n");
            } catch (FileNotFoundException e) {
                System.out.println("File not found, check the file location or the name.");
                validFileName = false;
            }
        } while(!validFileName);

        showGradesList();
    }

    /**
     * Add student.
     */
    public void addStudent(){
        boolean addAnother, validGrade = false;
        String studentName, addAnotherReader;
        double studentGrade;
        Scanner addInput = new Scanner(System.in);

        System.out.println("\n------------------------------------------------------");
        System.out.println("------------------ ADDING A STUDENT ------------------");
        do {
            System.out.print("-> Type the name of the student: ");
            studentName = addInput.nextLine();
            do{
                try{
                    System.out.print("->Type the grade of " + studentName + ": ");
                    studentGrade = Double.parseDouble(addInput.nextLine());
                    if(studentGrade >= 0 && studentGrade <= 10){
                        validGrade = true;
                        listOfGrades.add(new Student(studentName, studentGrade));
                        System.out.println("------------ STUDENT ADDED SUCCESSFULLY! -------------\n");
                    } else{
                        System.out.println("Invalid grade, please enter a decimal number between 0 and 10.\n");
                        validGrade = false;
                    }
                }catch (NumberFormatException e) {
                    System.out.println("Invalid number format, please enter a decimal number between 0 and 10.\n");
                }
            } while(!validGrade);

            System.out.print("-> Do you want to add another student?(y/n): ");
            addAnotherReader = addInput.nextLine();
            addAnother = addAnotherReader.equals("y");
        }while(addAnother);

        showGradesList();
    }

    /**
     * Generate report.
     */
    public void generateReport(){
        new Report(emailDestination, name, listOfGrades);
    }

    /**
     * Show grades list.
     */
    public void showGradesList(){
        Scanner inputDecision = new Scanner(System.in);
        System.out.print("-> Do you want to see the students list?(y/n): ");
        String showList = inputDecision.nextLine();
        if(showList.equals("y")){
            System.out.println(listOfGrades);
        }
    }
}