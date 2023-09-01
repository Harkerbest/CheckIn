package cn.harkerBest.checkInBaseServer.dataFormat;

import java.util.ArrayList;
import java.util.List;

public class MultipleChoiceQuestion extends Question{
    public List<Choice> getRightChoices(){
        List<Choice> choices = new ArrayList<>();
        for (Integer correct_option : correct_options) {
            choices.add(new Choice(options.get(correct_option), true));
        }
        return choices;
    }
}
