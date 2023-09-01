package cn.harkerBest.checkInBaseServer.dataFormat;

public class Choice {
    private String content;
    private boolean correct;
    public Choice(String content, boolean correct){
        this.content = content;
        this.correct = correct;
    }
    public String toString(){
        return content;
    }
    public boolean isCorrect(){
        return correct;
    }
}
