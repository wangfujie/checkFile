import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;

public class ScanDirectory {

    private static final String SCAN_RECORD_FILE = "scan_record.txt";

    public static void main(String[] args) throws IOException {
        String directoryPath = args[0];
        String writeFile = args[1];

        long startTime = System.currentTimeMillis();

        AtomicInteger fileCount = new AtomicInteger(0);
        AtomicInteger dirCount = new AtomicInteger(0);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(SCAN_RECORD_FILE))) {
            // 使用Files.walkFileTree遍历目录
            Files.walkFileTree(Paths.get(directoryPath), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    // 直接写入文件，而不是存储在内存中
                    if (writeFile != null && writeFile.equals("true")) {
                        writer.write(file.toString());
                        writer.newLine(); // 换行
                    }
                    fileCount.incrementAndGet();
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    // 处理文件访问失败情况
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    // 直接写入文件，而不是存储在内存中
                    if (writeFile != null && writeFile.equals("true")) {
                        writer.write(dir.toString());
                        writer.newLine(); // 换行
                    }
                    dirCount.incrementAndGet();
                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

        long endWriteTime = System.currentTimeMillis();
        System.out.println("遍历及写入记录文件耗时：" + (endWriteTime - startTime) + "ms");
        System.out.println("文件数量：" + fileCount.get());
        System.out.println("目录数量：" + dirCount.get());
    }
}
