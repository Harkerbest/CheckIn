package cn.harkerBest.checkInBaseServer.io;

import cn.harkerBest.checkInBaseServer.dataFormat.Question;
import cn.harkerBest.checkInBaseServer.dataFormat.QuestionList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
    private static class QuestionDataHolder {
        public static QuestionData INSTANCE = new QuestionData();
    }
    public static QuestionData getInstance() {
        
        return QuestionDataHolder.INSTANCE;
    }
    public void readFromFile() throws IOException {
        Path path = Path.of("questions.serialized");
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
            QuestionData questionData = (QuestionData) ois.readObject();
            QuestionDataHolder.INSTANCE.hashCodeToQuestions = questionData.hashCodeToQuestions;
            QuestionDataHolder.INSTANCE.typeZoneToHashCodes = questionData.typeZoneToHashCodes;
        } catch (Exception e) {
            try {
                Files.deleteIfExists(path);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }
    public void appendToFile(QuestionList questionList) {
        Path path = Path.of("questions.serialized");
        
        try {
            QuestionData questionData;
            if (Files.exists(path)) {
                readFromFile();
                questionData = QuestionDataHolder.INSTANCE;
            } else {
                questionData = new QuestionData();
            }
            for (Question question : questionList) {
                questionData.addQuestionOfTypeZone(question, question.getTypeZone());
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
                oos.writeObject(questionData);
            }
        } catch (IOException e) {
            try {
                Files.deleteIfExists(path);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }
    public void addQuestionOfTypeZone(Question question,TypeZone typeZone) {
        hashCodeToQuestions.put(question.hashCode(),question);
        if (!typeZoneToHashCodes.containsKey(typeZone)) {
            typeZoneToHashCodes.put(typeZone, new ArrayList<>());
        } else {
            typeZoneToHashCodes.get(typeZone).add(question.hashCode());
        }
    }
    public List<Question> getQuestionsByTypeZone(TypeZone typeZone) {
        return typeZoneToHashCodes.get(typeZone).stream().map(hashCodeToQuestions::get).toList();
    }
    public Question getQuestionByHashCode(int hashCode) {
        return hashCodeToQuestions.get(hashCode);
    }
    public static void main(String[] args) throws IOException {//test
        QuestionData questionSavingUtils = QuestionData.getInstance();
        questionSavingUtils.readFromFile();
        System.out.println();
    }
    public enum TypeZone{
        UNDEFINED
    }
}
