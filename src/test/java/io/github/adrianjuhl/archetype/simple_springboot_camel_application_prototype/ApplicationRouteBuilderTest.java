package io.github.adrianjuhl.archetype.simple_springboot_camel_application_prototype;

import java.util.List;
import java.util.Map;

//import javax.ws.rs.core.MediaType;
import org.springframework.http.MediaType;

//import org.apache.camel.CamelContext;
//import org.apache.camel.Exchange;
//import org.apache.camel.Message;
//import org.apache.camel.ProducerTemplate;
//import org.apache.camel.builder.AdviceWith;
//import org.apache.camel.builder.AdviceWithRouteBuilder;
//import org.apache.camel.component.mock.MockEndpoint;
//import org.apache.camel.model.ModelCamelContext;
//import org.apache.camel.reifier.RouteReifier;
////import org.junit.Assert;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
//import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import io.github.adrianjuhl.archetype.simple_springboot_camel_application_prototype.ApplicationRouteBuilder.RouteIdentifier;

@DirtiesContext(classMode=DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest(classes=Application.class)
@TestPropertySource(properties = {
    "camel.context.shutdown.timeout=10"
})
class ApplicationRouteBuilderTest {

  @Autowired
  private CamelContext camelContext;

  @Autowired
  private ProducerTemplate template;

  @Test
  public void pingRouteShouldRespondWithPingJsonResponse() throws Exception {
    MockEndpoint mockOut = getMockEndpoint("mock:out");
    AdviceWith.adviceWith(camelContext, RouteIdentifier.PING.getRouteId(), a -> {
      a.weaveAddLast().to(mockOut.getEndpointUri());
    });
    camelContext.start();
    mockOut.expectedMessageCount(1);
    template.sendBody(RouteIdentifier.PING.getRouteUri(), null);
    debugExchanges(mockOut);
    mockOut.assertIsSatisfied();
    Message inMessage = mockOut.getExchanges().get(0).getIn();
    String actualBodyString = inMessage.getBody(String.class);
    Assertions.assertEquals(MediaType.APPLICATION_JSON.toString(), inMessage.getHeader(Exchange.CONTENT_TYPE, String.class), "The value of the Content-Type header should be: " + MediaType.APPLICATION_JSON);
    Assertions.assertTrue(actualBodyString.startsWith("{"), "Body should start with {");
    Assertions.assertTrue(actualBodyString.endsWith("}"), "Body should end with }");
    Assertions.assertTrue(actualBodyString.contains("\"ping\":"), "Body should contain ping element");
  }

  /**
   * GET appVersionInfo response should be an appVersionInfo JSON structure.
   *
   * @throws Exception
   */
  @Test
  public void appVersionInfoRouteShouldRespondWithAppVersionInfoMessage() throws Exception {
    MockEndpoint mockOut = getMockEndpoint("mock:out");
    AdviceWith.adviceWith(camelContext, RouteIdentifier.APP_VERSION_INFO.getRouteId(), a -> {
      a.weaveAddLast().to(mockOut.getEndpointUri());
    });
    camelContext.start();
    mockOut.expectedMessageCount(1);
    template.sendBody(RouteIdentifier.APP_VERSION_INFO.getRouteUri(), null);
    debugExchanges(mockOut);
    mockOut.assertIsSatisfied();
    Message inMessage = mockOut.getExchanges().get(0).getIn();
    String actualBodyString = inMessage.getBody(String.class);
    Assertions.assertEquals("application/json", inMessage.getHeader(Exchange.CONTENT_TYPE, String.class), "The value of the Content-Type header should be: application/json");
    Assertions.assertTrue(actualBodyString.startsWith("{"), "Body should start with {");
    Assertions.assertTrue(actualBodyString.endsWith("}"), "Body should end with }");
    Assertions.assertTrue(actualBodyString.contains("\"appVersionInfo\":"), "Body should contain appVersionInfo element");
    Assertions.assertTrue(actualBodyString.contains("\"projectGroupId\":"), "Body should contain projectGroupId element");
    Assertions.assertTrue(actualBodyString.contains("\"projectArtifactId\":"), "Body should contain projectArtifactId element");
    Assertions.assertTrue(actualBodyString.contains("\"projectVersion\":"), "Body should contain projectVersion element");
    Assertions.assertTrue(actualBodyString.contains("\"gitCommitHash\":"), "Body should contain gitCommitHash element");
    Assertions.assertTrue(actualBodyString.contains("\"gitCommitDatetime\":"), "Body should contain gitCommitDatetime element");
    Assertions.assertTrue(actualBodyString.contains("\"gitCommitTags\":"), "Body should contain gitCommitTags element");
    Assertions.assertTrue(actualBodyString.contains("\"gitCommitBranch\":"), "Body should contain gitCommitBranch element");
    Assertions.assertTrue(actualBodyString.contains("\"mvnBuildDatetime\":"), "Body should contain mvnBuildDatetime element");
  }

  private MockEndpoint getMockEndpoint(String uri) {
    return camelContext.getEndpoint(uri, MockEndpoint.class);
  }

  private void debugExchanges(MockEndpoint mockEndpoint) {
    List<Exchange> exchangesReceived = mockEndpoint.getExchanges();
    for(Exchange exchange : exchangesReceived) {
      Object body = exchange.getIn().getBody();
      Map<String,Object> inHeaders = exchange.getIn().getHeaders();
      System.out.println(">>>>[debugExchanges] exchange: " + exchange);
      System.out.println(">>>>[debugExchanges] exchange body (class " + body.getClass() + "): " + body);
      System.out.println(">>>>[debugExchanges] exchange in headers: " + inHeaders);
    }
  }

}
