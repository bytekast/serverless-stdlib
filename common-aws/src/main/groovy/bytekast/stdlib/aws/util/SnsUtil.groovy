package bytekast.stdlib.aws.util

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder
import com.amazonaws.services.sns.model.PublishRequest
import groovy.json.JsonSlurper
import groovy.transform.CompileDynamic
import groovy.transform.CompileStatic
import groovy.transform.Memoized

@CompileStatic
@Singleton
class SnsUtil {

  @Memoized
  AmazonSNS snsClient(String region = 'us-east-1') {
    AmazonSNSAsyncClientBuilder.standard().withRegion(region).build()
  }

  @Memoized
  AmazonSNS snsClientWithCredentials(String accessKey, String secretKey, String region = 'us-east-1') {
    def credentials = new BasicAWSCredentials(accessKey, secretKey)
    return AmazonSNSAsyncClientBuilder.standard().withRegion(region).withCredentials(new AWSStaticCredentialsProvider(credentials)).build()
  }

  void publish(String topicArn, String msg) {
    if (topicArn && msg) {
      PublishRequest publishRequest = new PublishRequest(topicArn, msg)
      snsClient().publish(publishRequest)
    }
  }

  @CompileDynamic
  Map snsJsonBody(Map input) {
    def jsonString = input?.Records?.first()?.Sns?.Message ?: null
    if (jsonString) {
      return JsonSlurper.newInstance().parseText(jsonString)
    }
    return [:]
  }

  @CompileDynamic
  String snsMessage(Map input) {
    input?.Records?.first()?.Sns?.Message
  }
}
