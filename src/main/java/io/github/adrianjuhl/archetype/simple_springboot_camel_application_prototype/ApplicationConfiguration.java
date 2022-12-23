package io.github.adrianjuhl.archetype.simple_springboot_camel_application_prototype;

import org.apache.camel.CamelContext;
import org.apache.camel.component.cxf.jaxrs.CxfRsEndpoint;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@Configuration
public class ApplicationConfiguration {

//  @Autowired
//  CamelContext camelContext;

  @Bean
  JAXRSServerFactoryBean restServer() {
    CxfRsEndpoint endpoint = new CxfRsEndpoint();
    JAXRSServerFactoryBean restServer = endpoint.createJAXRSServerFactoryBean();
    restServer.setServiceClass(RestInterface.class);
    restServer.setProvider(new JacksonJsonProvider());
    return restServer;
  }

}
