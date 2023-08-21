package back.server.mybox.Domain.service;

import back.server.mybox.Domain.entity.FolderEntity;
import back.server.mybox.Domain.entity.UserEntity;
import back.server.mybox.Domain.entity.FileEntity;
import back.server.mybox.Domain.repository.FileRepository;
import back.server.mybox.Domain.repository.FolderRepository;
import back.server.mybox.Domain.repository.UserRepository;
import back.server.mybox.common.SecurityUtils;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class NcpService {
    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.region.static}")
    private String regionName;

    @Value("${cloud.aws.s3.endpoint}")
    private String endPoint;

    @Value("${cloud.aws.credentials.bucket}")
    private String bucketName;

    private final UserRepository userRepository;
    private final FolderRepository folderRepository;
    private final FileRepository fileRepository;
    private AmazonS3 s3builder(){
        AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
        return amazonS3;
    }

    private String ncpname(String Fname){
        UserEntity userEntity = userRepository.findByUsername(SecurityUtils.getCurrentUserId());
        String ncpname = userEntity.getUserId().toString() + "_" + Fname;
        return ncpname;
    }

    public Long createPrivateFolder(Long userId, String userName) {
        String ncpfoldername = userId.toString() + "_" + userName;
        s3builder().putObject(bucketName, ncpfoldername + "/", new ByteArrayInputStream(new byte[0]), new ObjectMetadata());

        UserEntity userEntity = userRepository.findByUserId(userId);

        FolderEntity folderEntity = FolderEntity.builder()
                .foldername(userName)
                .userEntity(userEntity)
                .createdAt(LocalDateTime.now())
                .build();
        folderRepository.save(folderEntity);

        return folderEntity.getFolderId();
    }

    public Long createFolder(String folderName, Long parentFolder) {
        UserEntity userEntity = userRepository.findByUsername(SecurityUtils.getCurrentUserId());
        String parentFoldername = folderRepository.findByFolderId(parentFolder).getFoldername();
        String parentncpname = ncpname(parentFoldername);
        String ncpfoldername = ncpname(folderName);
        s3builder().putObject(bucketName, parentncpname + "/" + ncpfoldername + "/", new ByteArrayInputStream(new byte[0]), new ObjectMetadata());

        FolderEntity folderEntity = FolderEntity.builder()
                .foldername(folderName)
                .createdAt(LocalDateTime.now())
                .userEntity(userEntity)
                .parentFolder(parentFolder)
                .build();
        folderRepository.save(folderEntity);

        return folderEntity.getFolderId();
    }

    public void deleteFolder(String folderName) {
        FolderEntity folder = folderRepository.findByFoldername(folderName);

        String replaceFolderName = (folderName + "/").replace(File.separatorChar, '/');
        String ncpfoldername =  ncpname(folderRepository.findByFolderId(folder.getParentFolder()).getFoldername())+"/"+ ncpname(replaceFolderName);

        s3builder().deleteObject(bucketName, ncpfoldername);

        folderRepository.delete(folder);
    }

    public String uploadFile(MultipartFile multipartFile, Long folderId) throws IOException {
        UserEntity userEntity = userRepository.findByUsername(SecurityUtils.getCurrentUserId());

        //방법 추가하기
        if (folderId.equals(null)){
            folderId = userEntity.getPrivateFolder();
        }

        String originalFilename = multipartFile.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        FolderEntity folder = folderRepository.findByFolderId(folderId);
        String ncpfoldername = (folder.getFoldername() + "/").replace(File.separatorChar, '/');
        String ncpfilename = ncpname(originalFilename);

        s3builder().putObject(bucketName, ncpfoldername + ncpfilename, multipartFile.getInputStream(), metadata);


        FileEntity fileEntity = FileEntity.builder()
                .filename(originalFilename)
                .folder(folder)
                .build();
        fileRepository.save(fileEntity);

        return s3builder().getUrl(bucketName, ncpfilename).toString();
    }

    public void deleteFile(String fileName) {
        String replaceFileName = (fileName).replace(File.separatorChar, '/');
        String ncpfilename = ncpname(replaceFileName);

        FileEntity file = fileRepository.findByFilename(fileName);

        String ncpfoldername = (file.getFolder().getFoldername() + "/").replace(File.separatorChar, '/');

        s3builder().deleteObject(bucketName, ncpfoldername + ncpfilename);
        fileRepository.delete(file);
    }

    public ResponseEntity<byte[]> downloadFile(String filename) throws IOException {
        //file 레포에서 파일 찾아서 부모 루트 달아주기
        String ncpfilename = ncpname(filename);

        S3Object s30bject = s3builder().getObject(new GetObjectRequest(bucketName, ncpfilename));
        S3ObjectInputStream objectInputStream = s30bject.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(contentType(ncpfilename));
        httpHeaders.setContentLength(bytes.length);

        String[] arr = ncpfilename.split("/");
        String type = arr[arr.length - 1];
        String fileName = URLEncoder.encode(type, "UTF-8").replaceAll("\\+", "%20");
        httpHeaders.setContentDispositionFormData("attachment", ncpfilename);
        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    private MediaType contentType(String keyname) {
        String[] arr = keyname.split("\\.");
        String type = arr[arr.length - 1];
        switch (type) {
            case "txt":
                return MediaType.TEXT_PLAIN;
            case "png":
                return MediaType.IMAGE_PNG;
            case "jpg":
                return MediaType.IMAGE_JPEG;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }


    //    public void createBucket(String bucketName) {
//        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
//                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
//                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
//                .build();
//
//        try {
//            if (s3.doesBucketExistV2(bucketName)) {
//                throw new RuntimeException("Bucket " + bucketName + " already exists.");
//            } else {
//                s3.createBucket(bucketName);
//            }
//        } catch (AmazonS3Exception e) {
//            e.printStackTrace();
//        } catch (SdkClientException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public List<Bucket> getBucketList() {
//    // S3 client
//        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
//                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
//                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
//                .build();
//
//        try {
//            List<Bucket> buckets = s3.listBuckets();
//            System.out.println("Bucket List: ");
//            for (Bucket bucket : buckets) {
//                System.out.println("    name=" + bucket.getName() + ", creation_date=" + bucket.getCreationDate() + ", owner=" + bucket.getOwner().getId());
//            }
//            return buckets;
//        } catch (AmazonS3Exception e) {
//            e.printStackTrace();
//        } catch (SdkClientException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

//    public void folderList(){
    //db에서 조회해도 괜찮지 않을까?

//        // S3 client
//        final AmazonS3 s3 = AmazonS3ClientBuilder.standard()
//                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
//                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
//                .build();
//
//        // list all in the bucket
//        try {
//            ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
//                    .withBucketName(bucketName)
//                    .withMaxKeys(300);
//
//            ObjectListing objectListing = s3.listObjects(listObjectsRequest);
//
//            System.out.println("Object List:");
//            while (true) {
//                for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
//                    System.out.println("    name=" + objectSummary.getKey() + ", size=" + objectSummary.getSize() + ", owner=" + objectSummary.getOwner().getId());
//                }
//
//                if (objectListing.isTruncated()) {
//                    objectListing = s3.listNextBatchOfObjects(objectListing);
//                } else {
//                    break;
//                }
//            }
//        } catch (AmazonS3Exception e) {
//            System.err.println(e.getErrorMessage());
//            System.exit(1);
//        }
//
//        // top level folders and files in the bucket
//        try {
//            ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
//                    .withBucketName(bucketName)
//                    .withDelimiter("/")
//                    .withMaxKeys(300);
//
//            ObjectListing objectListing = s3.listObjects(listObjectsRequest);
//
//            System.out.println("FolderEntity List:");
//            for (String commonPrefixes : objectListing.getCommonPrefixes()) {
//                System.out.println("    name=" + commonPrefixes);
//            }
//
//            System.out.println("FileEntity List:");
//            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
//                System.out.println("    name=" + objectSummary.getKey() + ", size=" + objectSummary.getSize() + ", owner=" + objectSummary.getOwner().getId());
//            }
//        } catch (AmazonS3Exception e) {
//            e.printStackTrace();
//        } catch(SdkClientException e) {
//            e.printStackTrace();
//        }
//    }
}
