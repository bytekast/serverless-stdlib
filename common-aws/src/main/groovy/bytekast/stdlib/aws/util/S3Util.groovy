package bytekast.stdlib.aws.util

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.*
import groovy.transform.CompileStatic
import groovy.transform.Memoized

@Singleton
@CompileStatic
class S3Util {

  @Memoized
  AmazonS3 s3Client(String region = 'us-east-1') {
    return AmazonS3ClientBuilder.standard().withRegion(region).build()
  }

  @Memoized
  AmazonS3 s3ClientWithCredentials(String accessKey, String secretKey, String region = 'us-east-1') {
    def credentials = new BasicAWSCredentials(accessKey, secretKey)
    return AmazonS3ClientBuilder.standard().withRegion(region).withCredentials(new AWSStaticCredentialsProvider(credentials)).build()
  }

  List<S3ObjectSummary> listBucketItems(final AmazonS3 s3Client, final String bucketName, final String prefix = "") {
    def items = []
    ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix)
    while (true) {
      def objectListing = s3Client.listObjects(listObjectsRequest)
      items.addAll(objectListing.getObjectSummaries())
      listObjectsRequest.setMarker(objectListing.getNextMarker())
      if (!objectListing.isTruncated()) {
        break
      }
    }
    items
  }

  PutObjectResult putS3Object(final AmazonS3Client s3Client, final String bucketName, final String key, final String content) {
    s3Client.putObject(bucketName, key, content)
  }

  String getS3ObjectContent(final AmazonS3 s3Client, final String bucketName, final String key) {
    S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, key))
    object.getObjectContent()?.text
  }

  void deleteS3Object(final AmazonS3 s3Client, final String bucketName, final String key) {
    s3Client.deleteObject(new DeleteObjectRequest(bucketName, key))
  }
}
