package io.github.adrianjuhl.simple_springboot_camel_application_prototype;

import org.apache.camel.component.cxf.jaxrs.CxfRsEndpoint;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@Configuration
public class ApplicationConfiguration {

  @Bean
  JAXRSServerFactoryBean restServer() {
    CxfRsEndpoint endpoint = new CxfRsEndpoint();
    JAXRSServerFactoryBean restServer = endpoint.createJAXRSServerFactoryBean();
    restServer.setServiceClass(RestInterface.class);
    restServer.setProvider(jsonProvider());
    return restServer;
  }

  @Bean
  JacksonJsonProvider jsonProvider(){
    return new JacksonJsonProvider();
  }

}
