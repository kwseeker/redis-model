package top.kwseeker.commandline.single.entity;

public class StudentGrade {

    private String mathGrade;
    private String physicsGrade;
    private String chemistryGrade;
    private String computerScienceGrade;

    public StudentGrade(String mathGrade, String physicsGrade, String chemistryGrade, String computerScienceGrade) {
        this.mathGrade = mathGrade;
        this.physicsGrade = physicsGrade;
        this.chemistryGrade = chemistryGrade;
        this.computerScienceGrade = computerScienceGrade;
    }

    public String getMathGrade() {
        return mathGrade;
    }

    public void setMathGrade(String mathGrade) {
        this.mathGrade = mathGrade;
    }

    public String getPhysicsGrade() {
        return physicsGrade;
    }

    public void setPhysicsGrade(String physicsGrade) {
        this.physicsGrade = physicsGrade;
    }

    public String getChemistryGrade() {
        return chemistryGrade;
    }

    public void setChemistryGrade(String chemistryGrade) {
        this.chemistryGrade = chemistryGrade;
    }

    public String getComputerScienceGrade() {
        return computerScienceGrade;
    }

    public void setComputerScienceGrade(String computerScienceGrade) {
        this.computerScienceGrade = computerScienceGrade;
    }
}
