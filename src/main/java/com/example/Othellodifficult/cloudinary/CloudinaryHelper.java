package com.example.Othellodifficult.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.Othellodifficult.common.Common;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// https://console.cloudinary.com/pm/c-ec1c837df708ade0d599b18e2b553b/getting-started
// https://dev.to/hackmamba/uploading-media-in-spring-boot-programmatically-with-cloudinary-35bm
public class CloudinaryHelper {
    public static Cloudinary cloudinary;

    static {
        cloudinary = new Cloudinary(
                ObjectUtils.asMap(
                        Common.CLOUDINARY_NAME, Common.CLOUDINARY_NAME_VALUE,
                        Common.CLOUDINARY_API_KEY, Common.CLOUDINARY_API_KEY_VALUE,
                        Common.CLOUDINARY_API_SECRET, Common.CLOUDINARY_API_SECRET_VALUE
                )
        );
        System.out.println("SUCCESS GENERATE INSTANCE FOR CLOUDINARY");
    }

    public static String uploadAndGetFileUrl(MultipartFile multipartFile){
        try {
            File uploadedFile = convertMultiPartToFile(multipartFile);
            Map uploadResult = cloudinary.uploader().uploadLarge(uploadedFile, ObjectUtils.emptyMap());
            return  uploadResult.get("url").toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

//    public static List<String> uploadAndGetFileUrls(MultipartFile[] multipartFiles) {
//        try {
//            // Tạo mảng byte để lưu trữ dữ liệu của tất cả các tệp
//            byte[][] fileBytes = new byte[multipartFiles.length][];
//
//            // Lấy dữ liệu của từng tệp và lưu vào mảng byte
//            for (int i = 0; i < multipartFiles.length; i++) {
//                fileBytes[i] = multipartFiles[i].getBytes();
//            }
//
//            // Tải lên các tệp dưới dạng mảng byte một lần
//            Map uploadResults = cloudinary.uploader().uploadLarge(new ByteArrayInputStream(concatenateByteArrays(fileBytes)), ObjectUtils.emptyMap());
//
//            // Tạo danh sách để lưu trữ các URL
//            List<String> fileUrls = uploadResults.
//
//            // Trích xuất URL của từng tệp đã tải lên và thêm vào danh sách
//            for (Map uploadResult : uploadResults) {
//                fileUrls.add(uploadResult.get("url").toString());
//            }
//
//            // Trả về danh sách URL
//            return fileUrls;
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to upload files: " + e.getMessage(), e);
//        }
//    }
//
//     Phương thức này kết hợp mảng byte của tất cả các tệp thành một mảng byte duy nhất
//    private static byte[] concatenateByteArrays(byte[][] arrays) {
//        int totalLength = 0;
//        for (byte[] array : arrays) {
//            totalLength += array.length;
//        }
//
//        byte[] result = new byte[totalLength];
//        int currentIndex = 0;
//        for (byte[] array : arrays) {
//            System.arraycopy(array, 0, result, currentIndex, array.length);
//            currentIndex += array.length;
//        }
//        return result;
//    }
}
