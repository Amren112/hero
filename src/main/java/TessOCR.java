import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoadLibs;

import java.awt.*;
import java.io.File;

/**
 * Created by 618 on 2018/1/8.
 * @author lingfengsan
 */
public class TessOCR {
    String getOCR(File imageFile){
        ITesseract instance = new Tesseract();
        File tessDataFolder = LoadLibs.extractTessResources("tessdata");
        instance.setLanguage("chi_sim");
        //Set the tessdata path
        instance.setDatapath(tessDataFolder.getAbsolutePath());
        try {
            Rectangle rectangle = new Rectangle(100, 300, 1000, 1100);
            return instance.doOCR(imageFile,rectangle)
                    .replace(" ",".").replace(",","");
        } catch (TesseractException e) {
            System.err.println("提取文字失败："+e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        File imageFile=new File("C:\\Users\\11267\\Desktop\\910390302542145397.png");
        TessOCR tessOCR=new TessOCR();
        System.out.println(tessOCR.getOCR(imageFile));
    }
}
