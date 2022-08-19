package io.github.adrianjuhl.simple_springboot_camel_prototype;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.adrianjuhl.simple_springboot_camel_prototype.ApplicationRouteBuilder.RouteIdentifier;

@SpringBootTest(classes=Application.class)
class ApplicationRouteBuilderTest {

  @Autowired
  private CamelContext context;

  @Autowired
  private ProducerTemplate template;

  @Test
  public void pingRouteShouldRespondWithPingJsonResponse() throws Exception {
    MockEndpoint mockOut = getMockEndpoint("mock:out");
    context
      .getRouteDefinition(RouteIdentifier.PING.getRouteId())
      .adviceWith(context, new AdviceWithRouteBuilder() {
      @Override
      public void configure() throws Exception {
        weaveAddLast().to(mockOut.getEndpointUri());
      }
    });
    context.start();
    mockOut.expectedMessageCount(1);
    template.sendBody(RouteIdentifier.PING.getRouteUri(), null);
    debugExchanges(mockOut);
    mockOut.assertIsSatisfied();
    Message inMessage = mockOut.getExchanges().get(0).getIn();
    String actualBodyString = inMessage.getBody(String.class);
    Assert.assertEquals(MediaType.APPLICATION_JSON, inMessage.getHeader(Exchange.CONTENT_TYPE, String.class));
    Assert.assertTrue("Body should start with {", actualBodyString.startsWith("{"));
    Assert.assertTrue("Body should end with }", actualBodyString.endsWith("}"));
    Assert.assertTrue("Body should contain ping element", actualBodyString.contains("\"ping\":"));
  }

  private MockEndpoint getMockEndpoint(String uri) {
    return context.getEndpoint(uri, MockEndpoint.class);
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
