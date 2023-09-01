package cn.harkerBest.checkInBaseServer.dataFormat;

public class Option {
    private String optionContent;
    private boolean isCorrect;

    public Option(String optionContent, boolean isCorrect) {
        this.optionContent = optionContent;
        this.isCorrect = isCorrect;
    }

    public String getOptionContent() {
        return optionContent;
    }

    public boolean isCorrect() {
        return isCorrect;
    }
}
