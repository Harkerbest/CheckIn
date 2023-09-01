package cn.harkerBest.checkInBaseServer.io;

import cn.harkerBest.checkInBaseServer.dataFormat.Question;
import cn.harkerBest.checkInBaseServer.dataFormat.QuestionList;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionData implements Serializable {
    @Serial
    private static final long serialVersionUID = -1145141919810L;
    private Map<String,Question> hashCodeToQuestions;
    private Map<TypeZone,List<String>> typeZoneToHashCodes;
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
        Path path = Path.of(".\\..\\..\\questions.serialized");//相对于CheckIn\apache-tomcat-10.1.11\bin
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
        hashCodeToQuestions.put(MD5Of(question),question);
        if (!typeZoneToHashCodes.containsKey(typeZone)) {
            typeZoneToHashCodes.put(typeZone, new ArrayList<>());
        }
        typeZoneToHashCodes.get(typeZone).add(MD5Of(question));
    }
    public List<Question> getQuestionsByTypeZone(TypeZone typeZone) {
        return typeZoneToHashCodes.get(typeZone).stream().map(hashCodeToQuestions::get).toList();
    }
    public Question getQuestionByHashCode(String hashCode) {
        return hashCodeToQuestions.get(hashCode);
    }
    public static void main(String[] args) throws IOException {//test
        QuestionData questionSavingUtils = QuestionData.getInstance();
        questionSavingUtils.readFromFile();
        System.out.println();
    }
    
    public static String MD5Of(Question question) {
        String input = question.toString();
        if(input == null || input.isEmpty()) {
            return null;
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(input.getBytes());
            byte[] byteArray = md5.digest();
            
            BigInteger bigInt = new BigInteger(1, byteArray);
            // 参数16表示16进制
            StringBuilder result = new StringBuilder(bigInt.toString(16));
            // 不足32位高位补零
            while (result.length() < 32) {
                result.insert(0, "0");
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            // impossible
        }
        return null;
    }
    
    public enum TypeZone{
        UNDEFINED
    }
}
