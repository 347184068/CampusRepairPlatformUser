package utils;

import com.google.gson.Gson;

public class GsonCore {
	public static Gson gson;
	static {
		gson = new Gson();
	}
}
