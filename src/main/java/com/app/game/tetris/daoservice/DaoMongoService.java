package com.app.game.tetris.daoservice;

public interface DaoMongoService {
    void runMongoServer();
    void prepareMongoDB();
    boolean isMongoDBNotEmpty();
    void loadShotFromMongodb(String playerName, String fileName);
    void cleanMongodb(String playerName, String fileName);
    void loadSnapShotIntoMongodb(String playerName, String fileName);
    void makeDesktopSnapshot(String fileNameDetail);
}
