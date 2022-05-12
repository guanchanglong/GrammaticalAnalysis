package analysis;

import java.util.*;

/**
 * @Author 小关同学
 * @Create 2022/5/2 22:37
 * 构建FIRST集
 */
public class FirstCollection {

    private EliminateLeftRecursion eliminateLeftRecursion;
    private Map<String, Set<String>> firstCollectionMap = new HashMap<>();

    public Map<String, Set<String>> getFirstCollectionMap() {
        return firstCollectionMap;
    }

    public EliminateLeftRecursion getEliminateLeftRecursion() {
        return eliminateLeftRecursion;
    }

    public FirstCollection(String fileSrc) {
        //消除左递归
        this.eliminateLeftRecursion = new EliminateLeftRecursion(fileSrc);
        eliminateLeftRecursion.eliminateLeftRecursion();
        //生成First集
        createFirstCollection();
    }

    public void createFirstCollection(){
        Map<String,String> formula = eliminateLeftRecursion.getNewFormula();
        for (Map.Entry<String,String> entry: formula.entrySet()){
            String leftNonTerminal = entry.getKey();
            Set<String> set = new HashSet<>();
            searchFirstCharacter(leftNonTerminal, set);
            firstCollectionMap.put(leftNonTerminal, set);
        }

        System.out.println();
        for (Map.Entry<String, Set<String>> entry: firstCollectionMap.entrySet()){
            System.out.println(entry.getKey() + "的FIRST集：" + entry.getValue().toString());
        }
    }


    /**
     * 递归向下查找式子第一个开头的字符
     * @param nonTerminal 终结符
     */
    public void searchFirstCharacter(String nonTerminal, Set<String> set){
        Map<String,String> formula = eliminateLeftRecursion.getNewFormula();
        String[]strings = formula.get(nonTerminal).split("\\|");
        for (String string: strings){
            String firstCharacter = ""+string.charAt(0);
            //首字符是否是非终结符，若是则继续递归查找，若不是则直接添加进FIRST集合
            if (formula.containsKey(firstCharacter)){
                searchFirstCharacter(firstCharacter, set);
            }else{
                set.add(firstCharacter);
            }
        }
    }

    //测试
    public static void main(String[] args) {
        FirstCollection firstCollection = new FirstCollection("./src/input.txt");
        //进行消除左递归
        firstCollection.eliminateLeftRecursion.eliminateLeftRecursion();
        firstCollection.createFirstCollection();


        System.out.println();
        for (Map.Entry<String, Set<String>> entry: firstCollection.firstCollectionMap.entrySet()){
            System.out.println(entry.getKey() + "的FIRST集：" + entry.getValue().toString());
        }
    }
}
