

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

public class MyPlayer {

    private Player player;

    private BufferedInputStream stream;

    public void play(String file)
    throws JavaLayerException, FileNotFoundException {
        stream = new BufferedInputStream((new FileInputStream(file)));
        player = new Player(stream);
        player.play();
    }

    public void close() throws IOException {
        if (player != null) {
            player.close();
        }
        if (stream != null) {
            stream.close();
        }
    }

}