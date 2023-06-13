package com.wangfj.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;

import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CheckFileMain
 *
 * @author shimi
 * {@code @date} 2023/6/12
 */
public class CheckFileMain {

    public static ExecutorService executorService = ThreadUtil.newExecutor(16);

    public static void main(String[] args) {
        String filePath = "D:\\Downloads\\赛博朋克2077\\S156\\S156_v1.61";
        String md5FileName = "S156_v1.61.md5";
        List<Future> futureList = new ArrayList<>();
        AtomicInteger numAtomicInt = new AtomicInteger(0);
        try (RandomAccessFile accessFile = new RandomAccessFile(FileUtil.newFile(filePath + FileUtil.FILE_SEPARATOR + md5FileName), "r")){
            String lineStr = "";
            do {
                lineStr = FileUtil.readLine(accessFile, Charset.defaultCharset());
                if (StrUtil.isNotEmpty(lineStr)){
                    final String[] contents = lineStr.split(" ");
                    Future taskFuture = executorService.submit(() -> {
                        int fileNum = numAtomicInt.getAndIncrement();
                        String fileMd5 = contents[0];
                        String fileName = contents[1].replace("*", "");
//                        System.out.println("-----读取内容：fileMd5: " + fileMd5 + " fileName:" + fileName);
                        String checkFileMd5 = SecureUtil.md5(FileUtil.newFile(filePath + FileUtil.FILE_SEPARATOR + fileName));
                        if (fileMd5.equals(checkFileMd5)){
                            System.out.println(fileNum + ":通过校验：" + fileName);
                        }else {
                            System.out.println(fileNum + ":未通过校验：" + fileName);
                        }
                    });
                    futureList.add(taskFuture);
                }
            }while (StrUtil.isNotEmpty(lineStr));
            System.out.println("总条数：" + numAtomicInt.get() + " 线程数：" + futureList.size());
            for (Future taskFuture : futureList){
                taskFuture.get();
            }
            System.out.println("执行完成--------------------");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
