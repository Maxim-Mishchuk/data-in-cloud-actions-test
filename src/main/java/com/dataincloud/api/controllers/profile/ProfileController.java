package com.dataincloud.api.controllers.profile;

import com.dataincloud.core.exceptions.ResourceNotFoundException;
import com.dataincloud.services.profile.ProfileService;
import com.dataincloud.services.profile.dto.ProfileDto;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping("/users/profiles")
    public List<ProfileDto> getAll() {
        return profileService.readAll();
    }

    @GetMapping("/users/{id}/profiles")
    public ProfileDto getById(@PathVariable("id") Long userId) {
        return profileService.readById(userId);
    }

    @PutMapping("/users/profiles")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "200", description = "Updated")
    })
    public ResponseEntity<ProfileDto> save(@RequestBody @Valid ProfileDto inputProfile) {
        try {
            ProfileDto updatedProfile = profileService.update(inputProfile);
            return ResponseEntity.status(HttpStatus.OK).body(updatedProfile);
        } catch (ResourceNotFoundException e) {
            ProfileDto createdProfile = profileService.create(inputProfile);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProfile);
        }
    }

    @DeleteMapping("/users/{id}/profiles")
    public ProfileDto delete(@PathVariable("id") Long userId) {
        return profileService.deleteById(userId);
    }
}
