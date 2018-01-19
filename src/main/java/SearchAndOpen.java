import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by 618 on 2018/1/8.
 *
 * @author lingfengsan
 */
public class SearchAndOpen implements Callable {
    private final String question;

    SearchAndOpen(String question) {
        this.question = question;
    }

    private Long searchAndOpen(String question) throws IOException {
        String path = null;
        try {
            path = "http://www.baidu.com/s?tn=ichuner&lm=-1&word=" +
                    URLEncoder.encode(question, "gb2312") + "&rn=20&t="+new Date().getTime();
            //获取操作系统的名字
            String osName = System.getProperty("os.name", "");
            if (osName.startsWith("Mac OS")) {
                //苹果的打开方式
                Class fileMgr = Class.forName("com.apple.eio.FileManager");
                Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[]{String.class});
                openURL.invoke(null, new Object[]{path});
            } else if (osName.startsWith("Windows")) {
                //windows的打开方式。
                //Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + path);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + path);
        return new SearchMy(question).search(question);
    }

    public static void main(String[] args) throws Exception {
        SearchAndOpen search = new SearchAndOpen("阿尔茨海默症又被称为什么? 老年痴呆症");
        System.out.println(search.call());
        SearchAndOpen search2 = new SearchAndOpen("阿尔茨海默症又被称为什么? 癌症");
        System.out.println(search2.call());
        SearchAndOpen search3 = new SearchAndOpen("阿尔茨海默症又被称为什么? 老年迟钝");
        System.out.println(search3.call());
    }

    @Override
    public Long call() throws Exception {
        return searchAndOpen(question);
    }
}
