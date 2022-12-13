package dataaccess;

import domain.Course;
import domain.CourseType;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MysqlCourseRepository implements MyCourseRepository{

    private Connection con;

    public MysqlCourseRepository() throws SQLException,ClassNotFoundException
    {
        try {
            this.con=MysqlDatabaseConnection.getConnection("jdbc:mysql://localhost:3306/kurssystem","root","");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Course> insert(Course entity) {
        return Optional.empty();
    }

    @Override
    public Optional<Course> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Course> getAll() {
        String sql= "SELECT * FROM `courses`";
        try {
            PreparedStatement preparedStatement= con.prepareStatement(sql);
            ResultSet rs=preparedStatement.executeQuery();
            ArrayList<Course> CourseList = new ArrayList<>();
            while (rs.next()){
                CourseList.add( new Course(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("hours"),
                        rs.getDate("begindate"),
                        rs.getDate("enddate"),
                        CourseType.valueOf(rs.getString("coursetype"))
                )
                );

            }
            return CourseList;
        } catch (SQLException e) {
            throw new DatabaseException("Database error occurred!");
        }
    }

    @Override
    public Optional<Course> update(Course entity) {
        return Optional.empty();
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public List<Course> findAllCoursesByName(String name) {
        return null;
    }

    @Override
    public List<Course> findAllCoursesByDescription(String description) {
        return null;
    }

    @Override
    public List<Course> findAllCoursesByNameOrDescription(String searchText) {
        return null;
    }

    @Override
    public List<Course> findAllCoursesByCourseType(CourseType courseType) {
        return null;
    }

    @Override
    public List<Course> findAllRunningCourses() {
        return null;
    }

    @Override
    public List<Course> findAllCoursesByStartDate(Date startDate) {
        return null;
    }
}
