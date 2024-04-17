package com.dataincloud.dal.profile;

import com.dataincloud.core.exceptions.ResourceNotFoundException;
import com.dataincloud.core.profile.IProfileRepository;
import com.dataincloud.core.profile.Profile;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class ProfileRepository implements IProfileRepository {
    private final ProfileMongoDbRepository profileRepository;
    private final ModelMapper modelMapper;

    @Override
    public Profile create(Profile profile) {
        return modelMapper.map(
                profileRepository.save(modelMapper.map(profile, ProfileDocument.class)),
                Profile.class
        );
    }

    @Override
    public List<Profile> readAll() {
        return profileRepository.findAll().stream()
                .map(profileDocument -> modelMapper.map(profileDocument, Profile.class))
                .toList();
    }

    @Override
    public Profile readById(Long userId) {
        return modelMapper.map(
                profileRepository.findById(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Profile not found")
                        ),
                Profile.class
        );
    }

    @Override
    public Profile update(Profile editedProfile) {
        if(!profileRepository.existsById(editedProfile.getUserId()))
            throw new ResourceNotFoundException("Profile not found");

        return modelMapper.map(
                profileRepository.save(modelMapper.map(editedProfile, ProfileDocument.class)),
                Profile.class
        );
    }

    @Override
    public Profile delete(Long userId) {
        Profile deletedProfile = modelMapper.map(
                profileRepository.findById(userId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Profile not found"))
                ,
                Profile.class
        );
        profileRepository.deleteById(userId);
        return deletedProfile;
    }
}