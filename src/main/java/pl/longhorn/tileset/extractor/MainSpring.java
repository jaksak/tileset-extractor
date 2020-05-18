package pl.longhorn.tileset.extractor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@SpringBootApplication
public class MainSpring {

	public static void main(String[] args) {
		SpringApplication.run(MainSpring.class, args);
	}

	@Bean
	public MultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver
				= new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(1048576);
		return multipartResolver;
	}
}
