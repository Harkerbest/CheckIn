package cn.harkerBest.checkInBaseServer.dataFormat;

import cn.harkerBest.checkInBaseServer.io.QuestionData;

import java.io.Serializable;
import java.util.List;

public class Question implements Serializable {
    protected String question_content;
    protected List<String> options;
    protected List<Integer> correct_options;
    protected QuestionData.TypeZone type_zone = QuestionData.TypeZone.UNDEFINED;
    public String getQuestionContent(){
        return question_content;
    }
    public QuestionData.TypeZone getTypeZone(){
        return type_zone;
    }
}
