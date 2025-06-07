package cz.vse.pexeso.main;

import java.util.ArrayList;

import cz.vse.pexeso.common.message.payload.GameListPayload;
import cz.vse.pexeso.common.message.payload.SendableGame;
import cz.vse.pexeso.game.Game;

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
                        Game g = gm.getValue();
                        games.add(new SendableGame(g.getId(), g.getName(), Long.parseLong(g.getCreatorId()), g.getCreatorName(), g.isStarted(), g.getPlayersCapacity(), g.getCardCount()));
                    }

                    var data = new GameListPayload(games);

                    // send users already in games
                    for (var gr : this.gsr.getAllGames().entrySet()) {
                        Game g = gr.getValue();
                        g.sendToAll(MessageFactory.getGsrUpdateMessage(data));
                    }

                    //send to users connected to default port (not in any game)
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
