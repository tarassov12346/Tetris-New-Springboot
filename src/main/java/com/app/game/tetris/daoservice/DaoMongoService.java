package com.app.game.tetris.daoservice;

public interface DaoMongoService {
    String MongoPath="C:\\mongodb\\bin\\mongod.exe";
    void runMongoServer();
    void loadMugShotFromMongodb(String playerName);
    void loadSnapShotFromMongodb(String playerName,String fileName);
    void cleanMongodb(String playerName, String fileName);
    void loadSnapShotIntoMongodb(String playerName, String fileName);
    void makeDesktopSnapshot(String fileNameDetail);
}
