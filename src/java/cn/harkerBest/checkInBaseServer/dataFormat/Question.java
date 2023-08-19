package cn.harkerBest.checkInBaseServer.dataFormat;

import java.io.Serializable;

public abstract class Question implements Serializable {
    protected String questionString;
    abstract public String getQuestionString();
}
