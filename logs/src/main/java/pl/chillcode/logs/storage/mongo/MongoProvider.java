package pl.chillcode.logs.storage.mongo;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.bukkit.entity.Player;
import pl.chillcode.check.model.CheckResult;
import pl.chillcode.logs.log.Log;
import pl.chillcode.logs.log.MessageLog;
import pl.chillcode.logs.storage.Provider;
import pl.crystalek.crcapi.database.config.DatabaseConfig;
import pl.crystalek.crcapi.lib.bson.Document;
import pl.crystalek.crcapi.lib.mongodb.client.FindIterable;
import pl.crystalek.crcapi.lib.mongodb.client.MongoCollection;
import pl.crystalek.crcapi.lib.mongodb.client.MongoCursor;
import pl.crystalek.crcapi.lib.mongodb.client.MongoDatabase;

import java.util.*;
import java.util.regex.Pattern;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class MongoProvider implements Provider {
    final MessageLogDocumentComparator messageLogComparator = new MessageLogDocumentComparator();
    final MongoDatabase mongoDatabase;
    final DatabaseConfig databaseConfig;
    MongoCollection<Document> userCollection;
    MongoCollection<Document> logCollection;

    @Override
    public void createUser(final Player player) {
        final Document userDocument = new Document()
                .append("uuid", player.getUniqueId().toString())
                .append("nickname", player.getName());

        userCollection.insertOne(userDocument);
    }

    @Override
    public Optional<UUID> getPlayerUUID(final String nick) {
        final Document playerUUIDDocument = new Document("nickname", Pattern.compile(".*" + nick + "*", Pattern.CASE_INSENSITIVE));
        final Document foundUserDocument = userCollection.find(playerUUIDDocument).first();

        return foundUserDocument == null ? Optional.empty() : Optional.of(UUID.fromString(foundUserDocument.get("uuid", String.class)));
    }

    @Override
    public Optional<String> getPlayerNickname(final UUID uuid) {
        final Document playerUUIDDocument = new Document("uuid", uuid.toString());
        final Document foundUserDocument = userCollection.find(playerUUIDDocument).first();

        return foundUserDocument == null ? Optional.empty() : Optional.of(foundUserDocument.get("nickname", String.class));
    }

    @Override
    public Optional<List<Log>> getPlayerLogs(final UUID uuid) {
        final Document logDocumentQuery = new Document("player_uuid", uuid.toString());
        final FindIterable<Document> logDocumentList = logCollection.find(logDocumentQuery);

        @Cleanup final MongoCursor<Document> iterator = logDocumentList.iterator();
        if (!iterator.hasNext()) {
            return Optional.empty();
        }

        final List<Log> logList = new ArrayList<>();
        final CheckResult[] checkResults = CheckResult.values();

        do {
            final Document logDocument = iterator.next();
            final Long checkStartTime = logDocument.get("check_start_time", Long.class);
            final UUID playerUUID = UUID.fromString(logDocument.get("player_uuid", String.class));
            final UUID adminUUID = UUID.fromString(logDocument.get("admin_uuid", String.class));
            final CheckResult checkResult = checkResults[logDocument.get("check_result", Integer.class)];
            final Long checkEndTime = logDocument.get("check_end_time", Long.class);

            final LinkedList<MessageLog> messageLogList = new LinkedList<>();
            final List<Document> messageLogDocumentList = logDocument.getList("messages_log", Document.class);
            if (messageLogDocumentList != null) {
                messageLogDocumentList.sort(messageLogComparator);

                for (final Document messageLogDocument : messageLogDocumentList) {
                    final String message = messageLogDocument.get("message", String.class);
                    final UUID senderUUID = UUID.fromString(messageLogDocument.get("sender_uuid", String.class));
                    final Long sentTime = messageLogDocument.get("sent_time", Long.class);
                    final Boolean adminMessage = messageLogDocument.get("admin_message", Boolean.class);

                    messageLogList.add(new MessageLog(message, senderUUID, sentTime, adminMessage));
                }
            }

            logList.add(new Log(checkStartTime, playerUUID, adminUUID, checkResult, checkEndTime, messageLogList));

        } while (iterator.hasNext());

        return Optional.of(logList);
    }

    @Override
    public void saveLog(final Log log) {
        final Document logDocument = new Document()
                .append("check_start_time", log.getCheckStartTime())
                .append("player_uuid", log.getPlayerUUID().toString())
                .append("admin_uuid", log.getAdminUUID().toString())
                .append("check_result", log.getCheckResult().ordinal())
                .append("check_end_time", log.getCheckEndTime());

        final LinkedList<MessageLog> messageLogList = log.getMessageLogList();
        if (!messageLogList.isEmpty()) {
            final List<Document> messageLogDocumentList = new ArrayList<>();

            for (final MessageLog messageLog : messageLogList) {
                final Document messageLogDocument = new Document()
                        .append("message", messageLog.getMessage())
                        .append("sender_uuid", messageLog.getSenderUUID().toString())
                        .append("sent_time", messageLog.getSentTime())
                        .append("admin_message", messageLog.isAdminMessage());

                messageLogDocumentList.add(messageLogDocument);
            }

            logDocument.append("messages_log", messageLogDocumentList);
        }

        logCollection.insertOne(logDocument);
    }

    @Override
    public void createTable() {
        this.userCollection = mongoDatabase.getCollection(String.format("%suser", databaseConfig.getPrefix()));
        this.logCollection = mongoDatabase.getCollection(String.format("%slogs", databaseConfig.getPrefix()));
    }
}
