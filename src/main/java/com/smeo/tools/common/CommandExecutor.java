package com.smeo.tools.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Executing a command on the commandline and giving access to the console output.
 */
public class CommandExecutor {
	private List<String> errorMessages = new ArrayList<String>();
	private List<String> consoleOutput = new ArrayList<String>();

	public void executeCommand(String[] commandArray) {
		Process process;
		try {
			process = Runtime.getRuntime().exec(commandArray);
			fillConsolesFromProcess(process);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void executeCommand(String command) {
		Process process;
		try {
			process = Runtime.getRuntime().exec(command);
			fillConsolesFromProcess(process);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void fillConsolesFromProcess(Process process) {
		try {
			BufferedReader is = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String buf = "";

			try {
				while ((buf = is.readLine()) != null) {
					consoleOutput.add(buf);
				}
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			is = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			while ((buf = is.readLine()) != null) {
				errorMessages.add(buf);
			}
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isSuccessfullyExecuted() {
		return (errorMessages.size() == 0);
	}

	public String getErrorMessages() {
		StringBuffer fullMessage = new StringBuffer();
		for (String currLine : errorMessages) {
			fullMessage.append(currLine);
			fullMessage.append("\n");
		}
		return fullMessage.toString();
	}

	public List<String> getConsoleOutput() {
		return consoleOutput;
	}
}
