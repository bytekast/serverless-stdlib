package bytekast.stdlib.aws.lambda

import com.amazonaws.services.lambda.runtime.Context
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy


@Builder(builderStrategy = SimpleStrategy, prefix = '')
@ToString(includePackage = false, includeFields = true)
class RequestContext {

  final Map input
  final Context context

  String requestId() {
    context?.awsRequestId
  }

  String resourcePath() {
    input?.resource ?: 'unknown'
  }

  String httpMethod() {
    input?.httpMethod ?: 'unknown'
  }

  String queryString(String name) {
    input?.queryStringParameters?."${name}"?.trim()
  }

  String pathParameter(String name) {
    input?.pathParameters?."${name}"?.trim()
  }

  Map claims() {
    input?.requestContext?.authorizer
  }

  String httpRoute() {
    "${httpMethod()}:${resourcePath()}"
  }

  String rawJsonInput() {
    JsonOutput.toJson(input)
  }

  String httpBody() {
    input?.body
  }

  /**
   * @return List or Map
   */
  def parsedHttpJsonBody() {
    JsonSlurper.newInstance().parseText(httpBody())
  }
}