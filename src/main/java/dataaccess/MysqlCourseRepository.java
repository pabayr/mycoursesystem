package dataaccess;

import domain.Course;
import domain.CourseType;
import util.Assert;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class MysqlCourseRepository implements MyCourseRepository {

    private Connection con;

    public MysqlCourseRepository() throws SQLException, ClassNotFoundException {
        try {
            this.con = MysqlDatabaseConnection.getConnection("jdbc:mysql://localhost:3306/kurssystem", "root", "");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<Course> insert(Course entity) {
        Assert.notNull(entity);


        try {
            String sql = "INSERT INTO `courses`(`name`,`description`,`hours`,`begindate`,`enddate`,`coursetype`) VALUES(?,?,?,?,?,?)";
            PreparedStatement preparedStatement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getDescription());
            preparedStatement.setInt(3, entity.getHours());
            preparedStatement.setDate(4, entity.getBeginDate());
            preparedStatement.setDate(5, entity.getEndDate());
            preparedStatement.setString(6, entity.getCourseType().toString());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                return Optional.empty();
            }
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return this.getById(generatedKeys.getLong(1));
            } else {
                return Optional.empty();
            }

        } catch (SQLException sqlException) {
            throw new DatabaseException(sqlException.getMessage());

        }

    }

    @Override
    public Optional<Course> getById(Long id) {
        Assert.notNull(id);
        if (countCoursesInDbWithId(id) == 0) {
            return Optional.empty();
        } else {
            try {
                String sql = "SELECT * FROM `courses` WHERE `id`=?";
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setLong(1, id);
                ResultSet rs = preparedStatement.executeQuery();
                rs.next();
                Course course = new Course(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("hours"),
                        rs.getDate("begindate"),
                        rs.getDate("enddate"),
                        CourseType.valueOf(rs.getString("coursetype"))
                );
                return Optional.of(course);

            } catch (SQLException e) {
                throw new DatabaseException(e.getMessage());
            }

        }
    }

    private int countCoursesInDbWithId(long id) {
        try {
            String countSql = "SELECT COUNT(*) FROM `courses` WHERE `id`=?";
            PreparedStatement preparedStatementCount = con.prepareStatement(countSql);
            preparedStatementCount.setLong(1, id);
            ResultSet resultSetCount = preparedStatementCount.executeQuery();
            resultSetCount.next();
            int courseCount = resultSetCount.getInt(1);
            return courseCount;
        } catch (SQLException sqlException) {
            throw new DatabaseException(sqlException.getMessage());
        }
    }


    @Override
    public List<Course> getAll() {
        String sql = "SELECT * FROM `courses`";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            ArrayList<Course> CourseList = new ArrayList<>();
            while (rs.next()) {
                CourseList.add(new Course(
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
        Assert.notNull(entity);
        String sql = "UPDATE `courses` SET `name` = ? , `description` = ? , `hours` = ? , `begindate`=? ,`enddate` = ? , `coursetype`= ? WHERE `courses`.`id`=?";
        if (countCoursesInDbWithId(entity.getId()) == 0) {
            return Optional.empty();
        } else {


            try {
                PreparedStatement preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, entity.getName());
                preparedStatement.setString(2, entity.getDescription());
                preparedStatement.setInt(3, entity.getHours());
                preparedStatement.setDate(4, entity.getBeginDate());
                preparedStatement.setDate(5, entity.getEndDate());
                preparedStatement.setString(6, entity.getCourseType().toString());
                preparedStatement.setLong(7,entity.getId());

                int affectedRows= preparedStatement.executeUpdate();
                if(affectedRows==0)
                {
                    return Optional.empty();
                } else
                {
                    return this.getById(entity.getId());
                }
            } catch (SQLException sqlException) {
                throw new DatabaseException(sqlException.getMessage());

            }
        }
    }

    @Override
    public void deleteById(Long id) {
       Assert.notNull(id);
       String sql="DELETE FROM `courses` WHERE id=?";
       try {
           if (countCoursesInDbWithId(id) == 1) {
               PreparedStatement preparedStatement = con.prepareStatement(sql);
               preparedStatement.setLong(1, id);
               preparedStatement.executeUpdate();
           }
       }catch(SQLException sqlException){
               throw new DatabaseException(sqlException.getMessage());
       }
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
        try{
            String sql="SELECT * FROM `courses` WHERE LOWER(`description`) LIKE LOWER(?) OR LOWER(`name`) LIKE LOWER (?)";
            PreparedStatement preparedStatement=con.prepareStatement(sql);
            preparedStatement.setString(1,"%"+searchText+"%");
            preparedStatement.setString(2,"%"+searchText+"%");
            ResultSet rs = preparedStatement.executeQuery();
            ArrayList<Course> courseArrayList= new ArrayList<>();
            while(rs.next())
            {
                courseArrayList.add(new Course(
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
            return courseArrayList;
        }catch(SQLException sqlException){
            throw new DatabaseException(sqlException.getMessage());

        }

    }

    @Override
    public List<Course> findAllCoursesByCourseType(CourseType courseType) {
        return null;
    }

    @Override
    public List<Course> findAllRunningCourses(){

        try {
            String sql = "SELECT * FROM `courses` WHERE NOW()<`enddate`";
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            ArrayList<Course> courseList = new ArrayList<>();
            while (rs.next()) {
                courseList.add(new Course(rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("hours"),
                        rs.getDate("begindate"),
                        rs.getDate("enddate"),
                        CourseType.valueOf(rs.getString("coursetype"))

                ));

            }
            return courseList;
        }catch(SQLException sqlException){
            throw new DatabaseException("Datenbankfehler :" +sqlException.getMessage());
        }

    }

    @Override
    public List<Course> findAllCoursesByStartDate(Date startDate) {
        return null;
    }
}
