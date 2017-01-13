package io.kaicode.rest.util.branchpathrewrite;

import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@RequestMapping("/{branch}/books")
	public String books(@PathVariable String branch) {
		// branch value comes in with '|' pipe separators
		// swap back to '/' slash
		branch = ControllerHelper.parseBranchPath(branch);

		return "Books on path " + branch;
	}

	@RequestMapping("/{branch}/kites")
	public String kites(@PathVariable String branch) {
		branch = ControllerHelper.parseBranchPath(branch);
		return "Kites on path " + branch;
	}

	public static void main(String[] args) {
		SpringApplication.run(TestController.class, args);
	}

}
