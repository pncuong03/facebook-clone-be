package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.cloudinary.CloudinaryHelper;
import com.example.Othellodifficult.dto.IdAndName;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/upload")
@AllArgsConstructor
@CrossOrigin
public class Test {
    @PostMapping("/test")
    public String upload(@RequestParam(name = "pfile") MultipartFile multipartFile){
        return CloudinaryHelper.uploadAndGetFileUrl(multipartFile);
    }
}
