package cn.harkerBest.checkInBaseServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class TrafficLogger implements Serializable {
    Map<String,Map<String,Integer>> dateStringToCounts;
    static class TrafficLoggerHolder{
        static volatile TrafficLogger INSTANCE = new TrafficLogger();
    }
    private TrafficLogger(){
        final Path path = Path.of(TRAFFIC_LOG_SERIALIZED_PATH_STRING);
        try {
            TrafficLogger trafficLogger = (TrafficLogger) new ObjectInputStream(Files.newInputStream(path)).readObject();
            this.dateStringToCounts = trafficLogger.dateStringToCounts;
        } catch (IOException | ClassNotFoundException e) {
            TrafficLoggerHolder.INSTANCE = new TrafficLogger("");
            dateStringToCounts = new HashMap<>();
            HashMap<String, Integer> typeStringToCountMap = new HashMap<>();
            typeStringToCountMap.put("mainCount", 0);
            dateStringToCounts.put(Instant.now().toString().substring(0, 10), typeStringToCountMap);
            try {
                Files.deleteIfExists(path);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    private TrafficLogger(String doNothing){
        //do nothing
    }
    
    public static final String TRAFFIC_LOG_SERIALIZED_PATH_STRING = "..\\..\\trafficLog.serialized";//相对于CheckIn\apache-tomcat-10.1.11\bin
    
    public static TrafficLogger getInstance(){
        return TrafficLoggerHolder.INSTANCE;
    }
    public void logOnce(){
        final String todayString = Instant.now().toString().substring(0, 10);
        if (dateStringToCounts == null){
            dateStringToCounts = new HashMap<>();
            HashMap<String, Integer> typeStringToCountMap = new HashMap<>();
            typeStringToCountMap.put("mainCount",0);
            dateStringToCounts.put(todayString, typeStringToCountMap);
//            dateStringToCounts = Map.of(todayString,Map.of("mainCount",1));
        }
//        if (!dateStringToCounts.containsKey(todayString)){
//            dateStringToCounts.put(todayString,Map.of("mainCount",0));
//        }
        dateStringToCounts.get(todayString).put("mainCount",dateStringToCounts.get(todayString).getOrDefault("mainCount",0)+1);
        Thread flushToFileThread = new Thread(()->{
            Path path = Path.of(TRAFFIC_LOG_SERIALIZED_PATH_STRING);
            try {
                if (!Files.exists(path))
                    Files.createFile(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))) {
                oos.writeObject(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        flushToFileThread.run();
    }
    public String getAmountOf(String dateString,String typeString){
        if (dateStringToCounts == null || !dateStringToCounts.containsKey(dateString) || !dateStringToCounts.get(dateString).containsKey(typeString))
            return "0";
        return String.valueOf(dateStringToCounts.get(dateString).getOrDefault(typeString,0));
    }
}
