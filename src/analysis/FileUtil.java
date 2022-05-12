package analysis;

import java.io.*;

/**
 * @Author 小关同学
 * @Create 2022/5/2 23:30
 * 文件读取/写入
 */
public class FileUtil {


    /**
     * 读取文件文本内容
     * @param fileName
     * @return
     */
    public static BufferedReader readFile(String fileName) {

        BufferedReader bufferedReader = null;
        try {
            File myFile = new File(fileName);//通过字符串创建File类型对象，指向该字符串路径下的文件

            if (myFile.isFile() && myFile.exists()) { //判断文件是否存在

                InputStreamReader Reader = new InputStreamReader(new FileInputStream(myFile), "UTF-8");
                //考虑到编码格式，new FileInputStream(myFile)文件字节输入流，以字节为单位对文件中的数据进行读取
                //new InputStreamReader(FileInputStream a, "编码类型")
                //将文件字节输入流转换为文件字符输入流并给定编码格式

                bufferedReader = new BufferedReader(Reader);
                //BufferedReader从字符输入流中读取文本，缓冲各个字符，从而实现字符、数组和行的高效读取。
                //通过BuffereReader包装实现高效读取

            } else {
                System.out.println("找不到指定的文件");
            }

        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }

        return bufferedReader;

    }


}
