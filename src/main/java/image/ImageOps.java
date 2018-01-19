package image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by 11267 on 2018/1/19.
 * 1、图像处理技术：http://blog.csdn.net/zlxtk/article/details/54890789
 */

public class ImageOps {

    public static void main(String[] args) {
        try {
            BufferedImage read = ImageIO.read(new File("C:\\Users\\11267\\Desktop\\22.Jpeg"));
            int width = read.getWidth();
            int height = read.getHeight();

            System.out.println(width);
            System.out.println(height);
            System.out.println(height*width);
/*
            for (int w = 0; w < width; w++) {
                for (int h = 0; h < height; h++) {
                    int rgb = read.getRGB(w, h);
                    System.out.println(rgb);
                    read.setRGB(w,h,2222);

                }
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
