package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.cloudinary.CloudinaryHelper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/upload")
@AllArgsConstructor
public class Test {
    @PostMapping("/test")
    public String upload(@RequestParam(name = "pfile") MultipartFile multipartFile){
        return CloudinaryHelper.uploadAndGetFileUrl(multipartFile);
    }
}
