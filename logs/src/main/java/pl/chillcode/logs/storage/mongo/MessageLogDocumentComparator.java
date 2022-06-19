package pl.chillcode.logs.storage.mongo;

import pl.crystalek.crcapi.lib.bson.Document;

import java.util.Comparator;

final class MessageLogDocumentComparator implements Comparator<Document> {

    @Override
    public int compare(final Document messageLogDocument1, final Document messageLogDocument2) {
        return Long.compare(messageLogDocument1.get("sent_time", Long.class), messageLogDocument2.get("sent_time", Long.class));
    }
}
