package bytekast.stdlib.aws.lambda

@Singleton
class LambdaRequest {

  private static final InheritableThreadLocal<RequestContext> CURRENT = new InheritableThreadLocal<RequestContext>()

  void set(RequestContext requestContext) {
    CURRENT.set(requestContext)
  }

  void remove() {
    CURRENT.remove()
  }

  RequestContext get() {
    CURRENT.get()
  }
}
