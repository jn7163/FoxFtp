package FTPSearcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

public class Util {
	public static final String pathConnect(String[] elements) {
		if (elements == null || elements.length == 0)
			return "";

		String path = "";
		for (String e : elements) {
			e = e.trim();
			if (e.startsWith(File.pathSeparator))
				path += e;
			else
				path += File.separator + e;
		}
		return path;
	}

	public static final boolean copy(String path, String copyPath) {
		File filePath = new File(path);
		DataInputStream read = null;
		DataOutputStream write = null;
		try {
			if (filePath.isDirectory()) {
				File[] list = filePath.listFiles();
				for (int i = 0; i < list.length; i++) {
					String newPath = path + File.separator + list[i].getName();
					String newCopyPath = copyPath + File.separator
							+ list[i].getName();
					File newFile = new File(copyPath);
					if (!newFile.exists()) {
						newFile.mkdir();
					}
					copy(newPath, newCopyPath);
				}
			} else if (filePath.isFile()) {
				read = new DataInputStream(new BufferedInputStream(
						new FileInputStream(path)));
				write = new DataOutputStream(new BufferedOutputStream(
						new FileOutputStream(copyPath)));
				byte[] buf = new byte[1024 * 512];
				int b = 0;
				while ((b = read.read(buf)) != -1) {
					write.write(buf, 0, b);
				}
				read.close();
				write.close();
			} else {
				return false;
			}

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (read != null)
				try {
					read.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			if (write != null)
				try {
					write.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return true;
	}

	// 删除文件夹
	// param folderPath 文件夹完整绝对路径
	public static boolean delFolder(String folderPath) {
		try {
			if (!delAllFile(folderPath)) { // 删除完里面所有内容
				return false;
			}
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			if (!myFilePath.delete()) { // 删除空文件夹
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// 删除指定文件夹下所有文件
	// param path 文件夹完整绝对路径
	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		flag = true;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + File.pathSeparator + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + File.pathSeparator + tempList[i]);// 再删除空文件夹
				flag = true;
			}
		}
		return flag;
	}

	public static final String getRelativePath(String prefix,
			String absolutePath) {
		if (!absolutePath.startsWith(prefix)) {
			return null;
		} else {
			absolutePath = absolutePath.substring(prefix.length());
			if (absolutePath.startsWith(File.pathSeparator)) {
				absolutePath = absolutePath.substring(File.pathSeparator
						.length());
			}
			if (absolutePath.endsWith(File.pathSeparator)) {
				absolutePath = absolutePath.substring(0, absolutePath.length()
						- File.pathSeparator.length());
			}
			return absolutePath;
		}
	}

	public static final String genJSON(boolean success, String message) {
		String json = null;
		try {
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("success", success);
			jsonObj.put("message", message);
			json = jsonObj.toString();
			System.out.println(json);
		} catch (JSONException e) {
			e.printStackTrace();
			message = message.replace("\\", "\\\\");
			String _json = "{\"success\":" + (success ? "true" : "false")
					+ ",\"message\":\"" + message + "\"}";
			try {
				new JSONObject(_json);
				json = _json;
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return json;
	}

	/*
	 * 将一个含有引号的字符串转换为特殊表示形式，可以直接在page中使用的字符串, 目前是将所有的'转换为&#34;，"转换为&#39;
	 */
	public static final String packHtmlString(String originStr) {
		if (originStr == null)
			return "";

		originStr = originStr.replace("'", "&#39;");
		originStr = originStr.replace("\"", "&#34;");

		return originStr;
	}

	/*
	 * 将一个含有非ascii字符的url字符串转换为gbk编码
	 */
	public static String packUrlString(String s) {
		// StringBuffer sb = new StringBuffer();
		// for (int i = 0; i < s.length(); i++) {
		// char c = s.charAt(i);
		// if (c >= 0 && c <= 255) {
		// sb.append(c);
		// } else {
		// byte[] b;
		// try {
		// b = String.valueOf(c).getBytes("gbk");
		// } catch (Exception ex) {
		// System.out.println(ex);
		// b = new byte[0];
		// }
		// for (int j = 0; j < b.length; j++) {
		// int k = b[j];
		// if (k < 0)
		// k += 256;
		// sb.append("%" + Integer.toHexString(k).toUpperCase());
		// }
		// }
		// }
		// String _tmp = sb.toString();//.replace("%", "%25");
		// return _tmp;
		// StringBuffer sb = new StringBuffer();
		// int _dd = s.indexOf("//");
		// String _prefix = "";
		// if (_dd != -1) {
		// _prefix = s.substring(0, _dd + 2);
		// s = s.substring(_dd + 2);
		// }
		// sb.append(_prefix);
		// String eles[] = s.split("/");
		// try {
		// for (int i = 0; i < eles.length - 1; i++) {
		// eles[i] = URLEncoder.encode(eles[i], "gbk");
		// sb.append(eles[i]);
		// sb.append("/");
		// }
		// eles[eles.length - 1] = URLEncoder.encode(eles[eles.length - 1],
		// "gbk");
		// sb.append(eles[eles.length - 1]);
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// String _tmp = sb.toString().replace("+", "%20");
		// return _tmp;

		try {
			s = URLEncoder.encode(s, "gbk");
			s = s.replace("%3a", ":");
			s = s.replace("%2f", "/");
			s = s.replace("%3A", ":");
			s = s.replace("%2F", "/");
			s = s.replace("%5c", "\\");
			s = s.replace("%5C", "\\");
			s = s.replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return s;

	}
}
