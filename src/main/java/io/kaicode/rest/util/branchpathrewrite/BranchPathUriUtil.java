package io.kaicode.rest.util.branchpathrewrite;

public class BranchPathUriUtil {

	public static String parseBranchPath(String branch) {
		return branch.replace("|", "/");
	}

}
