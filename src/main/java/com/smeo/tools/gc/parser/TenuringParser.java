package com.smeo.tools.gc.parser;

import com.smeo.tools.gc.domain.Tenuring;

public class TenuringParser {
	Tenuring tenuring;

	public Tenuring parse(String currLine) {
		if (currLine.startsWith("Desired survivor size ")) {
			tenuring = new Tenuring();
			String[] elements = currLine.split("\\(max|\\)");
			tenuring.max = Integer.valueOf(elements[1].trim());
		} else {
			if (tenuring != null) {
				if (currLine.startsWith("- age")) {
					String[] elements = currLine.split("- age|:|bytes");
					tenuring.ages.add(Integer.valueOf(elements[2].trim()));
				} else if (tenuring != null) {
					Tenuring value = tenuring;
					tenuring = null;
					return value;
				}
			}
		}
		return null;
	}
}
