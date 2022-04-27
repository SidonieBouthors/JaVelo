package ch.epfl.javelo.gui;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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



    private final static int cacheMemoireSize = 100;
    private final Path cacheDisque;
    private final String nameOfServer;

        LinkedHashMap<Path, Image> cacheMemoire =
            new LinkedHashMap<Path, Image>() {
                protected boolean removeEldestEntry(Map.Entry<Path, Image> eldest)
                {
                    return size() > cacheMemoireSize;
                }
            };


    public TileManager(Path cacheDisque, String nameOfServer) {
        this.nameOfServer=nameOfServer;
        this.cacheDisque=cacheDisque;
        if(!Files.exists(cacheDisque)){
            System.out.println("Make Dir");
            cacheDisque.toFile().mkdir();
        }
    }

    /**
    public static void main(String[] args) throws IOException {
        String  a = "cache";
        TileManager ma = new TileManager(Path.of(a),"https://tile.openstreetmap.org/");
        ma.imageForTileAt(new TileId(19,271725,185422));
    }**/

    /**
     * takes as argument the identity of a tile (of type TileId) and returns its image
     * @param id
     * @return
     */
    public Image imageForTileAt(TileId id) throws IOException {
        Preconditions.checkArgument(TileId.isValid(id.zoom, id.x, id.y));
        Path dirPath = cacheDisque.resolve(String.valueOf(id.zoom))
                                  .resolve(String.valueOf(id.x));
        Path imagePath = dirPath.resolve(id.y+".png");
        //File zoomFile = new File(zoomPath);
        //File xPathFile = new File(xPath);
        File imageFile = imagePath.toFile();


        if (cacheMemoire.containsKey(imagePath)) {
            //System.out.println("from memory cache");
            return cacheMemoire.get(imagePath);
        } else if (Files.exists(imagePath)) {
            //System.out.println("from disk cache");
            InputStream i = new FileInputStream(imageFile);
            Image image = new Image(i);
            cacheMemoire.put(imagePath,image);
            return image;
        } else{
            //System.out.println("from server");
            //creating directories/file that doesn't exist in cache disque
            /*
            if (!zoomFile.exists()) {
                zoomFile.mkdirs();
                xPathFile.mkdirs();
                imageNameFile.createNewFile();
            }else if (!xPathFile.exists()) {
                xPathFile.mkdirs();
                imageNameFile.createNewFile();
            } else if (!imageFile.exists()) {
                imageFile.createNewFile();
            }*/

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
            cacheMemoire.put(imagePath,image);
            return image;

        }


    }

    record TileId(int zoom,int x, int y){
        /**
         * Returns true if and only if they constitute a valid tile identity
         * @param zoom
         * @param x
         * @param y
         * @return
         */
        public  static boolean isValid(int zoom, int x, int y) {
            double i = Math.pow(2, zoom);
            return x >= 0 && x < i && y >= 0 && y < i;
        }
    }

}
