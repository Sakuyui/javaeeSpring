package com.akb.springsql.mapper;

import com.akb.springsql.pojo.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentMapper {
    @Select("select * from student")
    List<Student> getAllStudentList(String id);

    @Select("select * from student where id like CONCAT('%',#{str},'%') or " +
            "name like CONCAT('%',#{str},'%') or phone like CONCAT('%',#{str},'%') or " +
            "qq like CONCAT('%',#{str},'%') or email like CONCAT('%',#{str},'%')")
    List<Student> getFuzzyQueryStudentList(String str);
    @Insert("insert into student (id,name,phone,qq,email) " +
            "values (#{id},#{name},#{phone},#{qq},#{email})")
    public int add(Student student);

    @Delete(" delete from student where id= #{id} ")
    public void del(String id);

    @Select("select * from student where id= #{id} ")
    public Student getStudent(String id);

    @Update("update users set " +
            "mobName = #{mobName}," +
            "userName = #{userName}," +
            "nickname = #{nickname}," +
            "phone = #{phone}," +
            "register = #{register} " +
            "where id=#{id} ")
    public int update(Student users);

}
