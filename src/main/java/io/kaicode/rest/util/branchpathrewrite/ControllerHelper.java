package io.kaicode.rest.util.branchpathrewrite;

public class ControllerHelper {

	public static String parseBranchPath(String branch) {
		return branch.replace("|", "/");
	}

}
