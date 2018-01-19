import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 618 on 2018/1/8.
 * @author lingfengsan
 */
public class SearchMy implements Callable {
    private final String question;

    SearchMy(String question) {
        this.question = question;
    }
    Long search(String question) throws IOException {
//        String path = "http://www.baidu.com/s?tn=ichuner&lm=-1&word=" +
//                URLEncoder.encode(question, "gb2312") + "&rn=1&t="+new Date().getTime();
        String path = "https://www.baidu.com/baidu?wd="+URLEncoder.encode(question, "gb2312")+"&tn=monline_dg&ie=utf-8'";
        boolean findIt = false;
        String line = null;
       /* while (!findIt) {
            URL url = new URL(path);
            BufferedReader breaded = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((line = breaded.readLine()) != null) {
                System.out.println(line);
                if (line.contains("百度为您找到相关结果约")) {
                    findIt = true;
                    int start = line.indexOf("百度为您找到相关结果约") + 11;

                    line = line.substring(start);
                    int end = line.indexOf("个");
                    line = line.substring(0, end);
                    break;
                }

            }
        }
        line = line.replace(",", "");*/
        line = doRequest(path);
        return Long.valueOf(line);
    }

    static { System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");}
    public static void main(String[] args) throws Exception {
        SearchMy search = new SearchMy("阿尔茨海默症又被称为什么?");
        System.out.println(search.call());

        /*int[] rank = rank(new Long[]{1l, 17l, 6l});
        for (int i : rank) {
            System.out.println(i);
        }*/
    }

    /**
     * 从小到达排序，返回索引排序
     * @param floats
     * @return
     */
    private static int[] rank(Long[] floats){
        int[] rank=new int[3];
        Long[] f= Arrays.copyOf(floats,3);
        Arrays.sort(f);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(f[i]==floats[j]){
                    rank[i]=j;
                }
            }
        }
        return rank;
    }

    @Override
    public Long call() throws Exception {
        return search(question);
    }

    public String doRequest(String url){
        String num = "0";
        try {
            String html = GPHttpUtils.GeneralRequest(url, GPHttpUtils.METHOD_TYPE.GET);
            Document jsoup = Jsoup.parse(html);
            String result = jsoup.select(".nums").text();
            result = result.replaceAll(",", "");
            System.out.println("处理后的结果："+result);
            Pattern pattern_string = Pattern.compile("\\w([0-9]{0,})\\w");
            Matcher matcher_string = pattern_string.matcher(result);
            if(matcher_string.find()){
                num = matcher_string.group(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return num;
    }
}
