package com.smeo.tools.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	public static List<String> getLogFileNamesFromAllSubdirectories(String searchPath, String searchPattern) {
		CommandExecutor commandExecutor = new CommandExecutor();
		String[] commandArray = { "find",
				searchPath,
				"-name",
				searchPattern };
		commandExecutor.executeCommand(commandArray);
		if (!commandExecutor.isSuccessfullyExecuted()) {
			throw new IllegalArgumentException("Could not retreive file list for searchPath '" + searchPath + "' and searchPattern '" + searchPattern + "' : " +
					commandExecutor.getErrorMessages());
		}

		List<String> filenames = new ArrayList<String>();
		for (String currFoundResult : commandExecutor.getConsoleOutput()) {
			File currFoundResultFile = new File(currFoundResult);
			if (currFoundResultFile.isFile()) {
				filenames.add(currFoundResult);
			}
		}
		return filenames;
	}

	public static boolean copyDirectoryFromTo(String sourceDirectory, String destinationDirectory, boolean createDestinationIfNotExist) {
		if (!directoryExists(destinationDirectory) && createDestinationIfNotExist) {
			createFullDirectoryPath(destinationDirectory);
		}
		CommandExecutor commandExecutor = new CommandExecutor();
		String[] commandArray = { "cp",
				"-r",
				sourceDirectory,
				destinationDirectory };
		commandExecutor.executeCommand(commandArray);
		return commandExecutor.isSuccessfullyExecuted();
	}

	public static boolean copyFileFromTo(String sourceFile, String destinationDirectory, boolean createDestinationIfNotExist) {
		if (!directoryExists(destinationDirectory) && createDestinationIfNotExist) {
			createFullDirectoryPath(destinationDirectory);
		}
		CommandExecutor commandExecutor = new CommandExecutor();
		String[] commandArray = { "cp",
				sourceFile,
				destinationDirectory };
		commandExecutor.executeCommand(commandArray);
		return commandExecutor.isSuccessfullyExecuted();
	}

	public static boolean directoryExists(String destinationDirectory) {
		File directory = new File(destinationDirectory);
		return (directory.exists() && directory.isDirectory());
	}

	public static boolean fileExists(String fileName) {
		File file = new File(fileName);
		return (file.exists() && file.isFile());
	}

	public static void createFullDirectoryPath(String directoryPath) {
		String[] pathNodes = directoryPath.substring(1).split("/");
		StringBuffer currPath = new StringBuffer();

		for (String currPathNode : pathNodes) {
			currPath.append("/");
			currPath.append(currPathNode);
			if (!FileUtils.directoryExists(currPath.toString())) {
				File directory = new File(currPath.toString());
				if (!directory.mkdir()) {
					throw new IllegalArgumentException("could not create directory path node '" + currPathNode
							+ "' in order to create full directory path '" + directoryPath + "'");
				}
			}
		}
	}

	public static String[] listDirectory(String directoryPath) {
		File dir = new File(directoryPath);
		if (dir.exists() && dir.isDirectory()) {
			return dir.list();
		}
		return new String[0];
	}

	static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

}
