package com.blog.blog_project.aws_config_services;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.hibernate.result.Output;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

@Service
public class AWSs3Client {

    @Value("${aws.access_id}")
    private String accessKey;

    @Value("${aws.secret_key}")
    private String secretKey;

    @Value("${aws.endpointUrl}")
    private String endpointURL;

    @Value("${aws.bucketName}")
    private String bucketName;

    private AmazonS3 s3Client;


    @PostConstruct
    public void init() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.US_EAST_1)
                .build();
    }

    //converts the multipart file into a java.io.File object
    //TODO Need a check method to restrict file types accepted
    private File convertMultipartToFile(MultipartFile file) throws IOException {
        //Look at using optional to avoid possible null pointer.
        Optional<MultipartFile> optionalFile = Optional.ofNullable(file);

//            File newFile = new File(optionalFile.orElseThrow(IOException::new).getOriginalFilename());
        assert file != null; //?Necessary?
        File newFile = new File(file.getOriginalFilename());
            FileOutputStream output = new FileOutputStream(newFile);
            output.write(file.getBytes());
            output.close();
        return newFile;
    }

    //puts object in s3 with public read permissions
    private void uploadToS3(String fileName, File file) {
        s3Client.putObject
                (new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    //Generates filename from a timestamp and the original filename
    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename()
                .replace(" ", "_");
    }

    public String uploadFile(MultipartFile multipartFile) {
        String fileUrl = "";

        try{
            File file = convertMultipartToFile(multipartFile);
            String fileName = generateFileName(multipartFile);
            fileUrl = endpointURL + "/" + bucketName + "/" + fileName;
            uploadToS3(fileName, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrl;
    }
}
