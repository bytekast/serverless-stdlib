package bytekast.stdlib.aws.lambda

import com.amazonaws.services.lambda.invoke.LambdaFunction
import com.amazonaws.services.lambda.invoke.LambdaInvokerFactoryConfig
import groovy.transform.Canonical
import groovy.transform.Immutable

import java.lang.reflect.Method

@Canonical
@Immutable
class LambdaFunctionNameResolver implements com.amazonaws.services.lambda.invoke.LambdaFunctionNameResolver {

  final String function

  @Override
  String getFunctionName(Method method, LambdaFunction annotation, LambdaInvokerFactoryConfig config) {
    return function
  }
}
