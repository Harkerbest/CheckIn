package cn.harkerBest.checkInBaseServer.dataFormat;

public class SingleChoiceQuestion extends Question{
    public Choice getRightChoice(){
        return new Choice(options.get(correct_options.get(0)), true);
    }
}
