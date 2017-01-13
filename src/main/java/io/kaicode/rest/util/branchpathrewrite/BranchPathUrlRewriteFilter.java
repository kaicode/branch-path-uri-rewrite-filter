package io.kaicode.rest.util.branchpathrewrite;

import org.apache.catalina.comet.CometEvent;
import org.apache.catalina.comet.CometFilterChain;
import org.apache.catalina.filters.RequestFilter;
import org.apache.juli.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BranchPathUrlRewriteFilter extends RequestFilter {

	public static final String ORIGINAL_BRANCH_PATH_URI = "originalBranchPathURI";
	private final List<Pattern> patterns;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public BranchPathUrlRewriteFilter(String... patternStrings) {
		patterns = new ArrayList<>();
		for (String pattern : patternStrings) {
			patterns.add(Pattern.compile(pattern));
		}
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		if (servletRequest.getAttribute(ORIGINAL_BRANCH_PATH_URI) == null) {
			String originalRequestURI = request.getRequestURI();
			final String rewrittenRequestURI = rewriteUri(originalRequestURI);
			if (rewrittenRequestURI != null) {
				servletRequest = new HttpServletRequestWrapper(request) {
					@Override
					public String getRequestURI() {
						return rewrittenRequestURI;
					}
				};
				servletRequest.setAttribute(ORIGINAL_BRANCH_PATH_URI, originalRequestURI);
				servletRequest.getRequestDispatcher(rewrittenRequestURI).forward(servletRequest, servletResponse);
				return;
			}
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	private String rewriteUri(String requestURI) {
		if (requestURI != null) {
			for (Pattern pattern : patterns) {
				final Matcher matcher = pattern.matcher(requestURI);
				if (matcher.matches()) {
					final String path = matcher.group(1);
					String rewrittenURI = requestURI.replace(path, path.replace("/", "|").replace("%2F", "|"));
					logger.debug("Request URI '{}' matches pattern '{}', rewritten URI '{}'", requestURI, pattern, rewrittenURI);
					return rewrittenURI;
				}
			}
		}
		return null;
	}

	@Override
	public void doFilterEvent(CometEvent cometEvent, CometFilterChain cometFilterChain) throws IOException, ServletException {
	}

	@Override
	protected Log getLogger() {
		return null;
	}
}
