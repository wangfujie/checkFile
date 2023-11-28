package com.wangfj.file;

import cn.hutool.core.date.TimeInterval;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IncrementalScanner {

    private static final String SCAN_RECORD_FILE = "scan_record.txt";

    /**
     * readPreviousScanRecord耗时：4毫秒
     * scanDirectory耗时：57毫秒
     * Added: 3874
     * Removed: 0
     * Modified: 0
     * writeCurrentScanRecord耗时：10毫秒
     * @param args
     */
    public static void main(String[] args) {
        try {
            TimeInterval timeInterval = new TimeInterval();
            timeInterval.start("readPreviousScanRecord");
            Set<String> previousScan = readPreviousScanRecord();
            //打印读取耗时
            System.out.println("readPreviousScanRecord耗时：" + timeInterval.intervalPretty("readPreviousScanRecord"));
            timeInterval.start("scanDirectory");
            List<String> currentScan = scanDirectory("D:\\developer\\softsafe\\code\\sca-platform");
            //打印 scanDirectory耗时
            System.out.println("scanDirectory耗时：" + timeInterval.intervalPretty("scanDirectory"));

            Set<String> added = new HashSet<>(currentScan);
            added.removeAll(previousScan);

            Set<String> removed = new HashSet<>(previousScan);
            currentScan.forEach(removed::remove);

            Set<String> modified = new HashSet<>(previousScan);
            modified.retainAll(currentScan);

            System.out.println("Added: " + added.size());
            System.out.println("Removed: " + removed.size());
            System.out.println("Modified: " + modified.size());

            timeInterval.start("writeCurrentScanRecord");
            writeCurrentScanRecord(currentScan);
            System.out.println("writeCurrentScanRecord耗时：" + timeInterval.intervalPretty("writeCurrentScanRecord"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描指定目录，返回包含所有文件和目录路径的集合
     * @param directoryPath 目录路径
     * @return 包含所有文件和目录路径的集合
     * @throws IOException
     */
    private static List<String> scanDirectory(String directoryPath) throws IOException {
        // 存储扫描结果的集合
        final List<String> scanResult = new ArrayList<>();

        // 使用Files.walkFileTree遍历目录
        Files.walkFileTree(Paths.get(directoryPath), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // 将文件路径添加到结果集合，包括文件的修改时间
                scanResult.add(file.toString() + " " + attrs.lastModifiedTime());
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                // 处理文件访问失败情况
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                // 将目录路径添加到结果集合
                scanResult.add(dir.toString());
                return FileVisitResult.CONTINUE;
            }
        });

        // 返回扫描结果集合
        return scanResult;
    }

    private static Set<String> readPreviousScanRecord() throws IOException {
        Set<String> result = new HashSet<>();
        try {
            result.addAll(Files.readAllLines(Paths.get(SCAN_RECORD_FILE)));
        } catch (IOException e) {
            // 处理扫描记录文件不存在的情况
        }
        return result;
    }

    private static void writeCurrentScanRecord(List<String> scanResult) throws IOException {
        // 将当前扫描结果写入文件
        Files.write(Paths.get(SCAN_RECORD_FILE), scanResult);
    }
}


