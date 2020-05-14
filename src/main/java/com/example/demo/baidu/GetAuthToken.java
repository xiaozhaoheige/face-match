package com.example.demo.baidu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

@RestController
@RequestMapping(value = "/auth")
public class GetAuthToken {

	// 设置APPID/AK/SK
	@Value(value = "${baidu.appID}")
	public String APP_ID;

	@Value(value = "${baidu.apiKey}")
	public String API_KEY;

	@Value(value = "${baidu.secretKey}")
	public String SECRET_KEY;

	@RequestMapping(value = "/token")
	public String getAuth() {

		String clientId = API_KEY;// 官网获取的 API Key 更新为你注册的
		String clientSecret = SECRET_KEY;// 官网获取的 Secret Key 更新为你注册的
		return getAuth(clientId, clientSecret);
	}

	private String getAuth(String ak, String sk) {
		// 获取token地址
		String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
		String getAccessTokenUrl = authHost

				+ "grant_type=client_credentials" // 1. grant_type为固定参数
				+ "&client_id=" + ak // 2. 官网获取的 API Key
				+ "&client_secret=" + sk; // 3. 官网获取的 Secret Key
		try {
			URL realUrl = new URL(getAccessTokenUrl);
			// 打开和URL之间的连接
			HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			// 获取所有响应头字段
			Map<String, List<String>> map = connection.getHeaderFields();
			// 遍历所有的响应头字段
			for (String key : map.keySet()) {
				System.err.println(key + "--->" + map.get(key));
			}
			// 定义 BufferedReader输入流来读取URL的响应
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String result = "";
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			/**
			 * 返回结果示例
			 */
			System.err.println("result:" + result);

			JSONObject jsonObject = JSONObject.parseObject(result);
			String access_token = jsonObject.getString("access_token");
			return access_token;
		} catch (Exception e) {
			System.err.printf("获取token失败！");
			e.printStackTrace(System.err);
		}
		return null;
	}

	@RequestMapping(value = "/add")
	public static String add(@RequestParam String token) throws Exception {

		byte[] bytes1 = FileUtil.readFileByBytes("D:\\pic\\111.jpg");
		String image1 = Base64Utils.encode(bytes1);

		// 请求url
		String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add";
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("image", image1);
			map.put("group_id", "group_repeat");
			map.put("user_id", "user1");
			map.put("user_info", "abc");
			map.put("liveness_control", "NORMAL");
			//map.put("image_type", "FACE_TOKEN");   
			map.put("image_type", "BASE64");
			map.put("quality_control", "LOW");

			String param = JSONObject.toJSONString(map);
			// 客户端可自行缓存，过期后重新获取。
			String result = HttpUtil.post(url, token, "application/json", param);
			System.out.println(result);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
