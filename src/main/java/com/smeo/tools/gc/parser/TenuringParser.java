package com.smeo.tools.gc.parser;

import com.smeo.tools.gc.domain.Tenuring;

public class TenuringParser {
	Tenuring tenuring;

	public Tenuring parse(String currLine) {
		if (currLine.startsWith("Desired survivor size ")) {
			tenuring = new Tenuring();
			String[] elements = currLine.split("size|bytes|threshold|max|\\(|\\)");
            tenuring.desiredSurvivorSpace = Integer.valueOf(elements[1].trim());
            tenuring.newThreshold = Integer.valueOf(elements[3].trim());
			tenuring.max = Integer.valueOf(elements[5].trim());
		} else {
			if (tenuring != null) {
				if (currLine.startsWith("- age")) {
					String[] elements = currLine.split("- age|:|bytes|total|,");
					tenuring.usedSpace[Integer.valueOf(elements[1].trim())-1] = Integer.valueOf(elements[2].trim());
                    tenuring.totalSpace[Integer.valueOf(elements[1].trim())-1] = Integer.valueOf(elements[4].trim());
				} else if (tenuring != null) {
					Tenuring value = tenuring;
					tenuring = null;
					return value;
				}
			}
		}
		return tenuring;
	}
}
