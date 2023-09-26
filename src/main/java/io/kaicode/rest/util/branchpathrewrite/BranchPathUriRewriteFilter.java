package io.kaicode.rest.util.branchpathrewrite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BranchPathUriRewriteFilter implements Filter {

	public static final String ORIGINAL_BRANCH_PATH_URI = "originalBranchPathURI";
	private final List<Pattern> patterns;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	public BranchPathUriRewriteFilter(String... patternStrings) {
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
			String contextPath = request.getContextPath();
			if (contextPath != null) {
				originalRequestURI = originalRequestURI.substring(contextPath.length());
			}
			final String rewrittenRequestURI = rewriteUri(originalRequestURI);
			if (rewrittenRequestURI != null) {
				servletRequest = new HttpServletRequestWrapper(request) {
					@Override
					public String getRequestURI() {
						return rewrittenRequestURI;
					}

					// Fix issue when upgrading to Spring boot 2.7 with spring web 5.3.20
					// DefaultRequestPath class has added validateContextPath method.
					@Override
					public String getContextPath() { return "/";}
				};
				servletRequest.setAttribute(ORIGINAL_BRANCH_PATH_URI, originalRequestURI);
				servletRequest.getRequestDispatcher(rewrittenRequestURI).forward(servletRequest, servletResponse);
				return;
			}
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {

	}

	private String rewriteUri(String requestURI) {
		if (requestURI != null) {
			for (Pattern pattern : patterns) {
				final Matcher matcher = pattern.matcher(requestURI);
				if (matcher.matches()) {
					final String path = matcher.group(1);
					String rewrittenURI = requestURI.replace(path, BranchPathUriUtil.encodePath(path));
					logger.debug("Request URI '{}' matches pattern '{}', rewritten URI '{}'", requestURI, pattern, rewrittenURI);
					return rewrittenURI;
				}
			}
		}
		return null;
	}

}
