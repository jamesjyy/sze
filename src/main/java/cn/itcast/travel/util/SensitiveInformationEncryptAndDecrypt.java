package cn.itcast.travel.util;


import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * 
 * 敏感信息加密解密
 * 	生成update sql脚本 方法   
 * @version </pre>
 */
public class SensitiveInformationEncryptAndDecrypt {
	/**
	 * 	生成update sql 方法
	 * 		生成文件名    D:/敏感信息加密-update.sql.txt
	 * 				D:/敏感信息解密-update.sql.txt
	 * 			注意 在每执行一次 会在文件后面进行追加，执行sql 脚本时    注意位置 
	 * 				执行方法时，需先更换数据库url，端口号，数据库名，用户名，密码
	 * 		注意切换写入文件的  加密或者解密方法   
	 * 					90    行为  加密
	 * 					105 行为  解密
	 * 					119 行为  备份数据
	 * */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		//输入表名   (ebs_pic_compare_info,ebs_bird_compare_info,ebs_exam_compare_info,ebs_sendjson_1og_201808)等相关表
		String tables = "ebs_user_info";
		
		//声明Connection对象
        Connection con = null;
        //声明ResultSet对象
        ResultSet rs = null;
        //声明PreparedStatement对象
        PreparedStatement pst = null;
        //驱动程序名
        String driver = "com.mysql.cj.jdbc.Driver";
        //URL指向要访问的数据库地址，端口号，数据库名mybatis
        String url = "jdbc:mysql://localhost:3306/edcebs?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone = GMT";
        //MySQL配置时的用户名
        String user = "root";
        //MySQL配置时的密码
        String password = "1234";
        try {
            //加载驱动程序
            Class.forName(driver);
            //1.getConnection()方法，连接MySQL数据库！！
            con = DriverManager.getConnection(url,user,password);
            //要执行的SQL语句
            String sql;
            //初始化集合
            List<Map<String,Object>> resultListMap = new ArrayList<Map<String,Object>>();
            if("ebs_user_info".equals(tables)){
            	sql = "select ID id,PW pw from "+tables.trim();
            	 //创建prepareStatement
    			pst = con.prepareStatement(sql);
                //3.ResultSet类，用来存放获取的结果集！！
                rs = pst.executeQuery();
                while(rs.next()){
                	Map paramMap = new HashMap<>();
                	paramMap.put("id", rs.getLong(1));
                	paramMap.put("pw", rs.getString(2));
                	resultListMap.add(paramMap);
                }
            }else{
            	sql = "select ID id,IDNO idno,IDDOC_NAME name from "+tables.trim();
            	//创建prepareStatement
    			pst = con.prepareStatement(sql);
                //3.ResultSet类，用来存放获取的结果集！！
                rs = pst.executeQuery();
                while(rs.next()){
                	Map paramMap = new HashMap<>();
                	paramMap.put("id", rs.getLong(1));
                	paramMap.put("idno", rs.getString(2));
                	paramMap.put("name", rs.getString(3));
                	resultListMap.add(paramMap);
                }
            }
           MD5 md5=new MD5();
            for (Map<String, Object> map : resultListMap) {
            	//敏感信息加密
            	String str1 = (String) map.get("id").toString();
            	String str2 = (String) map.get("pw");
            	if(19 < str1.length() && 5 < str2.length()){
            		continue;
            	}
            	if("ebs_user_info".equals(tables)){
            		//String u="update ebs_user_info e set e.PW='"+md5.MD5(map.get(key))"'";
            		String ss=(String) map.get("pw");
            		String substring = ss.substring(0, 2);
            		String updates = "update "+tables.trim()+" set PW = '"+md5.MD5((String) map.get("pw")+substring)+"' where ID = "+map.get("id")+";\r\n";
            		//String updates = "update "+tables.trim()+" set CARD_NO = '"+des.encrypt((String) map.get("idno"))+"' where ID = "+map.get("id")+";\r\n";
            		outputTxt(updates,"e:/登录密码MD5加密-生产-edcebs-20180906-update-sql.txt");
            	}else{
            		String ss=(String) map.get("pw");
            		String substring = ss.substring(0, 2);
            		String updates = "update "+tables.trim()+" set PW = '"+md5.MD5((String) map.get("pw")+substring)+"' where ID = "+map.get("id")+";\r\n";
            		outputTxt(updates,"e:/登录密码MD5加密-生产-edcebs-20180906-update-sql.txt");
            	}
            	
            	//敏感信息解密
            	/*String str1 = (String) map.get("idno");
            	String str2 = (String) map.get("name");
            	if(19 > str1.length() && 5 > str2.length()){
            		continue;
            	}
            	if("ebs_exam_compare_info".equals(tables)){
            		String updates = "update "+tables.trim()+" set CARD_NO = '"+des.decrypt((String) map.get("idno"))+"' where ID = "+map.get("id")+";\r\n";
            		outputTxt(updates,"D:/一体机查验敏感信息脱敏解密-生产-edcebs-20180906-1-1-update-sql.txt");
            	}else{
            		String updates = "update "+tables.trim()+" set IDNO = '"+des.decrypt((String) map.get("idno"))+"',IDDOC_NAME = '"+des.decrypt((String) map.get("name"))+"' where ID = "+map.get("id")+";\r\n";
            		outputTxt(updates,"D:/一体机查验敏感信息脱敏解密-生产-edcebs-20180906-1-1-update-sql.txt");
            	}*/
            	
            	//敏感信息备份
            	if("ebs_exam_compare_info".equals(tables)){
            		String updates = "update "+tables.trim()+" set PW = '"+(String) map.get("pw")+"' where ID = "+map.get("id")+";\r\n";
            		outputTxt(updates,"e:/登录密码MD5加密备份-生产-edcebs-20180906-1-1-update-fallback-sql.txt");
            	}else{
            		//String updates = "update "+tables.trim()+" set PW = '"+(String) map.get("pw")+"',IDDOC_NAME = '"+(String) map.get("name")+"' where ID = "+map.get("id")+";\r\n";
            		String updates = "update "+tables.trim()+" set PW = '"+(String) map.get("pw")+"' where ID = "+map.get("id")+";\r\n";
            		outputTxt(updates,"e:/登录密码MD5加密备份-生产-edcebs-20180906-1-1-update-fallback-sql.txt");
            	}
            }
            System.out.println("数据库数据成功获取！！!");
        } catch(ClassNotFoundException e) {   
            //数据库驱动类异常处理
            System.out.println("Sorry,can`t find the Driver!");   
            e.printStackTrace();   
            } catch(SQLException e) {
            //数据库连接失败异常处理
            e.printStackTrace();  
            }catch (Exception e) {
            e.printStackTrace();
        }finally{
        	if(null != rs){
        		try {
					rs.close();
					rs = null;
					pst.close();
					pst = null;
					con.close();
					con = null;
				} catch (SQLException e) {
					e.printStackTrace();
				}
        	}
        }
	}
	/**
	 * 	将数据写入文件
	 * */
	private static void outputTxt(String updates,String address){
		FileWriter writer = null;
		try {
			writer = new FileWriter(address , true);//true表示在源文件后追加，false表示覆盖，默认false
			writer.write(updates);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(null != writer){
				try {
					writer.close();
					writer = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
