package com.dataincloud.api.controllers.profile;

import com.dataincloud.api.Application;
import com.dataincloud.core.profile.Profile;
import com.dataincloud.dal.profile.ProfileDocument;
import com.dataincloud.services.profile.dto.ProfileDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static com.dataincloud.core.profile.Profile.ProfileTags.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@Testcontainers
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
class ProfileControllerIntegrationTest {

    @Container
    private static final MongoDBContainer mongoDb = new MongoDBContainer("mongo:latest").withExposedPorts(27017);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDb::getReplicaSetUrl);
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void fillDbWithTestData() {
        ProfileDocument testProfile = new ProfileDocumentBuilder()
                .userId(1L)
                .photo(new Byte[]{0, 0, 1, 0})
                .firstName("FirstName1")
                .lastName("LastName1")
                .birthDate(LocalDate.of(2002, 3, 18))
                .tagsList(List.of(EDUCATION))
                .build();
        mongoTemplate.save(testProfile);
    }

    @AfterEach
    void cleanDb() {
        mongoTemplate.dropCollection("profiles");
    }

    static class ProfileDtoBuilder {
        private Long userId;
        private Byte[] photo;
        private String firstName;
        private String lastName;
        private LocalDate birthDate;
        private List<Profile.ProfileTags> tags;

        public ProfileDtoBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public ProfileDtoBuilder photo(Byte[] photo) {
            this.photo = photo;
            return this;
        }

        public ProfileDtoBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public ProfileDtoBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public ProfileDtoBuilder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public ProfileDtoBuilder birthDate(int year, int month, int dayOfMonth) {
            this.birthDate = LocalDate.of(year, month, dayOfMonth);
            return this;
        }

        public ProfileDtoBuilder tags(List<Profile.ProfileTags> tags) {
            this.tags = tags;
            return this;
        }

        public ProfileDto build() {
            ProfileDto profileDto = new ProfileDto();
            profileDto.setUserId(this.userId);
            profileDto.setPhoto(this.photo);
            profileDto.setFirstName(this.firstName);
            profileDto.setLastName(this.lastName);
            profileDto.setBirthDate(this.birthDate);
            profileDto.setTags(this.tags);
            return profileDto;
        }
    }

    static class ProfileDocumentBuilder {
        private Long userId;
        private String firstName;
        private String lastName;
        private Byte[] photo;
        private LocalDate birthDate;
        private List<Profile.ProfileTags> tagsList;

        public ProfileDocumentBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public ProfileDocumentBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public ProfileDocumentBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public ProfileDocumentBuilder photo(Byte[] photo) {
            this.photo = photo;
            return this;
        }

        public ProfileDocumentBuilder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public ProfileDocumentBuilder tagsList(List<Profile.ProfileTags> tagsList) {
            this.tagsList = tagsList;
            return this;
        }

        public ProfileDocument build() {
            ProfileDocument profileDocument = new ProfileDocument();
            profileDocument.setUserId(this.userId);
            profileDocument.setFirstName(this.firstName);
            profileDocument.setLastName(this.lastName);
            profileDocument.setPhoto(this.photo);
            profileDocument.setBirthDate(this.birthDate);
            profileDocument.setTagsList(this.tagsList);
            return profileDocument;
        }
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createValidProfileTest() throws Exception{
        ProfileDto newProfile = new ProfileDtoBuilder()
                .userId(2L)
                .photo(new Byte[]{0, 0, 1, 1})
                .firstName("NewFirstName")
                .lastName("NewLastName")
                .birthDate(1999, 7, 11)
                .tags(List.of(BLOG))
                .build();

        mockMvc.perform(
                put("/users/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProfile))
        ).andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource("setInvalidProfiles")
    void createAndUpdateInvalidProfileTest(ProfileDto invalidProfile) throws Exception{
        mockMvc.perform(
                put("/users/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProfile))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void updateValidProfileTest() throws Exception{
        ProfileDto newProfile = new ProfileDtoBuilder()
                .userId(1L)
                .photo(new Byte[]{0, 0, 1, 1})
                .firstName("NewFirstName")
                .lastName("NewLastName")
                .birthDate(1999, 7, 11)
                .tags(List.of(BLOG))
                .build();

        mockMvc.perform(
                put("/users/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProfile))
        ).andExpect(status().isOk());
    }

    @Test
    void getAllProfilesTest() throws Exception {
        mockMvc.perform(get("/users/profiles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void getProfileByExistingIdTest() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/users/{userId}/profiles", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void getProfileByNonExistentIdTest() throws Exception {
        Long userId = 2L;

        mockMvc.perform(get("/users/{userId}/profiles", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteExistingProfileTest() throws Exception {
        Long userId = 1L;

        mockMvc.perform(delete("/users/{userId}/profiles", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(userId));
    }

    @Test
    void deleteNonExistentProfileTest() throws Exception {
        Long userId = 2L;

        mockMvc.perform(delete("/users/{userId}/profiles", userId))
                .andExpect(status().isNotFound());
    }

    static Stream<Arguments> setInvalidProfiles() {
        return Stream.of(
                Arguments.of(
                        new ProfileDtoBuilder()
                                .userId(51L)
                                .photo(new Byte[]{0, 1})
                                .firstName("firstName")
                                .lastName("lastName")
                                .birthDate(2000, 1, 27)
                                .tags(List.of(BLOG))
                                .build()
                ),
                Arguments.of(
                        new ProfileDtoBuilder()
                                .userId(2L)
                                .photo(new Byte[]{})
                                .firstName("firstName")
                                .lastName("lastName")
                                .birthDate(2000, 1, 27)
                                .tags(List.of(BLOG))
                                .build()
                ),
                Arguments.of(
                        new ProfileDtoBuilder()
                                .userId(2L)
                                .photo(new Byte[]{0, 1})
                                .firstName("  ")
                                .lastName("lastName")
                                .birthDate(2000, 1, 27)
                                .tags(List.of(BLOG))
                                .build()
                ),
                Arguments.of(
                        new ProfileDtoBuilder()
                                .userId(2L)
                                .photo(new Byte[]{0, 1})
                                .firstName("firstName")
                                .lastName(" ")
                                .birthDate(2000, 1, 27)
                                .tags(List.of(BLOG))
                                .build()
                ),
                Arguments.of(
                        new ProfileDtoBuilder()
                                .userId(2L)
                                .photo(new Byte[]{0, 1})
                                .firstName("firstName")
                                .lastName(" ")
                                .birthDate(2030, 1, 27)
                                .tags(List.of(BLOG))
                                .build()
                ),
                Arguments.of(
                        new ProfileDtoBuilder()
                                .userId(2L)
                                .photo(new Byte[]{0, 1})
                                .firstName("firstName")
                                .lastName(" ")
                                .birthDate(2000, 1, 27)
                                .tags(List.of())
                                .build()
                )
        );
    }

}