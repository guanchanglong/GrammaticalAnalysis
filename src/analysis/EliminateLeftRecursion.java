package analysis;

import entity.Formula;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * @Author 小关同学
 * @Create 2022/5/2 22:36
 * 消除左递归(直接左递归和简介左递归)
 */
public class EliminateLeftRecursion {

    //原始式子
    private Map<String,String> oldFormula = new HashMap<>();
    //消除左递归后的式子
    private Map<String,String> newFormula = new HashMap<>();
    //经过封装过的式子集合，主要作用是判断是否经历了间接左递归的递归查找过程
    private ArrayList<Formula> param = new ArrayList<>();
    //是否发生了左递归
    private boolean isHappenLeftRecursion;

    public boolean isHappenLeftRecursion() {
        return isHappenLeftRecursion;
    }

    public Map<String, String> getNewFormula() {
        return newFormula;
    }

    public Map<String, String> getOldFormula() {
        return oldFormula;
    }


    /**
     * 初始化数据
     * @param fileSrc
     */
    public EliminateLeftRecursion(String fileSrc) {
        BufferedReader bufferedReader = FileUtil.readFile(fileSrc);
        if (bufferedReader!=null){
            String lineText = null;
            while(true){
                try {
                    if ((lineText = bufferedReader.readLine()) == null){
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                assert lineText != null;
                String[]strings = lineText.split("——>");
                Formula formulaEntity = new Formula(strings[0], strings[1]);
                oldFormula.put(strings[0], strings[1]);
                param.add(formulaEntity);
            }
        }
    }

    /**
     * 消除直接左递归
     * @param leftFormula
     * @param rightNonTerminal
     * @return
     */
    public boolean eliminateDirectLeftRecursion(String leftFormula, String rightNonTerminal) {
        //右侧由|分隔的符号集
        List<String> splitByLine = new ArrayList<>(Arrays.asList(rightNonTerminal.split("\\|")));
        int index = -1;
        //右侧大P所在的首部的字符串
        String oldFlag = null;
        int oldFlagIndex = 0;
        for (String string : splitByLine) {
            index = string.indexOf(leftFormula);
            if (index == 0) {
                oldFlag = string;
                break;
            }
            oldFlagIndex++;
        }
        //如果有直接左递归，进行消除直接左递归的操作
        if (index == 0) {
            //P'
            String newFlag = leftFormula + "'";
            //P'——>aP'|ε
            String newFormula1 = newFlag + "——>" + oldFlag.replace(leftFormula, "") + newFlag + "|ε";
            //移除直接左递归项
            splitByLine.remove(oldFlagIndex);
            //P——>bP'
            String newFormula2 = leftFormula + "——>";
            for (int i = 0; i < splitByLine.size(); i++) {
                String str = splitByLine.get(i);
                if (!str.equals("ε")){
                    newFormula2 = newFormula2 + str + newFlag;
                }else{
                    newFormula2 = newFormula2 + str;
                }
                if (i + 1 < splitByLine.size()) {
                    newFormula2 = newFormula2 + "|";
                }
            }
            String[] newFormulas1 = newFormula1.split("——>");
            String[] newFormulas2 = newFormula2.split("——>");
            newFormula.put(newFormulas1[0], newFormulas1[1]);
            newFormula.put(newFormulas2[0], newFormulas2[1]);
            isHappenLeftRecursion = true;
            return true;
        }else {
            newFormula.put(leftFormula, rightNonTerminal);
            return false;
        }
    }

    /**
     * 消除左递归
     * @return
     */
    public void eliminateLeftRecursion(){
        for(Formula formulaEntity: param){
            //如果已经进行过间接消除左查询，就不再循环
            if (formulaEntity.isRecursionFlag()){
                continue;
            }

            //消除式子的直接左递归
            boolean flag = eliminateDirectLeftRecursion(formulaEntity.getLeft(), formulaEntity.getRight());
            //如果式子没有直接左递归，看看是否是间接左递归
            if (!flag){
                String string1 = "";
                //右侧由|分隔的符号集
                List<String> splitByLine = new ArrayList<>(Arrays.asList(formulaEntity.getRight().split("\\|")));
                //搜索是否有间接左递归
                for (int i = 0;i < splitByLine.size();i++){
                    String string = splitByLine.get(i);
                    String flagNonTerminal = ""+string.charAt(0);
                    //判断是否存在首项是非终结符的
                    if (oldFormula.containsKey(flagNonTerminal)){
                        string = string.replaceFirst(flagNonTerminal, "");
                        //生成右边的式子
                        string1 = createRightFormulaString(formulaEntity.getLeft(), flagNonTerminal, string, i);
                        //如果最后面的符号是|，且是循环的最后一个元素的话，则去掉|
                        if (string1.charAt(string1.length()-1)=='|' && i==splitByLine.size()-1){
                            string1 = string1.substring(0, string1.length()-1);
                        }
                    }else{//如果首项不是终结符
                        string1 = string1 + string;
                        if (i+1<splitByLine.size()){
                            string1 = string1 + "|";
                        }
                    }
                }
                //消除式子的直接左递归
                eliminateDirectLeftRecursion(formulaEntity.getLeft(), string1);
            }

            //清除已经递归了的元素中多余的式子
            for (Formula formulaEntity2: param){
                if (formulaEntity2.isRecursionFlag()){
                    if (newFormula.containsKey(formulaEntity2.getLeft())){
                        String[]strings = newFormula.get(formulaEntity2.getLeft()).split("\\|");
                        for (String string: strings){
                            if (string.indexOf(formulaEntity.getLeft())==0){
                                newFormula.remove(formulaEntity2.getLeft());
                                break;
                            }
                        }
                    }
                }
            }
        }

        //如果发生了消除左递归才进行打印
        if (isHappenLeftRecursion){
            System.out.println("消除左递归后的结果：");
            for (Map.Entry<String,String> entry: newFormula.entrySet()){
                System.out.println(entry.getKey()+"——>"+entry.getValue());
            }
        }
    }

    public List<String> returnRightFormulaStringList(String flagNonTerminal, String leftFormula, String rightFormula){
        List<String> result = recursionSearch(flagNonTerminal, leftFormula);
        boolean flag = false;
        ListIterator<String> listIterator = result.listIterator();
        while(listIterator.hasNext()){
            String string = listIterator.next();
            if (string.equals("ε") && rightFormula.length()!=0){
                List<String> list;
                if (rightFormula.length() > 1){
                    String str1 = "" + rightFormula.charAt(0);
                    String str2 = rightFormula.replaceFirst(str1, "");
                    list = returnRightFormulaStringList(str1, leftFormula, str2);
                }else{
                    String str1 = "" + rightFormula.charAt(0);
                    list = returnRightFormulaStringList(str1, leftFormula, "");
                }
                for (String s: list){
                    if (!s.equals("ε")){
                        listIterator.add(s+"ε");
                    }else{
                        if (rightFormula.length()<=1){//如果此时后面已经没有元素了且存在ε，则证明结果必有ε
                            //这里找一个特殊符号#代表ε，跟其他的多余的ε区分开来
                            listIterator.add("#"+"ε");
                        }
                    }
                }
                flag = true;
            }
        }
        if (flag){
            result.add("ε");
        }
        return result;
    }

    public String createRightFormulaString(String leftFormula, String flagNonTerminal, String string, int flag){
        List<String> result = returnRightFormulaStringList(flagNonTerminal, leftFormula, string);
        result.removeIf(str -> str.equals("ε"));
        String string1 = "";
        for (int j = 0;j < result.size();j++){
            String string2 = result.get(j);
            if (string2.indexOf(leftFormula) == 0){
                newFormula.remove(flagNonTerminal);
            }
            int num = 0;
            for (int x = 0;x < string2.length();x++){
                if (string2.charAt(x)=='ε'){
                    num++;
                }
            }
            string2 = string2.substring(0, string2.length()-num);
            if (!string2.equals("ε")){
                if (num == 0){
                    if (string2.equals("#")){
                        string1 = string1 + "ε";
                    }else{
                        string1 = string1 + string2 + string;
                    }
                }else{
                    String str = "";
                    if (num <= string.length()){
                        str = string.substring(num, string.length());
                    }
                    if (string2.equals("#")){
                        string1 = string1 + "ε";
                    }else{
                        string1 = string1 + string2 + str;
                    }
                    result.set(j, string2.substring(0, string2.length()-1));
                }
            }else{//如果是ε的话，后续得查看ε后面是否还有符号(终结符或非终结符)
                string1 = string1 + result.get(j);
            }
            //后面还有元素，且该元素不为ε，就继续添加|分隔
            if (j+1<result.size() || flag < string.length()){
                string1 = string1 + "|";
            }
        }
        return string1;
    }

    /**
     * 改变式子状态，如果进行过间接消除左递归的过程的，就设置recursionFlag为true
     * 即已经经过递归查找简介左递归的式子下次消除左递归就不用在遍历了
     * @param nonTerminal
     */
    public void perform(String nonTerminal){
        for(Formula formulaEntity: param){
            if (formulaEntity.getLeft().equals(nonTerminal)){
                formulaEntity.setRecursionFlag(true);
            }
        }
    }


    /**
     * 递归向下搜寻、合并间接左递归
     * @param nonTerminal 式子本身的左终结符
     * @param targetCharacter 待搜寻的目标终结符
     * @return
     */
    public List<String> recursionSearch(String nonTerminal, String targetCharacter){
        String rightFormula = oldFormula.get(nonTerminal);
        List<String> rightFormulaCollection = new ArrayList<>(Arrays.asList(rightFormula.split("\\|")));
        perform(nonTerminal);
        newFormula.put(nonTerminal, rightFormula);
        for (int i = 0;i < rightFormulaCollection.size();i++){
            int index = -1;
            String string = rightFormulaCollection.get(i);
            index = string.indexOf(targetCharacter);

            //如果第一位是目的终结符，说明出现了间接左递归
            if (index == 0){
                return rightFormulaCollection;
            }

            String flagNonTerminal = ""+string.charAt(0);
            if (oldFormula.containsKey(flagNonTerminal)){
                List<String> result = recursionSearch(flagNonTerminal, targetCharacter);
                String string1 = string.replace(flagNonTerminal,"");
                String string2 = "";
                for (int j = 0;j < result.size();j++){
                    string2 = result.get(j) + string1 + string2;
                    if (j+1 < result.size()){
                        string2 = "|" + string2;
                    }
                }
                String[]strings = string2.split("\\|");
                rightFormulaCollection.addAll(Arrays.asList(strings));
                rightFormulaCollection.remove(i);
                newFormula.put(nonTerminal, rightFormula.replace(string, string2));
            }
        }
        return rightFormulaCollection;
    }

    //测试
    public static void main(String[] args) {
        EliminateLeftRecursion eliminateLeftRecursion = new EliminateLeftRecursion("./src/input.txt");
        eliminateLeftRecursion.eliminateLeftRecursion();
    }
}
