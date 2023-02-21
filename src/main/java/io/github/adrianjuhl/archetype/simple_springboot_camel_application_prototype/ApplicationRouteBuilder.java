package io.github.adrianjuhl.archetype.simple_springboot_camel_application_prototype;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class ApplicationRouteBuilder extends RouteBuilder {

  @Autowired
  private CamelContext camelContext;

  @Value("${camel.context.shutdown.timeout}")
  private Long camelContextShutdownTimeout;

  @Value("${appVersionInfoConfigProperty:notsupplied}")
  private String appVersionInfoConfigProperty;

  @Value("${helloTimerPeriodMilliseconds:5000}")
  private Long helloTimerPeriodMilliseconds;

  enum RouteIdentifier {
    PING                                        ("direct:handleRequestPing"),
    APP_VERSION_INFO                            ("direct:handleRequestAppVersionInfo"),
    ;
    private final String routeUri;
    private final String routeId;
    RouteIdentifier(final String routeUri) {
      this.routeUri = routeUri;
      this.routeId = ApplicationRouteBuilder.class.getSimpleName() + "_" + this.name();
    }
    public String getRouteId() {
      return routeId;
    }
    public String getRouteUri() {
      return routeUri;
    }
  }

  public String loggerName() {
    return this.getClass().getCanonicalName();
  }

  @Override
  public void configure() throws Exception {

    System.out.println("start of configure() - appVersionInfoConfigProperty is ~~~" + appVersionInfoConfigProperty + "~~~");

    // The source of AppVersionInfo is within src/main/java-templates/
    Class<?> appVersionInfoClass = Class.forName(ApplicationRouteBuilder.class.getPackageName() + ".AppVersionInfo");
    Object appVersionInfo = appVersionInfoClass.getDeclaredConstructor(String.class).newInstance(appVersionInfoConfigProperty);
    Object appVersionInfoJson = appVersionInfoClass.getMethod("toJsonString").invoke(appVersionInfo);

    errorHandler(noErrorHandler());
    camelContext.getShutdownStrategy().setTimeout(camelContextShutdownTimeout);

//    from("cxfrs:bean:restServer?bindingStyle=SimpleConsumer")
    from("cxfrs:bean:restServer")
      .log(LoggingLevel.TRACE, loggerName(), "Start of route cxfrs:bean:restServer")
      .doTry()
        .toD("direct:${header.operationName}")
      .endDoTry()
      .doCatch(Exception.class)
        .to("direct:handleUncaughtException")
      .end()
      .log(LoggingLevel.TRACE, loggerName(), "End of route cxfrs:bean:restServer")
    ;

    from("direct:handleUncaughtException")
      .log(LoggingLevel.TRACE, loggerName(), "Start of route direct:handleUncaughtException")
      .log(LoggingLevel.ERROR, loggerName(), "An uncaught exception occurred:")
      .toF("log:%s?level=ERROR&showBody=false&showCaughtException=true&showStackTrace=true", loggerName())
      .removeHeaders("*")
      .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.INTERNAL_SERVER_ERROR.value()))
      .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
      .setBody(constant("{\"error\":\"An unexpected error occurred. This error has been logged and will be investigated.\"}"))
      .log(LoggingLevel.TRACE, loggerName(), "End of route direct:handleUncaughtException")
    ;

    from(RouteIdentifier.PING.getRouteUri())
      .routeId(RouteIdentifier.PING.getRouteId())
      .log(LoggingLevel.TRACE, loggerName(), "Start of route " + RouteIdentifier.PING.getRouteUri())
      .removeHeaders("*")
      .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.OK.value()))
      .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
      .setBody(constant("{\"ping\":\"ping_response\"}"))
       .log(LoggingLevel.TRACE, loggerName(), "End of route " + RouteIdentifier.PING.getRouteUri())
    ;

    from(RouteIdentifier.APP_VERSION_INFO.getRouteUri())
      .routeId(RouteIdentifier.APP_VERSION_INFO.getRouteId())
      .log(LoggingLevel.TRACE, loggerName(), "Start of route " + RouteIdentifier.APP_VERSION_INFO.getRouteUri())
      .removeHeaders("*")
      .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(HttpStatus.OK.value()))
      .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON))
      .setBody(constant(appVersionInfoJson))
//      .setBody(constant(appVersionInfoClass.getMethod("toJsonString").invoke(appVersionInfo)))
//      .setBody(constant(appVersionInfoClass.getMethod("toJsonString").invoke(appVersionInfoClass.getDeclaredConstructor(String.class).newInstance(appVersionInfoConfigProperty))))
//      .setBody(constant(new AppVersionInfo().toJsonString())) // The source of AppVersionInfo is in src/main/java-templates/ 
      .log(LoggingLevel.TRACE, loggerName(), "End of route " + RouteIdentifier.APP_VERSION_INFO.getRouteUri())
    ;

    from("timer:hello?period=" + helloTimerPeriodMilliseconds)
      .routeId("hello")
      // and call the bean
      //.bean(myBean, "saySomething")
      .setBody(constant("hello"))
      // and print it to system out via stream component
      .to("stream:out")
    ;

  }

}
