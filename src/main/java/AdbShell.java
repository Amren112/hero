import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by 11267 on 2018/1/16.
 */
public class AdbShell {
    // adb目录
    public static String ADB_HOME = "D:\\adb\\adb ";
    //按压的时间系数，可根据具体情况适当调节
    private final double pressTimeCoefficient = 1.35;

    public static void main(String[] args) {
        try {
            // 输入按键
           /* Process process = Runtime.getRuntime().exec(ADB_HOME+"shell input keyevent 40");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = bufferedReader.readLine();
            while(line != null) {
                System.out.println(line);
            }*/

            // 输入文本
            Runtime.getRuntime().exec(ADB_HOME + "shell input text \"hashdhasdhas啊啊\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 按压屏幕
     *
     * @param distance
     * @author LeeHo
     * @update 2017年12月31日 下午12:23:19
     */
    private void doPress(double distance) {
        System.out.println("distance: " + distance);
        //计算按压时间，最小200毫秒
        int pressTime = (int) Math.max(distance * pressTimeCoefficient, 200);
        System.out.println("pressTime: " + pressTime);
        //执行按压操作
        String command = String.format(ADB_HOME+" shell input swipe %s %s %s %s %s", 0, 0, 0, 0,pressTime);
        System.out.println(command);
        executeCommand(command);
    }

    /**
     * 执行shell命令
     *
     * @param command
     */
    private void executeCommand(String command) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            System.out.println("exec command start: " + command);
            process.waitFor();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line = bufferedReader.readLine();
            if (line != null) {
                System.out.println(line);
            }
            System.out.println("exec command end: " + command);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}
