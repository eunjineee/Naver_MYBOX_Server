package back.server.mybox.Domain.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;


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



    public String createFolder(String folderName) {
        final AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
        amazonS3.putObject(bucketName, folderName + "/", new ByteArrayInputStream(new byte[0]), new ObjectMetadata());
        return folderName;
    }
    public void deleteFolder(String folderName) {
        final AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
        String replaceFolderName = (folderName + "/").replace(File.separatorChar, '/');
        amazonS3.deleteObject(bucketName, replaceFolderName);
    }
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
//            System.out.println("Folder List:");
//            for (String commonPrefixes : objectListing.getCommonPrefixes()) {
//                System.out.println("    name=" + commonPrefixes);
//            }
//
//            System.out.println("File List:");
//            for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
//                System.out.println("    name=" + objectSummary.getKey() + ", size=" + objectSummary.getSize() + ", owner=" + objectSummary.getOwner().getId());
//            }
//        } catch (AmazonS3Exception e) {
//            e.printStackTrace();
//        } catch(SdkClientException e) {
//            e.printStackTrace();
//        }
//    }

    public String uploadFile(MultipartFile multipartFile) throws IOException {
        final AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();

        String originalFilename = multipartFile.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucketName, originalFilename, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucketName, originalFilename).toString();
    }

    public void deleteFile(String fileName) {
        final AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();
        String replaceFileName = (fileName).replace(File.separatorChar, '/');
        amazonS3.deleteObject(bucketName, replaceFileName);
    }

    public ResponseEntity<byte[]> downloadFile(String fileUrl) throws IOException {
        final AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endPoint, regionName))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();

        S3Object s30bject = amazonS3.getObject(new GetObjectRequest(bucketName, fileUrl));
        S3ObjectInputStream objectInputStream = s30bject.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(contentType(fileUrl));
        httpHeaders.setContentLength(bytes.length);

        String[] arr = fileUrl.split("/");
        String type = arr[arr.length - 1];
        String fileName = URLEncoder.encode(type, "UTF-8").replaceAll("\\+", "%20");
        httpHeaders.setContentDispositionFormData("attachment", fileName);
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
}
