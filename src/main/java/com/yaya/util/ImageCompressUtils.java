package com.yaya.util;

import com.yaya.exception.GlobalCommonException;
import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;

/**
 * 图片压缩工具
 */
public class ImageCompressUtils {

    /**
     * 压缩图片到指定大小以下（不改变长宽）
     * @param inputFile  原始图片文件
     * @param outputFile 输出文件
     * @param targetMaxSizeKB 目标最大大小（KB）
     * @return 是否压缩成功
     */
    public static boolean compressImage(File inputFile, File outputFile, int targetMaxSizeKB) throws IOException {
        if (!inputFile.exists()) {
            throw new GlobalCommonException("压缩文件不存在");
        }

        long targetBytes = targetMaxSizeKB * 1024L;//目标压缩大小
        long originalSize = inputFile.length();//源文件大小
        //如果源文件比目标文件小,不需要压缩
        if (originalSize <= targetBytes) {
            Thumbnails.of(inputFile).scale(1.0).outputQuality(1.0).toFile(outputFile);
            System.out.println("无需压缩");
            return true;
        }

        System.out.println("原始大小: " + (originalSize / 1024) + " KB");
        //临时文件保存位置
        File tempFile = new File(outputFile.getParent(), outputFile.getName());
        double quality = 0.95;                 //图片质量
        double scale = 1.0;                    // 当前缩放比例
        int maxAttempts = 20;                  //最多压缩次数,防止死循环
        int attempt = 0;
        //文件过大,要想达到目标大小,需要渐进式循环压缩
        while (attempt < maxAttempts) {
            attempt++; //记录压缩次数

            //压缩,并将压缩文件保存到临时目录
            Thumbnails.of(inputFile)
                    .scale(scale)
                    .outputFormat("jpg")
                    .outputQuality(quality)
                    .toFile(tempFile);
            //压缩后的文件大小
            long currentSize = tempFile.length();
            System.out.printf("第%d次尝试 → 质量: %.2f, 缩放: %.2f → 大小: %d KB%n",attempt, quality, scale, currentSize / 1024);
            //如果压缩后的文件已经比目标文件小,那么压缩完成直接输出
            if (currentSize <= targetBytes) {
                System.out.println("✅ 压缩成功！最终质量=" + quality + ", 缩放=" + scale);
                return true;
            }

            // 策略：先快速降低质量，再降低分辨率
            if (quality > 0.25) {
                quality -= 0.15;          // 先快速降质量
            } else if (quality > 0.1) {
                quality = 0.1;
            } else {
                // 质量已经最低，开始轻微缩放
                scale = Math.max(0.6, scale - 0.1);   // 最低缩放到60%
            }
        }

        // 最终兜底,通过上面循环压缩还没有压缩到最后目标的大小,采用jpg文件的方式最后压缩一次
        Thumbnails.of(inputFile)
                .scale(Math.max(0.6, scale))
                .outputFormat("jpg")
                .outputQuality(0.1)
                .toFile(outputFile);
        // 最终压缩的文件大小
        long finalSize = outputFile.length() / 1024;
        System.out.println("最终压缩结果: " + finalSize + " KB");
        return finalSize <= targetMaxSizeKB;
    }

    static void main() throws IOException {
        compressImage(new File("C:\\Users\\Administrator\\Pictures\\Camera Roll\\桌面壁纸\\【哲风壁纸】大海-岛屿-栈桥.png"),new File("C:\\Users\\Administrator\\Pictures\\Camera Roll\\桌面壁纸\\【哲风壁纸】大海-岛屿-栈桥v2.png"),200);
    }
}
