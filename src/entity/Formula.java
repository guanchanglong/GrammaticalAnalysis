package entity;

/**
 * @Author 小关同学
 * @Create 2022/5/3 23:55
 */
public class Formula {
    //右侧式子
    private String right;
    //左侧非终结符
    private String left;
    //是否已经进行过间接消除左递归
    private boolean recursionFlag;

    public Formula() {
    }

    public Formula(String left, String right) {
        this.right = right;
        this.left = left;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public boolean isRecursionFlag() {
        return recursionFlag;
    }

    public void setRecursionFlag(boolean recursionFlag) {
        this.recursionFlag = recursionFlag;
    }

    @Override
    public String toString() {
        return right + "——>" + left;
    }
}
