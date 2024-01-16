package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Entity.*;
import com.korea.project2_team4.Repository.ImageRepository;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

@Service
@Builder
public class ImageService {
    private final ImageRepository imageRepository;

    @Value("${ImgLocation}")
    public String imgLocation;

    public boolean deleteExistingFile(String existingFilePath) {
        if (existingFilePath != null && !existingFilePath.isEmpty()) {
            File existingFile = new File(existingFilePath);
            if (existingFile.exists()) {
                return existingFile.delete();
            }
        }
        return false;
    }

    public void saveDefaultImgsForProfile(Profile profile) throws Exception {
        String projectpath = imgLocation;
        String filename = "no_img.jpg";
        File defaultImage = new File(projectpath, filename);

        if (!defaultImage.exists()) {
            ClassPathResource defaultImageResource = new ClassPathResource("static/no_img.jpg");
            Files.copy(defaultImageResource.getInputStream(), defaultImage.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        String filePath = Paths.get(projectpath, filename).toString();

        if (defaultImage.exists()) {
            Image defaultimg = new Image();
            defaultimg.setFileName(filename);
            defaultimg.setFilePath(filePath);
            defaultimg.setProfileImage(profile);

            this.imageRepository.save(defaultimg);
        }

    }

    public void saveDefaultImgsForPet(Pet pet) throws Exception {
        String projectpath = imgLocation;
        String filename = "no_img.jpg";
        File defaultImage = new File(projectpath, filename);

        if (!defaultImage.exists()) {
            ClassPathResource defaultImageResource = new ClassPathResource("static/no_img.jpg");
            Files.copy(defaultImageResource.getInputStream(), defaultImage.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        String filePath = Paths.get(projectpath, filename).toString();

        if (defaultImage.exists()) {
            Image defaultimg = new Image();
            defaultimg.setFileName(filename);
            defaultimg.setFilePath(filePath);
            defaultimg.setPetImage(pet);

            this.imageRepository.save(defaultimg);
        }

    }


    private String generateRandomFileName(String originalFileName) {
        // 확장자를 포함한 원본 파일 이름에서 확장자를 분리합니다.
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));

        // UUID를 이용하여 랜덤한 파일 이름을 생성합니다.
        String randomFileName = UUID.randomUUID().toString();

        // 확장자를 다시 추가하여 최종 파일 이름을 생성합니다.
        return randomFileName + fileExtension;
    }

    public void uploadPostImage(List<MultipartFile> multipartFiles, Post post) throws IOException, NoSuchAlgorithmException {
        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = multipartFile.getOriginalFilename();
            assert fileName != null;

            if (multipartFile.isEmpty()) {
                continue;
            }//multipartFile 이 비어 있다면 continue 문을 실행 하여 현재 반복을 중지 하고 다음 반복문 으로 넘어갈 것

            String saveName = generateRandomFileName(fileName);

            String savePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

            if (!new File(savePath).exists()) {
                try {
                    new File(savePath).mkdir();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            String filePath = savePath + "\\" + saveName;

            File origFile = new File(filePath);
            multipartFile.transferTo(origFile);

            createPostImage(fileName, saveName, filePath, post);

        }
    }

    public void uploadResalePostImage(List<MultipartFile> multipartFiles, ResalePost resalePost) throws IOException, NoSuchAlgorithmException {
        for (MultipartFile multipartFile : multipartFiles) {
            String fileName = multipartFile.getOriginalFilename();
            assert fileName != null;

            if (multipartFile.isEmpty()) {
                continue;
            }//multipartFile 이 비어 있다면 continue 문을 실행 하여 현재 반복을 중지 하고 다음 반복문 으로 넘어갈 것

            String saveName = generateRandomFileName(fileName);

            String savePath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\files";

            if (!new File(savePath).exists()) {
                try {
                    new File(savePath).mkdir();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            String filePath = savePath + "\\" + saveName;

            File origFile = new File(filePath);
            multipartFile.transferTo(origFile);

            createResalePostImage(fileName, saveName, filePath, resalePost);

        }
    }

    public void createResalePostImage(String fileName, String saveName, String filePath, ResalePost resalePost) {
        Image image = new Image();

        image.setFileName(fileName);

        image.setSaveName(saveName);

        image.setFilePath(filePath);

        image.setResalePostImages(resalePost);

        this.imageRepository.save(image);

    }

    public void saveImgsForPet(Pet pet, MultipartFile file) throws Exception {
        if (pet != null && file != null && !file.isEmpty()) {
            if (pet.getPetImage() != null) { //새로등록말고 업데이트시, 기존이미지있으면 지우기
                String filepath = pet.getPetImage().getFilePath();
                if (filepath != null && !filepath.isEmpty()) {
                    deleteExistingFile(filepath);
                    this.imageRepository.delete(pet.getPetImage());
                }
            }

            String projectPath = imgLocation;
            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_" + file.getOriginalFilename();
            File saveFile = new File(projectPath, fileName);
            file.transferTo(saveFile);

            Image image = new Image();
            image.setFileName(fileName);
            image.setFilePath(saveFile.getAbsolutePath());
            image.setPetImage(pet);

            this.imageRepository.save(image);
        }
    }

    public void saveImgsForProfile(Profile profile, MultipartFile file) throws Exception {
        if (profile != null && file != null && !file.isEmpty()) {
            if (profile.getProfileImage() != null) {
                String filepath = profile.getProfileImage().getFilePath();
                if (filepath != null && !filepath.isEmpty()) {
                    deleteExistingFile(filepath);
                    this.imageRepository.delete(profile.getProfileImage());

                }
            }


            String projectPath = imgLocation;
            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_" + file.getOriginalFilename();
            File saveFile = new File(projectPath, fileName);
            file.transferTo(saveFile);

            Image image = new Image();
            image.setFileName(fileName);
            image.setFilePath(saveFile.getAbsolutePath());
            image.setProfileImage(profile);
            this.imageRepository.save(image);
        }
    }

    public void deleteProfileImage(Profile profile) {
        Image profileImage = profile.getProfileImage();
        String filepath = profile.getProfileImage().getFilePath();
        deleteExistingFile(filepath);
        this.imageRepository.delete(profileImage);

    }

    public void createPostImage(String fileName, String saveName, String filePath, Post post) {
        Image image = new Image();

        image.setFileName(fileName);

        image.setSaveName(saveName);

        image.setFilePath(filePath);

        image.setPostImages(post);

        this.imageRepository.save(image);

    }

    public void deleteImage(Image image) {
        imageRepository.delete(image);
    }

    public Image findBySaveName(String saveName) {
        return imageRepository.findBySaveName(saveName);
    }
}
