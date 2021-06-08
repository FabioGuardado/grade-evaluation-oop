package gradesmanager;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean exit = false, validOption;
        int option;
        Scanner inputReader = new Scanner(System.in);

        //Creating an array of three subjects.
        Subject[] subjects = new Subject[3];
        subjects[0] = new Subject("Math");
        subjects[1] = new Subject("Science");
        subjects[2] = new Subject("Programming");

        // Main menu for pick a subject
        do {
            System.out.println("----------------- GRADES MANAGER APP -----------------");
            System.out.println("This application allows you to manage grades of 3 ");
            System.out.println("subjects, generate reports and send them to your email.");
            System.out.println("\nIf you want to start filling subjects, select one ");
            System.out.println("of them in this menu.");
            System.out.println("\nIf you want to close this program, select exit.");
            System.out.println("------------------------------------------------------");
            System.out.println("---------------     ---------------     ---------------");
            System.out.println("|    Math     |     |   Science   |     | Programming |");
            System.out.println("---------------     ---------------     ---------------");
            System.out.println("    Press 1             Press 2             Press 3   ");
            do {
                try {
                    System.out.print("\n-> Select an option or press 4 to exit: ");
                    option = Integer.parseInt(inputReader.nextLine());
                    if (option > 0 && option <= 4){
                        switch (option){
                            case 1:
                            case 2:
                            case 3:
                                //Calling the submenu and giving the subject selected
                                subMenuSubject(option, subjects[(option-1)]);
                                break;
                            case 4:
                                exit = true;
                                System.out.println("Thanks for using this grades manager, see you later! 😉");
                                break;
                            default:
                                System.out.println("Select an option between 1 and 4.");
                                break;
                        }
                        validOption = true;
                    } else {
                        System.out.println("\nInvalid option, please type a number between 1 and 4.\n");
                        validOption = false;
                    }
                } catch(NumberFormatException e){
                    System.out.println("\nInvalid number format, please select an option between 1 and 4.\n");
                    validOption = false;
                }
            } while(!validOption);
        } while(!exit);
    }

    // Submenu with the options to manage anyone of the three subjects
    public static void subMenuSubject(int subjectId, Subject subject){
        boolean subMenuExit = false;
        int subMenuOption;
        Scanner subMenuReader = new Scanner(System.in);
        do {
            System.out.println("----------------------- OPTIONS -----------------------\n");
            System.out.println(" -----------   -------------  ------------------------");
            System.out.println(" |Load file|   |Add student|  |Generate & send report|");
            System.out.println(" -----------   -------------  ------------------------");
            System.out.println("   Press 1        Press 2             Press 3   ");
            try{
                System.out.print("\n-> What do you want to do? (press 4 to exit): ");
                subMenuOption = Integer.parseInt(subMenuReader.nextLine());
                if(subMenuOption > 0 && subMenuOption <= 4){
                    switch (subMenuOption){
                        case 1:
                            subject.loadFile();
                            break;
                        case 2:
                            subject.addStudent();
                            break;
                        case 3:
                            subject.generateReport();
                            break;
                        case 4:
                            subMenuExit = true;
                            break;
                        default:
                            System.out.println("Select an option between 1 and 4.");
                            break;
                    }
                } else {
                    System.out.println("\nInvalid option, please type a number between 1 and 4.\n");
                }
            } catch(NumberFormatException e) {
                System.out.println("\nInvalid number format, please select an option between 1 and 4.\n");
            }
        } while(!subMenuExit);
    }
}