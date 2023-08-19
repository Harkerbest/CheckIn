package cn.harkerBest.checkInBaseServer.dataFormat;

import java.util.List;

public abstract class MultipleChoiceQuestion extends Question{
    abstract public List<Choice> getRightChoices();
}
