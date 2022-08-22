package io.github.adrianjuhl.archetype.simple_springboot_camel_application_prototype;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public interface RestInterface {

  @Path("ping")
  @GET
  @Produces({MediaType.APPLICATION_JSON})
  public void handleRequestPing();

}
