package com.Trade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.gson.Gson;

@SuppressWarnings("serial")
public class NewOkcoinTradeRecord1Servlet extends HttpServlet {
	static Map<String, Boolean> map = new LinkedHashMap<String, Boolean>();
	static ArrayList<String> inputList = new ArrayList<String>();

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		synchronized (this) {
			String url = "https://www.okcoin.com/api/v1/future_trades.do";
			String param = "symbol=btc_usd&contract_type=quarter";

			while (true) {
				try {
					Gson gson = new Gson();

					String result = "";

					for (int g = 0; g < 2; g++) {
						// Servaer 回傳值
						result = sendGet(url, param);
						if (!result.equals("")) {
							// 取出不重複資料
							JSONArray jsonArray = new JSONArray(result);

							if (jsonArray != null) {
								int len = jsonArray.length();
								for (int i = 0; i < len; i++) {
									// System.out.println((String)
									// jsonArray.get(i));
									String tid = gson.fromJson(String.valueOf(jsonArray.get(i)), TradeRecord.class)
											.getTid();
									if (map.get(tid) == null) {
										inputList.add(jsonArray.get(i).toString());
										map.put(tid, true);
									}
								}
							}
							Thread.sleep(1000);
						}

					}
					/*
					 * Thread t0 = new Thread(new Runnable() { public void run()
					 * { try { // METHOD } catch (Exception e) {
					 * e.printStackTrace(); } } }, ""); Thread t1 = new
					 * Thread(t0); t1.start();
					 */

					/*
					 * 超慢速
					 * 
					 * JSONArray json = new JSONArray(inputList.toString());
					 * String url2 =
					 * "https://script.google.com/macros/s/AKfycbzEnSeQ0ZJtHQxEYs3AnuCIdocnsUp0iiOF_yBVBiWVsX9RVuMG/exec";
					 * param = "name=" + json.toString(); sendPost(url2, param);
					 */
					// Long time1 = System.currentTimeMillis();
					
					Thread t1 = ThreadManager.createThreadForCurrentRequest(new Runnable() {
						public void run() {
							try {
								int size = inputList.size();
								JSONArray json = new JSONArray(inputList.toString());
								String url2 = "https://script.google.com/macros/s/AKfycbzEnSeQ0ZJtHQxEYs3AnuCIdocnsUp0iiOF_yBVBiWVsX9RVuMG/exec";
								String param2 = "name=" + json.toString();
								sendPost(url2, param2);

								for (int i = 0; i < size; i++) {
									inputList.remove(0);
									Gson gson2 = new Gson();

									String tid = gson2.fromJson((String) inputList.get(i), TradeRecord.class).getTid();
									String msStart = gson2.fromJson((String) inputList.get(0), TradeRecord.class)
											.getDate_ms();
									String msEnd = gson2
											.fromJson((String) inputList.get(size - 1), TradeRecord.class)
											.getDate_ms();
									if ((Long.parseLong(msEnd) - Long.parseLong(msStart)) > 5 * 60 * 1000
											&& map.get(tid) != null) {
										map.remove(tid);
									}

								}

							} catch (Exception e) {

								e.printStackTrace();
							}
						}
					});

					t1.start();
					/*
					 * Gson gson2 = new Gson(); for (int i = 0; i <
					 * inputList.size(); i++) { String tid =
					 * gson2.fromJson((String) inputList.get(i),
					 * TradeRecord.class).getTid(); String msStart =
					 * gson2.fromJson((String) inputList.get(0),
					 * TradeRecord.class).getDate_ms(); String msEnd =
					 * gson2.fromJson((String) inputList.get(inputList.size() -
					 * 1), TradeRecord.class) .getDate_ms(); if
					 * ((Long.parseLong(msEnd) - Long.parseLong(msStart)) > 5 *
					 * 60 * 1000 && map.get(tid) != null) { map.remove(tid); } }
					 */

					// Long time2 = System.currentTimeMillis();
					// 送出App Script存資料

					/*
					 * for (Map.Entry<String, Boolean> entry : map.entrySet()) {
					 * map.remove(entry.getKey()); }
					 */
					// resp.getWriter().println(time2-time1);
					// 檢查超過5分資料remove

					// map.clear();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}

		// String url = "https://www.okcoin.com/api/v1/future_trades.do";
		// String param = "symbol=btc_usd&contract_type=quarter";
		// String result = GetPost.sendGet(url, param);

		// resp.getWriter().println(result);

	}

	public static String sendGet(String url, String param) {
		String result = "";
		BufferedReader in = null;
		try {
			String urlName = url + "?" + param;
			URL realUrl = new URL(urlName);
			// �򿪺�URL֮�������
			URLConnection conn = realUrl.openConnection();
			// ����ͨ�õ���������
			conn.setRequestProperty("accept", "application/json");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setRequestProperty("Content-Type", "application/json"); // �趨
																			// �����ʽ
																			// json��Ҳ�����趨xml��ʽ��
			// ����ʵ�ʵ�����
			conn.connect();
			// ��ȡ������Ӧͷ�ֶ�
			Map<String, List<String>> map = conn.getHeaderFields();
			// �������е���Ӧͷ�ֶ�
			for (String key : map.keySet()) {
				// System.out.println(key + "--->" + map.get(key));
			}
			// ����BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += "\n" + line;
			}
			// String s = new String(result.getBytes("utf-8"),"utf-8");
			// System.out.println(s);
		} catch (Exception e) {
			System.out.println("����GET��������쳣��" + e);
			e.printStackTrace();
		}
		// ʹ��finally�����ر�������
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	public static String sendPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// �򿪺�URL֮�������
			URLConnection conn = realUrl.openConnection();
			// ����ͨ�õ���������
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			// ����POST�������������������
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// ��ȡURLConnection�����Ӧ�������
			out = new PrintWriter(conn.getOutputStream());
			// �����������
			out.print(param);
			// flush������Ļ���
			out.flush();
			// ����BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result += line + "\n";
			}
		} catch (Exception e) {
			System.out.println("����POST��������쳣��" + e);
			e.printStackTrace();
		}
		// ʹ��finally�����ر��������������
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
}
