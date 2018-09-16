package bytekast.stdlib.aws.lambda


import groovy.transform.ToString
import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy

@Builder(builderStrategy = SimpleStrategy, prefix = '')
@ToString(includePackage = false, includeFields = true)
class Response {

  int statusCode
  Map headers
  String body

  void addHeader(String key, String value) {
    headers = headers ?: [:]
    headers.put(key, value)
  }
}
