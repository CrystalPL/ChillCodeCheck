package pl.chillcode.logs.storage;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import pl.chillcode.logs.storage.mongo.MongoProvider;
import pl.chillcode.logs.storage.mysql.MYSQLProvider;
import pl.chillcode.logs.storage.sqlite.SQLiteProvider;
import pl.crystalek.crcapi.storage.BaseStorage;
import pl.crystalek.crcapi.storage.impl.mongo.MongoStorage;
import pl.crystalek.crcapi.storage.impl.mysql.MYSQLStorage;
import pl.crystalek.crcapi.storage.impl.sqlite.SQLiteStorage;
import pl.crystalek.crcapi.storage.util.SQLUtil;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public final class Storage {
    BaseStorage<Provider> storage;

    public boolean init() {
        if (!storage.initDatabase()) {
            return false;
        }

        final Provider provider;
        switch (storage.getDatabaseConfig().getStorageType()) {
            case MYSQL:
                provider = new MYSQLProvider(new SQLUtil(((MYSQLStorage) storage.getDatabase()).getDatabase()), getStorage().getDatabaseConfig());
                break;
            case SQLITE:
                provider = new SQLiteProvider(new SQLUtil(((SQLiteStorage) storage.getDatabase()).getDatabase()), getStorage().getDatabaseConfig());
                break;
            case MONGODB:
                provider = new MongoProvider(((MongoStorage) storage.getDatabase()).getDatabase(), getStorage().getDatabaseConfig());
                provider.createTable();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + storage.getDatabaseConfig().getStorageType());
        }

        storage.setProvider(provider);
        return true;
    }
}
