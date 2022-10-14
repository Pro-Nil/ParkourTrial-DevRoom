package me.pronil.parkour.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import me.pronil.parkour.Parkour;
import me.pronil.parkour.utils.ParkourStorer;
import org.bson.Document;

import java.util.Objects;
import java.util.UUID;

public class DatabaseManager {

    private final Parkour instance;
    private final MongoCollection<Document> collection;

    public DatabaseManager(final Parkour instance) {
        this.instance = instance;
        this.collection = this.connect();
        this.load();
    }

    public MongoCollection<Document> connect() {
        try {

            MongoClient client = MongoClients.create(Objects.requireNonNull(instance.getConfig().getString("mongoURI")));
            MongoDatabase db = client.getDatabase(instance.getName());

            return db.getCollection("ParkourData");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void load() {
        for (Document document : collection.find()) {
            ParkourStorer object = new ParkourStorer(
                    UUID.fromString(document.getString("_id")),
                    document.getString("name"),
                    document.getLong("time")
            );

            instance.getParkourManager().getFinishedParkours().put(object.getPlayer(), object);
        }
    }

    public void save(ParkourStorer object) {
        Document document = new Document("_id", object.getPlayer().toString());

        document.put("name", object.getPlayerName());
        document.put("time", object.getTime());


        collection.replaceOne(
                Filters.eq("_id", object.getPlayer().toString()),
                document,
                new ReplaceOptions().upsert(true)
        );
    }
}