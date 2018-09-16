package bytekast.stdlib.aws.util

import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.MessageAttributeValue
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.amazonaws.services.sqs.model.SendMessageRequest
import groovy.transform.CompileStatic
import groovy.transform.Memoized

@CompileStatic
@Singleton
class SqsUtil {

  @Memoized
  AmazonSQS sqsClient(String region = 'us-east-1') {
    return AmazonSQSClientBuilder.standard().withRegion(region).build()
  }

  @Memoized
  AmazonSQS sqsClientWithProfile(String profile, String region = 'us-east-1') {
    return AmazonSQSClientBuilder.standard().withRegion(region).withCredentials(new ProfileCredentialsProvider(profile)).build()
  }

  @Memoized
  AmazonSQS sqsClientWithCredentials(String accessKey, String secretKey, String region = 'us-east-1') {
    def credentials = new BasicAWSCredentials(accessKey, secretKey)
    return AmazonSQSClientBuilder.standard().withRegion(region).withCredentials(new AWSStaticCredentialsProvider(credentials)).build()
  }

  void sendSQSMessage(String queueUrl, String msg, Map<String, MessageAttributeValue> msgAttributes = null) {
    sendSQSMessage(sqsClient(), queueUrl, msg, msgAttributes)
  }

  void sendSQSMessage(AmazonSQS client, String queueUrl, String msg, Map<String, MessageAttributeValue> msgAttributes = null) {
    def request = new SendMessageRequest(queueUrl, msg)
    request.withMessageAttributes(msgAttributes)
    client.sendMessage(request)
  }

  List<Message> getSQSMessages(String queueUrl, String... messageAttributeNames) {
    getSQSMessages(sqsClient(), queueUrl, messageAttributeNames)
  }

  List<Message> getSQSMessages(AmazonSQS client, String queueUrl, String... messageAttributeNames) {
    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl)
    receiveMessageRequest.setMaxNumberOfMessages(10)
    receiveMessageRequest.withAttributeNames("ApproximateReceiveCount")
    receiveMessageRequest.withMessageAttributeNames(messageAttributeNames)
    client.receiveMessage(receiveMessageRequest).getMessages()
  }

  void deleteSQSMessage(String queueUrl, String receiptHandle) {
    deleteSQSMessage(sqsClient(), queueUrl, receiptHandle)
  }

  void deleteSQSMessage(AmazonSQS client, String queueUrl, String receiptHandle) {
    client.deleteMessage(new DeleteMessageRequest(queueUrl, receiptHandle))
  }

  MessageAttributeValue attributeValue(String dataType, String value) {
    MessageAttributeValue.newInstance()
        .withDataType(dataType)
        .withStringValue(value)
  }

  Map<String, MessageAttributeValue> attributeMap(Map<String, String> values) {
    values?.collectEntries { k, v -> [k, attributeValue('String', v)] } as Map<String, MessageAttributeValue>
  }
}
