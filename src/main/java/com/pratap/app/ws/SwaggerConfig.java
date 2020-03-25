package com.pratap.app.ws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.client.LinkDiscoverer;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.hateoas.mediatype.collectionjson.CollectionJsonLinkDiscoverer;
import org.springframework.plugin.core.SimplePluginRegistry;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

	/**
	 * APPLICATION FAILED TO START : Spring boot 2.2.x Spring HateOas startup issue
	 * <p>Parameter 0 of method linkDiscoverers in org.springframework.hateoas.config.HateoasConfiguration 
	 * required a single bean, but 17 were found</p>
	 */
	@Bean
    public LinkDiscoverers discoverers() {
        List<LinkDiscoverer> plugins = new ArrayList<>();
        plugins.add(new CollectionJsonLinkDiscoverer());
        return new LinkDiscoverers(SimplePluginRegistry.create(plugins));

    }
	
	
	Contact contact = new Contact(
            "Pratap Narayan",
            "http://www.appsdeveloperblog.com", 
            "narayanpratap86@gmail.com"
    );
    
    List<VendorExtension> vendorExtensions = new ArrayList<>();
	
	ApiInfo apiInfo = new ApiInfo(
			"Photo app RESTful Web Service documentation",
			"This pages documents Photo app RESTful Web Service endpoints",
			"1.0",
			"http://www.appsdeveloperblog.com/service.html",
			contact, 
			"Apache 2.0",
			"http://www.apache.org/licenses/LICENSE-2.0", 
			vendorExtensions);
	
	@Bean
	public Docket apiDocket() {

		Docket docket = new Docket(DocumentationType.SWAGGER_2)
				.produces(new HashSet<>(Arrays.asList("HTTP", "HTTPS")))
				.apiInfo(apiInfo)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.pratap.app.ws"))
				.paths(PathSelectors.any()).build();

		return docket;

	}
}
