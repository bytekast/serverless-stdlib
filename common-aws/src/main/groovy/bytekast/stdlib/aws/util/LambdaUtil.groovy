package bytekast.stdlib.aws.util

import bytekast.stdlib.aws.lambda.LambdaFunctionNameResolver
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.services.lambda.AWSLambda
import com.amazonaws.services.lambda.AWSLambdaAsync
import com.amazonaws.services.lambda.AWSLambdaAsyncClientBuilder
import com.amazonaws.services.lambda.AWSLambdaClientBuilder
import com.amazonaws.services.lambda.invoke.LambdaInvokerFactory
import com.amazonaws.services.lambda.model.InvocationType
import com.amazonaws.services.lambda.model.InvokeRequest
import com.amazonaws.services.lambda.model.InvokeResult
import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import groovy.transform.Memoized

@CompileStatic
@Singleton
class LambdaUtil {

  @Memoized
  @CompileStatic
  <T> T createProxy(Class<T> type, String functionName) {
    return LambdaInvokerFactory.builder()
        .lambdaClient(AWSLambdaAsyncClientBuilder.standard().withRegion('us-east-1').build())
        .lambdaFunctionNameResolver(new LambdaFunctionNameResolver(functionName))
        .build(type)
  }

  @Memoized
  @CompileStatic
  <T> T createAsyncProxy(Class<T> type, String functionName) {
    return LambdaInvokerFactory.builder()
        .lambdaClient(AWSLambdaAsyncClientBuilder.standard().withRegion('us-east-1').build())
        .lambdaFunctionNameResolver(new LambdaFunctionNameResolver(functionName))
        .build(type)
  }

  @Memoized
  @CompileStatic
  AWSLambdaAsync asyncLambdaClient(String profile = null) {
    if (profile) {
      def profileCredentials = new ProfileCredentialsProvider(profile)
      AWSLambdaAsyncClientBuilder.standard().withCredentials(profileCredentials).withRegion('us-east-1').build()
    } else {
      AWSLambdaAsyncClientBuilder.standard().withRegion('us-east-1').build()
    }
  }

  @Memoized
  AWSLambda lambdaClient(String profile = null) {
    if (profile) {
      def profileCredentials = new ProfileCredentialsProvider(profile)
      AWSLambdaClientBuilder.standard().withCredentials(profileCredentials).withRegion('us-east-1').build()
    } else {
      AWSLambdaClientBuilder.standard().withRegion('us-east-1').build()
    }
  }

  @CompileStatic
  InvokeResult invokeAsync(String functionName, byte[] input) {
    invokeAsync(asyncLambdaClient(), functionName, input)
  }

  @CompileStatic
  InvokeResult invoke(String functionName, byte[] input) {
    invoke(lambdaClient(), functionName, input)
  }

  @CompileStatic
  InvokeResult invokeAsync(AWSLambdaAsync client, String functionName, byte[] input) {
    def encoded = Base64.getEncoder().encodeToString(input)
    def jsonRequest = JsonOutput.toJson([body: encoded])
    client.invoke(new InvokeRequest()
        .withFunctionName(functionName)
        .withInvocationType(InvocationType.Event)
        .withPayload(jsonRequest))
  }

  @CompileStatic
  InvokeResult invoke(AWSLambda client, String functionName, byte[] input) {
    def encoded = Base64.getEncoder().encodeToString(input)
    def jsonRequest = JsonOutput.toJson([body: encoded])
    client.invoke(new InvokeRequest()
        .withFunctionName(functionName)
        .withInvocationType(InvocationType.RequestResponse)
        .withPayload(jsonRequest))
  }
}
