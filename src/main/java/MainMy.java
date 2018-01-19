import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * 唯一改造：结果权重算法改为了直接获取搜索结果最大的
 */
public class MainMy {
    private static final int NUM_OF_ANSWERS = 3;
    private static final String QUESTION_FLAG="?";

    public static String[] choices = new String[]{"551,712","551,925","551,1109"};

    public static void main(String[] args) throws IOException {
        BufferedReader bf=new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            String str=bf.readLine();
            System.out.println("开始执行");
            try {
                if(str.length()==0){
                    run();
                }
            } catch (Exception e) {
               e.printStackTrace();
            }

        }
    }

    private static void run() throws InterruptedException {
//       记录开始时间
        long startTime;
//       记录结束时间
        long endTime;
        startTime = System.currentTimeMillis();
        //获取图片
        File image = new Phone().getImage();
        System.out.println("获取图片成功" + image.getAbsolutePath());
        //图像识别
        Long beginOfDectect=System.currentTimeMillis();
        String questionAndAnswers = new TessOCR().getOCR(image);
        System.out.println("识别成功："+questionAndAnswers);
        System.out.println("识别时间："+(System.currentTimeMillis()-beginOfDectect));
        if(!questionAndAnswers.contains(QUESTION_FLAG)){
            return;
        }
        //获取问题和答案
        System.out.println("检测到题目");
        Information information = new Information(questionAndAnswers);
        String question = information.getQuestion();
        String[] answers = information.getAns();
        System.out.println("问题:" + question);
        System.out.println("答案：");
        for (String answer : answers) {
            System.out.println(answer);
        }
        //搜索
        long countQuestion = 1;
        Long[] countQA = new Long[3];

        int maxIndex = 0;

        SearchMy[] searchQA = new SearchMy[3];
        SearchMy[] searchAnswers = new SearchMy[3];
        FutureTask<Long>[] futureQA = new FutureTask[NUM_OF_ANSWERS];
        for (int i = 0; i < NUM_OF_ANSWERS; i++) {
            // 搜索
            searchQA[i] = new SearchMy(question + " " + answers[i]);
            searchAnswers[i] = new SearchMy(answers[i]);
            futureQA[i] = new FutureTask<Long>(searchQA[i]);
            new Thread(futureQA[i]).start();
        }
        try {
            for (int i = 0; i < NUM_OF_ANSWERS; i++) {
                while (!futureQA[i].isDone()) {}
                countQA[i] = futureQA[i].get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //根据pmi值进行打印搜索结果
        int[] rank=rank(countQA);
        for (int i : rank) {
            System.out.print(answers[i]);
            System.out.println(" countQA:"+countQA[i]);
        }
        System.out.println("--------最终结果-------");
        System.out.println(answers[rank[NUM_OF_ANSWERS-1]]);// 取得最后一个权值最大的答案
        endTime = System.currentTimeMillis();
        float excTime = (float) (endTime - startTime) / 1000;

        System.out.println("执行时间：" + excTime + "s");

        // 点击答案
        String choice = choices[rank[NUM_OF_ANSWERS-1]];
        executeCommand(Phone.ADB_PATH+" shell input tap "+choice.split(",")[0]+" " + choice.split(",")[1]);
    }

    /**
     *
     * @param floats pmi值
     * @return 返回排序的rank
     */
    private static int[] rank(Long[] floats){
        int[] rank=new int[NUM_OF_ANSWERS];
        Long[] f=Arrays.copyOf(floats,3);
        Arrays.sort(f);
        for (int i = 0; i < NUM_OF_ANSWERS; i++) {
            for (int j = 0; j < NUM_OF_ANSWERS; j++) {
                if(f[i]==floats[j]){
                    rank[i]=j;
                }
            }
        }
        return rank;
    }

    /**
     * 执行shell命令
     *
     * @param command
     */
    private static void executeCommand(String command) {
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

