package cn.harkerBest.checkInBaseServer;

import cn.harkerBest.checkInBaseServer.dataFormat.QuestionList;
import cn.harkerBest.checkInBaseServer.io.QuestionData;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonToClassTranslator {
    public static QuestionList translate(Path path) throws IOException {
        Gson gson = new Gson();
        String json = Files.readString(path);
        return gson.fromJson(json, QuestionList.class);
    }
    
    public static void main(String[] args) {
        try {
            QuestionList questionList = JsonToClassTranslator.translate(Path.of("questions.json"));
            QuestionData questionData = QuestionData.getInstance();
            questionData.appendToFile(questionList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
