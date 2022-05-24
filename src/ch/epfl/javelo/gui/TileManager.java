package ch.epfl.javelo.gui;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import ch.epfl.javelo.Preconditions;
import javafx.scene.image.Image;
/**
 * TileManager
 *
 * @author Sidonie Bouthors (343678)
 * @author François Théron (346077)
 */
public final  class TileManager {

    private final static int CACHE_MEMOIRE_SIZE = 100;
    private final Path diskCache;
    private final String nameOfServer;
    private final LinkedHashMap<Path, Image> memoryCache =
            new LinkedHashMap<Path, Image>() {
                protected boolean removeEldestEntry(Map.Entry<Path, Image> eldest)
                {
                    return size() > CACHE_MEMOIRE_SIZE;
                }
            };

    /**
     * Constructor of  a Tile Manager
     * @param diskCache Place where you want to store your cache
     * @param nameOfServer Name of the server you want to extract data
     */
    public TileManager(Path diskCache, String nameOfServer) {
        this.nameOfServer=nameOfServer;
        this.diskCache =diskCache;
        if(!Files.exists(diskCache)){
            diskCache.toFile().mkdir();
        }
    }


    /**
     * Returns the image of the tile of given ID
     * @param id    : ID of the tile
     * @return image of the tile
     */
    public Image imageForTileAt(TileId id) throws IOException {
        Preconditions.checkArgument(TileId.isValid(id.zoom, id.x, id.y));
        Path dirPath = diskCache.resolve(String.valueOf(id.zoom))
                                  .resolve(String.valueOf(id.x));
        Path imagePath = dirPath.resolve(id.y+".png");
        File imageFile = imagePath.toFile();

        if (memoryCache.containsKey(imagePath)) {
            return memoryCache.get(imagePath);
        }
        else if (Files.exists(imagePath)) {
            InputStream i = new FileInputStream(imageFile);
            Image image = new Image(i);
            memoryCache.put(imagePath,image);
            return image;
        }
        else{
            Files.createDirectories(dirPath);
            String urlString = "https://"+nameOfServer+"/"+id.zoom+"/"+id.x+"/"+id.y+".png";
            URL u = new URL(urlString);
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");

            try (InputStream i = c.getInputStream(); OutputStream a = new FileOutputStream(imageFile);) {
                i.transferTo(a);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            InputStream i = new FileInputStream(imageFile);
            Image image = new Image(i);
            memoryCache.put(imagePath,image);
            return image;
        }
    }

    /**
     * TileId with the given zoom, x and y coordinates
     */
    public record TileId(int zoom,int x, int y){

        /**
         * Returns true if and only if the parameters constitute a valid tile identity
         * @param zoom  : zoom level
         * @param x     : x coordinate
         * @param y     : y coordinate
         * @return
         */
        public  static boolean isValid(int zoom, int x, int y) {
            double i = Math.pow(2, zoom);
            return x >= 0 && x < i && y >= 0 && y < i;
        }
    }
}
