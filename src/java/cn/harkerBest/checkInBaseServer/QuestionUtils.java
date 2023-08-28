package cn.harkerBest.checkInBaseServer;

import cn.harkerBest.checkInBaseServer.dataFormat.Choice;
import cn.harkerBest.checkInBaseServer.dataFormat.MultipleChoiceQuestion;
import cn.harkerBest.checkInBaseServer.dataFormat.Question;
import cn.harkerBest.checkInBaseServer.dataFormat.SingleChoiceQuestion;
import com.google.gson.Gson;
import org.json.JSONObject;
import org.json.XML;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class QuestionUtils {
    public static void main(String[] args) throws IOException {
        File formFile = new File("F:\\programTools\\CheckIn\\web\\form.xml");
        JSONObject jsonObject = XML.toJSONObject(Files.readString(formFile.toPath()));
        System.out.println(jsonObject.toString(4));
        Map<String,Object> form = (Map<String,Object>) new Gson().fromJson(jsonObject.toString(), Map.class).get("body");
        List<Object> questionsDiv = (List<Object>) ((Map<String,Object>)form.get("form")).get("div");
        List<Question> questions = new ArrayList<>();
        for (Object div : questionsDiv) {
            Map<String,Object> questionDiv = (Map<String,Object>) div;
            Map<String,Object> options = (Map<String,Object>) questionDiv.get("div");
            List<Object> optionsDiv = (List<Object>) options.get("div");
            Map<String,Object> firstOption = (Map<String,Object>) optionsDiv.get(0);
            Map<String,Object> input = (Map<String,Object>) firstOption.get("input");
            String questionType = (String) input.get("type");
            System.out.println(questionType);
            if (questionDiv.get("p") instanceof String){
                if (questionType.equals("radio")){
                    Question singleChoiceQuestion = new SingleChoiceQuestion() {
                        {
                            question_content = (String) (questionDiv.get("p"));
                        }
                        @Override
                        public Choice getRightChoice() {
                            return null;
                        }
                        
                        @Override
                        public String getQuestionContent() {
                            return question_content;
                        }
                    };
                    questions.add(singleChoiceQuestion);
                } else if (questionType.equals("checkbox")){
                    Question multipleChoiceQuestion = new MultipleChoiceQuestion() {
                        {
                            question_content = (String) (questionDiv.get("p"));
                        }
                        @Override
                        public List<Choice> getRightChoices() {
                            return null;
                        }
                        
                        @Override
                        public String getQuestionContent() {
                            return question_content;
                        }
                    };
                    questions.add(multipleChoiceQuestion);
                }
            }
        }
        Gson gson = new Gson();
        System.out.println(gson.toJson(questions, List.class));
    }
    public String toHtmlForm(){
        return "";
    }
}
