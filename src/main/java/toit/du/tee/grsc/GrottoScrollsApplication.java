package toit.du.tee.grsc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import toit.du.tee.grsc.jpa.ContentEntity;
import toit.du.tee.grsc.jpa.ContentRepository;

@SpringBootApplication
@ComponentScan(basePackages = {"toit.du.tee.grsc.manager", "toit.du.tee.grsc.rest"})
public class GrottoScrollsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GrottoScrollsApplication.class, args);
	}

//	@Bean
//	public String useless(ContentRepository contentRepository) {
//	   ContentEntity contentEntity = new ContentEntity(0, null);
//	   return "";
//	}
}
