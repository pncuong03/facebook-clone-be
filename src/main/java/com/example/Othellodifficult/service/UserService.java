package com.example.Othellodifficult.service;

import com.example.Othellodifficult.cloudinary.CloudinaryHelper;
import com.example.Othellodifficult.common.Common;
import com.example.Othellodifficult.dto.user.ChangeInfoUserRequest;
import com.example.Othellodifficult.dto.user.UserOutputV2;
import com.example.Othellodifficult.dto.user.UserRequest;
import com.example.Othellodifficult.entity.UserEntity;
import com.example.Othellodifficult.helper.StringUtils;
import com.example.Othellodifficult.mapper.UserMapper;
import com.example.Othellodifficult.repository.UserRepository;
import com.example.Othellodifficult.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private UserEntity getUserBy(Long userId){
        return userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException(Common.RECORD_NOT_FOUND)
        );
    }

    @Transactional(readOnly = true)
    public UserOutputV2 getUserInformation(String accessToken){
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = getUserBy(userId);
        return userMapper.getOutputFromEntity(userEntity);
    }

    @Transactional
    public void changeUserInformation(ChangeInfoUserRequest changeInfoUserRequest,
                                      String accessToken,
                                      MultipartFile avatar, MultipartFile background){
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = getUserBy(userId);
        userMapper.updateEntityFromInput(userEntity, changeInfoUserRequest);
        userEntity.setBirthday(OffsetDateTime.parse(changeInfoUserRequest.getBirthdayString()));
        if(Objects.isNull(background)){
            userEntity.setBackgroundUrl(Common.DEFAULT_BACKGROUND);
        }else{
            userEntity.setBackgroundUrl(CloudinaryHelper.uploadAndGetFileUrl(background));
        }
        if(Objects.isNull(avatar)){
            userEntity.setImageUrl(Common.DEFAULT_IMAGE_URL);
        }else{
            userEntity.setImageUrl(CloudinaryHelper.uploadAndGetFileUrl(avatar));
        }
        userRepository.save(userEntity);
    }

    @Transactional
    public String signUp(UserRequest signUpRequest){
        if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))){
            throw new RuntimeException(Common.USERNAME_IS_EXISTS);
        }
        signUpRequest.setPassword(BCrypt.hashpw(signUpRequest.getPassword(), BCrypt.gensalt()));
        UserEntity userEntity = userMapper.getEntityFromRequest(signUpRequest);
        userEntity.setImageUrl(Common.DEFAULT_IMAGE_URL);
        userEntity.setBackgroundUrl(Common.DEFAULT_BACKGROUND);
        userEntity.setFullName(signUpRequest.getFullName());
        userRepository.save(userEntity);
        return TokenHelper.generateToken(userEntity);
    }

    @Transactional(readOnly = true)
    public String logIn(UserRequest logInRequest){
        UserEntity userEntity = userRepository.findByUsername(logInRequest.getUsername());
        if (Objects.isNull(userEntity)){
            throw new RuntimeException(Common.INCORRECT_PASSWORD);
        }
        String currentHashedPassword = userEntity.getPassword(); // password DB
        if (BCrypt.checkpw(logInRequest.getPassword(), currentHashedPassword)){
            return TokenHelper.generateToken(userEntity);
        }
        throw new RuntimeException(Common.INCORRECT_PASSWORD);
    }

    private List<String> getImageUrls(List<MultipartFile> multipartFiles){
        if (Objects.isNull(multipartFiles) || multipartFiles.isEmpty()){
            return new ArrayList<>();
        }
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles){
            imageUrls.add(CloudinaryHelper.uploadAndGetFileUrl(multipartFile));
        }
        return imageUrls;
    }
}
