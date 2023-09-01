package cn.harkerBest.checkInBaseServer.dataFormat;

import cn.harkerBest.checkInBaseServer.io.QuestionData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {
    protected String question_content;
    protected List<String> options;
    protected List<Integer> correct_options;
    protected QuestionData.TypeZone type_zone = QuestionData.TypeZone.UNDEFINED;
    public String getContent(){
        return question_content;
    }
    public List<String> getOptionsString(){
        return options;
    }
    public List<Choice> getChoices(){
        List<Choice> choices = new ArrayList<>();
        for (String option : options) {
            if (correct_options.contains(options.indexOf(option)))
                choices.add(new Choice(option, true));
            else {
                choices.add(new Choice(option, false));
            }
        }
        return choices;
    }
    public QuestionData.TypeZone getTypeZone(){
        return type_zone;
    }
    
    public String toString(){
        return "{"+question_content+","+options+","+correct_options+","+type_zone+"}";
    }
}
