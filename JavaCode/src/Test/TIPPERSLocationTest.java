package Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import MAIN.Location;

public class TIPPERSLocationTest {
	public static void main(String args[]) throws JSONException, MalformedURLException, IOException, ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String basic = "http://sensoria.ics.uci.edu:8001/semanticobservation/get?requestor_id=primal@uci.edu&service_id=1";
		String userName = "subject_id=dhrubagh08@gmail.com";
		String type = "type=1";
		String startTime = "start_timestamp="
				+ format.format(format.parse("2017-06-04 12:22:23")).replaceAll(" ", "%20");
		// System.out.println(startTime);
		String url = basic + "&" + userName + "&" + type + "&" + startTime;
		System.out.println(url);
		InputStream is = new URL(url).openStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		String response = rd.readLine();
		JSONArray arr = new JSONArray(response);
		HashSet<Location> mlist = new HashSet<Location>();
		for (int i = 0; i < arr.length(); i++) {
			JSONObject obj = (JSONObject) arr.get(i);
			obj = new JSONObject(obj.get("payload").toString());
			mlist.add(new Location("DBH " + obj.get("location")));
		}

		for (Location loc : mlist)
			System.out.println(loc.toString());
	}
}
