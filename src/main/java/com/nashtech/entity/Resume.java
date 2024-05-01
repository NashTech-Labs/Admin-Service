package com.nashtech.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Resume {
    private String candidateName;
    private String candidatePhone;
    private String candidateAge;
    private String candidateGender;
    private List<String> technicalSkills;
    private List<String> softSkills;
    private List<String> projectsDetails;
    private List<String> qualificationDetails;
    private List<String> certificationsDetails;
    private String totalExperienceYears;
    private int jobSwitchCount;
}
