package com.dataincloud.core.profile;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.builder.HashCodeExclude;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Data
public class Profile {
    @ToString.Exclude @HashCodeExclude
    private Long userId;
    private String firstName;
    private String lastName;
    private Byte[] photo;
    private LocalDate birthDate;
    private List<ProfileTags> tags = new LinkedList<>();

    public enum ProfileTags {
        EDUCATION,
        BLOG,
        SHOP

    }
}
