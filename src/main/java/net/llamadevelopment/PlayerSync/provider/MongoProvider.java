package net.llamadevelopment.PlayerSync.provider;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import net.llamadevelopment.PlayerSync.PlayerSync;
import net.llamadevelopment.PlayerSync.utils.ItemAPI;
import net.llamadevelopment.PlayerSync.utils.Manager;
import net.llamadevelopment.PlayerSync.utils.SyncPlayer;
import org.bson.Document;

public class MongoProvider extends Provider {

    private MongoClient mongoClient;
    private MongoCollection<Document> invDB;

    @Override
    public void open(Config c) {
        mongoClient = new MongoClient(new MongoClientURI(c.getString("mongo.uri")));
        invDB = mongoClient.getDatabase(c.getString("mongo.database")).getCollection(c.getString("mongo.collection"));
    }

    @Override
    public void close() {
        mongoClient.close();
    }

    @Override
    public void savePlayer(String uuid, String invString, String ecString, String health, int food, int level, int exp) {
        Document doc = invDB.find(new Document("_id", uuid)).first();
        if (doc != null) {
            Document newSave = new Document("inventory", invString)
                    .append("enderchest", ecString)
                    .append("health", health)
                    .append("food", food)
                    .append("level", level)
                    .append("exp", exp);
            Document update = new Document("$set", newSave);
            invDB.updateOne(new Document("_id", uuid), update);
        } else {
            Document newData = new Document("_id", uuid)
                    .append("inventory", invString)
                    .append("enderchest", ecString)
                    .append("health", "" + health)
                    .append("food", food)
                    .append("level", level)
                    .append("exp", exp);
            invDB.insertOne(newData);
        }
    }



    @Override
    public SyncPlayer getPlayer(Player player) {
        Document doc = invDB.find(new Document("_id", Manager.getUserID(player))).first();
        if (doc != null) {
            return new SyncPlayer(doc.getString("inventory"), doc.getString("enderchest"), Float.parseFloat(doc.getString("health")), doc.getInteger("food"), doc.getInteger("level"), doc.getInteger("exp"));
        } else {
            String inv = "empty";
            String ecInv = "empty";

            if (player.getInventory().getContents().size() > 0) {
                inv = ItemAPI.invToString(player.getInventory());
            }

            if (player.getEnderChestInventory().getContents().size() > 0) {
                ecInv = ItemAPI.invToString(player.getEnderChestInventory());
            }

            savePlayer(Manager.getUserID(player), inv, ecInv, "20.0", 20, 0, 0);
            return new SyncPlayer(inv, ecInv, 20.0f, 20, 0, 0);
        }
    }

    @Override
    public String getName() {
        return "mongodb";
    }
}


