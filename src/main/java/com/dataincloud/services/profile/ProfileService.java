package com.dataincloud.services.profile;

import com.dataincloud.core.profile.IProfileRepository;
import com.dataincloud.core.profile.Profile;
import com.dataincloud.services.profile.dto.ProfileDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;

import java.util.List;

@RequiredArgsConstructor
public class ProfileService {
    private final IProfileRepository profileRepository;
    private final ModelMapper modelMapper;

    public ProfileDto create(ProfileDto newProfile) {
        return modelMapper.map(
                profileRepository.create(modelMapper.map(newProfile, Profile.class)),
                ProfileDto.class
        );
    }

    public ProfileDto readById(Long userId) {
        return modelMapper.map(
                profileRepository.readById(userId),
                ProfileDto.class
        );
    }

    public List<ProfileDto> readAll() {
        return profileRepository.readAll().stream()
                .map(profile -> modelMapper.map(profile, ProfileDto.class))
                .toList();
    }

    public ProfileDto update(ProfileDto editedProfile) {
        return modelMapper.map(
                profileRepository.update(modelMapper.map(editedProfile, Profile.class)),
                ProfileDto.class
        );
    }

    public ProfileDto deleteById(Long userId) {
        return modelMapper.map(
                profileRepository.delete(userId),
                ProfileDto.class
        );
    }
}
