package utils;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import bean.RepaireTable;
import bean.Repaireman;

public class GsonUtils extends GsonCore {
	public static <T> T fromJson(String json, Class<T> classOfT) {
		return gson.fromJson(json, classOfT);
	}

	public static String toGson(Object jsonElement) {
		return gson.toJson(jsonElement);
	}

	public static ArrayList<Repaireman> toList(String jsonData){
		ArrayList<Repaireman> list= new ArrayList<Repaireman>();
		Type type = new TypeToken<List<Repaireman>>() {}.getType();
		ArrayList<Repaireman> repairemans = gson.fromJson(jsonData,type);
		for(Repaireman repaireman :repairemans){
			list.add(repaireman);
		}

		return list;
	}
	public static ArrayList<RepaireTable> toRepaireTableList(String jsonData){
		ArrayList<RepaireTable> list= new ArrayList<RepaireTable>();
		Type type = new TypeToken<List<RepaireTable>>() {}.getType();
		ArrayList<RepaireTable> repaireTables = gson.fromJson(jsonData,type);
		for(RepaireTable repaireTable :repaireTables){
			list.add(repaireTable);
		}

		return list;
	}
}
