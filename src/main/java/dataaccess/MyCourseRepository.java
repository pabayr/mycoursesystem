package dataaccess;

import domain.Course;
import domain.CourseType;

import java.util.Date;
import java.util.List;


//Das eigentliche DAO-Interface.
//Extended nur CRUD-Methoden des BaseRepos.

public interface MyCourseRepository extends BaseRepository<Course, Long> {
    //Hier werden die spezifischen Typen nun angegeben anstatt T,I

    //Kursspezifische Methoden

    List<Course> findAllCoursesByName(String name);

    List<Course> findAllCoursesByDescription(String description);

    List<Course> findAllCoursesByNameOrDescription(String searchText);


    List<Course> findAllCoursesByCourseType(CourseType courseType);

    List<Course> findAllRunningCourses();

    List<Course> findAllCoursesByStartDate(Date startDate);


}
