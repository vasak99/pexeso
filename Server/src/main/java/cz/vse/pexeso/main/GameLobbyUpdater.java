package cz.vse.pexeso.main;

import java.util.ArrayList;

import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.common.message.payload.SendableGame;

public class GameLobbyUpdater implements Runnable {

    public boolean keepAlive = true;

    private GameServerRuntime gsr;

    public GameLobbyUpdater(GameServerRuntime gsr) {
        this.gsr = gsr;
    }

    @Override
    public void run() {
        while (this.keepAlive) {
            synchronized(this.gsr) {
                if(this.gsr != null) {
                    var games = new ArrayList<SendableGame>();
                    for(var gm : this.gsr.getAllGames().entrySet()) {
                        games.add(new SendableGame(gm.getValue().getName(), gm.getValue().getId()));
                    }

                    var data = new GameListPayload(games);

                    this.gsr.sendToAll(MessageFactory.getGsrUpdateMessage(data));

                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {}
                }
            }
        }
    }

    public void terminate() {
        this.keepAlive = false;
    }

}
