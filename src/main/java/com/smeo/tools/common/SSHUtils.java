package com.smeo.tools.common;

import java.util.List;

public class SSHUtils {

	public static List<String> remoteLs(String host, String user, String directory) {
		String command = "ssh " + user + "@" + host + " ls " + directory;
		CommandExecutor commandExecutor = new CommandExecutor();
		commandExecutor.executeCommand(command);
		if (commandExecutor.isSuccessfullyExecuted()) {
			return commandExecutor.getConsoleOutput();
		} else {
			throw new IllegalArgumentException("remoteLs failed - " + commandExecutor.getErrorMessages());
		}
	}

	public static void scpDirectoryFromTo(String host, String user,
			String directory, String localDestDirectory) {
		String command = "scp -r " + user + "@" + host + ":" + directory + " " + localDestDirectory;
		CommandExecutor commandExecutor = new CommandExecutor();
		commandExecutor.executeCommand(command);
		if (!commandExecutor.isSuccessfullyExecuted()) {
			throw new IllegalArgumentException("remoteLs failed - " + commandExecutor.getErrorMessages());
		}
	}

	public static boolean scpFileFromTo(String host, String user, String filename, String localDestDirectory) {
		String command = "scp " + user + "@" + host + ":" + filename + " " + localDestDirectory;
		CommandExecutor commandExecutor = new CommandExecutor();
		commandExecutor.executeCommand(command);
		return commandExecutor.isSuccessfullyExecuted();
	}

	public static void main(String[] args) {
		System.out.println(remoteLs("eloghost01", "truehl", "/logfiles/clients/prod/"));
		// scpDirectoryFromTo("eloghost01", "truehl",
		// "/logfiles/clients/prod/*SepPs", "/tmp");
	}

}
