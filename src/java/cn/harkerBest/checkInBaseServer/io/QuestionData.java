package cn.harkerBest.checkInBaseServer.io;

import cn.harkerBest.checkInBaseServer.dataFormat.MultipleChoiceQuestion;
import cn.harkerBest.checkInBaseServer.dataFormat.Question;
import cn.harkerBest.checkInBaseServer.dataFormat.SingleChoiceQuestion;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1145141919810L;
    private Map<Integer,Question> hashCodeToQuestions;
    private Map<TypeZone,List<Integer>> typeZoneToHashCodes;
    private QuestionData() {}
    {
        if (hashCodeToQuestions == null) {
            hashCodeToQuestions = new HashMap<>();
        }
        if (typeZoneToHashCodes == null) {
            typeZoneToHashCodes = new HashMap<>();
        }
    }
    public QuestionData readFromFile() throws IOException {
        Path path = Path.of("questions.serialized");
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
            QuestionData questionData = (QuestionData) ois.readObject();
            return questionData;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void addQuestionOfTypeZone(Question question,TypeZone typeZone) {
        hashCodeToQuestions.put(question.hashCode(),question);
        typeZoneToHashCodes.get(typeZone).add(question.hashCode());
    }
    public List<Question> getQuestionsByTypeZone(TypeZone typeZone) {
        return typeZoneToHashCodes.get(typeZone).stream().map(hashCodeToQuestions::get).toList();
    }
    public Question getQuestionByHashCode(int hashCode) {
        return hashCodeToQuestions.get(hashCode);
    }
    public static void main(String[] args) throws IOException {//test
        QuestionData questionSavingUtils = new QuestionData();
        questionSavingUtils.readFromFile();
    }
    public enum TypeZone{
    
    }
}
