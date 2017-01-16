package io.kaicode.rest.util.branchpathrewrite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestApplication {

	@Bean
	public FilterRegistrationBean getUrlRewriteFilter() {
		// Encode branch paths in uri to allow request mapping to work
		return new FilterRegistrationBean(new BranchPathUriRewriteFilter(
				"/(.*)/books",
				"/(.*)/kites"
		));
	}

	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
	}
}
