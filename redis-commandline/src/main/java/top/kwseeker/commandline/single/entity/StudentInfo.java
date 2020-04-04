package top.kwseeker.commandline.single.entity;

public class StudentInfo {

    private String name;
    private int age;
    private String sex;
//    private String grade;
    private String gradeFK;

    public StudentInfo(String name, int age, String sex, String mathGrade, String gradeFK) {
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.gradeFK = gradeFK;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getGradeFK() {
        return gradeFK;
    }

    public void setGradeFK(String gradeFK) {
        this.gradeFK = gradeFK;
    }
}
