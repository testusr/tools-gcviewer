package com.smeo.tools.gc.domain;

import java.util.ArrayList;
import java.util.List;

public class Tenuring {
	public int max;
	public List<Integer> ages = new ArrayList<Integer>();

	public Integer getYoungGenPromotion() {
		if (ages.size() > 0) {
			return ages.get(0);
		}
		return null;
	}

	public Integer getOldGenPromotion() {
		if (ages.size() == max) {
			return ages.get(max - 1);
		}
		return null;
	}

	@Override
	public String toString() {
		return "Tenuring [max=" + max + ", ages=" + ages + "]";
	}
}
