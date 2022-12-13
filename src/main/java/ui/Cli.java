package ui;

import dataaccess.DatabaseException;
import dataaccess.MyCourseRepository;
import domain.Course;
import domain.CourseType;
import domain.InvalidValueException;

import javax.xml.crypto.Data;
import java.sql.Date;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Cli {
    Scanner scan;
    MyCourseRepository repo;

    public Cli(MyCourseRepository repo) { //Referenz auf das DAO MyCourseRepo
        this.repo = repo;
        this.scan = new Scanner(System.in);
    }

    public void start() {
        String input = "-";
        while (!input.equals("x")) {
            showMenue();
            input = scan.nextLine();
            switch (input) {
                case "1":
                    addCourse();
                    break;
                case "2":
                    showAllCourses();
                    break;
                case "3":
                    showCourseDetails();
                    break;
                case "4":
                    updateCourseDetails();
                    break;
                case "5":
                    deleteCourse();
                    break;
                case"6":
                    courseSearch();
                    break;
                case"7":
                    runningCourses();
                    break;
                case "x":
                    System.out.println("Pfiat di");
                    break;
                default:
                    inputError();
                    break;
            }

        }
        scan.close();
    }

    private void runningCourses() {
        System.out.println("Aktuell laufende Kurse: ");
        List <Course> list;
        try{
            list=repo.findAllRunningCourses();
            for(Course course:list)
            {
                System.out.println(course);
            }

        }catch(DatabaseException databaseException){
            System.out.println("Datenbankfehler bei Kurs-Anzeige für laufende Kurse: "+ databaseException.getMessage());

        }catch (Exception exception)
        {
            System.out.println("Unbekannter Fehler :" +exception.getMessage());
        }
    }

    private void courseSearch() {
        System.out.println("Geben sie einen Suchbegriff an!");
        String searchString= scan.nextLine();
        List <Course> courseList;
        try{
            courseList=repo.findAllCoursesByNameOrDescription(searchString);
            for(Course course : courseList)
            {
                System.out.println(course);
            }
        }catch (DatabaseException databaseException){
            System.out.println("Datenbankfehler bei der Kurssuche :" +databaseException.getMessage());
        }catch (Exception exception)
        {
            System.out.println("Unbekannter Fehler bei Kurssuche :" +exception.getMessage());
        }
    }

    private void deleteCourse() {
        System.out.println("Welchen Kurs möchten Sie löschen? Bitte ID eingeben:");

        long courseIdToDelete=Long.parseLong(scan.nextLine());
        try{
            repo.deleteById(courseIdToDelete);
            System.out.println("Kurs mit ID " + courseIdToDelete + "gelöscht!");
        }
        catch(DatabaseException databaseException){
            System.out.println("Datenbankfehler beim löschen: "+ databaseException.getMessage());
        }catch(Exception e){
            System.out.println("Unbekannter Fehler beim Löschen :" +e.getMessage());
        }

    }

    private void updateCourseDetails() {
        System.out.println("Für welche Kurs-ID möchten Sie die Kursdetails ändern?");

        Long courseId=Long.parseLong(scan.nextLine());

        try{

            Optional<Course> courseOptional=repo.getById(courseId);
            if(courseOptional.isEmpty())
            {
                System.out.println("Kurs mit der gegebenen ID nicht in der Datenbank");
            } else{
                Course course = courseOptional.get();
                System.out.println("Änderungen für folgenden Kurs: ");
                System.out.println(course);

                String name, description, hours, dateFrom, dateTo, courseType;

                System.out.println("Bitte neuen Kursdaten angeben (Enter, falls keine Änderung gewünscht ist) :");
                System.out.println("Name :");
                name= scan.nextLine();
                System.out.println("Beschreibung :");
                description= scan.nextLine();
                System.out.println("Stundenanzahl :");
                hours= scan.nextLine();
                System.out.println("Startdatum :");
                dateFrom= scan.nextLine();
                System.out.println("Enddatum :");
                dateTo= scan.nextLine();
                System.out.println("Was für eine Art Kurs ist es? (ZA/BF/FF/OE) :");
                courseType=scan.nextLine();

                Optional<Course> optionalCourseUpdated=repo.update(
                        new Course(
                                course.getId(),
                                name.equals("") ? course.getName() : name ,       //ternäre Operatoren. Wenn xyz true ?dann bla :sonst bla
                                description.equals("") ? course.getDescription() : description ,
                                hours.equals("") ? course.getHours() : Integer.parseInt(hours) ,
                                dateFrom.equals("") ? course.getBeginDate() : Date.valueOf(dateFrom) ,
                                dateTo.equals("") ? course.getEndDate() : Date.valueOf(dateTo) ,
                                courseType.equals("") ? course.getCourseType() : CourseType.valueOf(courseType)

                        )
                );

                optionalCourseUpdated.ifPresentOrElse(
                        (c)-> System.out.println("Kurs aktualisiert: "+ c) , // wenn present dann kurs=c
                        ()-> System.out.println("Kurs konnte nicht aktualisiert werden!") //wenn nicht
                );
            }

        }catch(Exception exception){
            System.out.println("Unbekannter Fehler bei Kursupdate :" +exception.getMessage());
        }



    }

    private void addCourse() {
        String name, description;
        int hours;
        Date dateFrom, dateTo;
        CourseType courseType;

        try {
            System.out.println("Bitte alle Kursdaten angeben");
            System.out.println("Name: ");
            name = scan.nextLine();
            if (name.equals("")) throw new IllegalArgumentException("Eingabe darf nicht leer sein!");
            System.out.println("Beschreibung: ");
            description=scan.nextLine();
            if(description.equals("")) throw new IllegalArgumentException("Eingabe darf nicht leer sein!");
            System.out.println("Stundenanzahl: ");
            hours=Integer.parseInt(scan.nextLine());
            System.out.println("Startdatum (YYYY-MM-DD): ");
            dateFrom=Date.valueOf(scan.nextLine());
            System.out.println("Enddatum (YYYY-MM-DD): ");
            dateTo=Date.valueOf(scan.nextLine());
            System.out.println("Kurstyp: (ZA/BF/FF/OE) :");
            courseType=CourseType.valueOf(scan.nextLine());

            Optional <Course> optionalCourse=repo.insert(
                    new Course(name,description,hours,dateFrom,dateTo,courseType)
            );

            if(optionalCourse.isPresent())
            {
                System.out.println("Kurs angelegt: "+ optionalCourse.get());

            } else {
                System.out.println("Kurs konnte nicht angelegt werden!");
            }
        } catch (IllegalArgumentException illegalArgumentException) {
            System.out.println("Eingabefehler: " + illegalArgumentException.getMessage());
        } catch (InvalidValueException invalidValueException) {
            System.out.println("Kursdaten nicht korrekt angegeben: " + invalidValueException.getMessage());
        } catch (DatabaseException databaseException) {
            System.out.println("Datenbankfehler beim einfügen: " + databaseException.getMessage());
        } catch (Exception exception) {
            System.out.println("Unbekannter Fehler beim einfügen: " + exception.getMessage());
        }
    }

    private void showCourseDetails() {
        System.out.println("Für welchen Kurs möchten Sie die Kursdetails anzeigen?");
        Long courseId = Long.parseLong(scan.nextLine());
        try {
            Optional<Course> courseOptional = repo.getById(courseId);
            if (courseOptional.isPresent()) {
                System.out.println(courseOptional.get());
            } else {
                System.out.println("Kurs mit der ID " + courseId + " nicht gefunden!");

            }

        } catch (DatabaseException ex) {
            System.out.println("Datenbankfehler bei Kursdetailanzeige: " + ex.getMessage());

        } catch (Exception exception) {
            System.out.println("Unbekannter Fehler bei Kurs-Detailanzeige: " + exception.getMessage());
        }
    }

    private void showAllCourses() {
        List<Course> list = null;

        try {
            list = repo.getAll();
            if (list.size() > 0) {
                for (Course course : list) {
                    System.out.println(course);
                }
            } else {
                System.out.println("Kursliste leer");
            }

        } catch (DatabaseException databaseException) {
            System.out.println("Datenbankfehler bei Anzeige aller Kurse: " + databaseException.getMessage());
        } catch (Exception exception) {
            System.out.println("Unbekannter Fehler bei Anzeige aller Kurse " + exception.getMessage());
        }
    }

    private void showMenue() {
        System.out.println("-------------KURSMANAGEMENT-------------");
        System.out.println("(1) Kurs eingeben \t (2) Alle Kurse anzeigen \t" + "(3) Kursdetails anzeigen");
        System.out.println("(4) Kursdetails ändern \t (5) Kurs löschen \t" + "(6) Kurssuche");
        System.out.println("(7) Laufende Kurse \t (-) xxxxxxx \t (-) xxxxxxxx");
        System.out.println("(x) ENDE");
    }

    private void inputError() {
        System.out.println("Bitte nur die Optionen der Menüauswahl eingeben!");
    }

}
