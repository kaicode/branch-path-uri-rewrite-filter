package io.kaicode.rest.util.branchpathrewrite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(
		webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
		classes = TestApplication.class
)
public class BranchPathUriRewriteFilterTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void exampleTest() {
		String booksBody = this.restTemplate.getForObject("/projectA/task1/books", String.class);
		assertEquals("Books on path projectA/task1", booksBody);

		String kitesBody = this.restTemplate.getForObject("/projectA/task1/kites", String.class);
		assertEquals("Kites on path projectA/task1", kitesBody);
	}
}
