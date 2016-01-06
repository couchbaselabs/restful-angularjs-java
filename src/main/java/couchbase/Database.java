package couchbase;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.*;
import com.couchbase.client.java.query.consistency.ScanConsistency;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by nraboy on 1/6/16.
 */
public class Database {

    private Database() { }

    /*
     * Get all documents in the bucket
     */
    public static List<Map<String, Object>> getAll(final Bucket bucket) {
        String queryStr = "SELECT META(users).id, firstname, lastname, email " +
                       "FROM `" + bucket.name() + "` AS users";
        N1qlQueryResult queryResult = bucket.query(N1qlQuery.simple(queryStr, N1qlParams.build().consistency(ScanConsistency.REQUEST_PLUS)));
        return extractResultOrThrow(queryResult);
    }

    /*
     * Get a particular document by its id
     */
    public static List<Map<String, Object>> getByDocumentId(final Bucket bucket, String documentId) {
        String queryStr = "SELECT firstname, lastname, email " +
                       "FROM `" + bucket.name() + "` AS users " +
                       "WHERE META(users).id = $1";
        ParameterizedN1qlQuery query = ParameterizedN1qlQuery.parameterized(queryStr, JsonArray.create().add(documentId));
        N1qlQueryResult queryResult = bucket.query(query);
        return extractResultOrThrow(queryResult);
    }

    /*
     * Delete records based on document id
     */
    public static List<Map<String, Object>> delete(final Bucket bucket, String documentId) {
        String queryStr = "DELETE " +
                "FROM `" + bucket.name() + "` AS users " +
                "WHERE META(users).id = $1";
        ParameterizedN1qlQuery query = ParameterizedN1qlQuery.parameterized(queryStr, JsonArray.create().add(documentId));
        N1qlQueryResult queryResult = bucket.query(query);
        return extractResultOrThrow(queryResult);
    }

    /*
     * Create or replace documents using an UPSERT
     */
    public static List<Map<String, Object>> save(final Bucket bucket, JsonObject data) {
        String documentId = !data.getString("document_id").equals("") ? data.getString("document_id") : UUID.randomUUID().toString();
        String queryStr = "UPSERT INTO `" + bucket.name() + "` (KEY, VALUE) VALUES " +
                "($1, {'firstname': $2, 'lastname': $3, 'email': $4})";
        JsonArray parameters = JsonArray.create()
                .add(documentId)
                .add(data.getString("firstname"))
                .add(data.getString("lastname"))
                .add(data.getString("email"));
        ParameterizedN1qlQuery query = ParameterizedN1qlQuery.parameterized(queryStr, parameters);
        N1qlQueryResult queryResult = bucket.query(query);
        return extractResultOrThrow(queryResult);
    }

    /*
     * Convert query results into a more friendly List object
     */
    private static List<Map<String, Object>> extractResultOrThrow(N1qlQueryResult result) {
        if (!result.finalSuccess()) {
            throw new DataRetrievalFailureException("Query error: " + result.errors());
        }
        List<Map<String, Object>> content = new ArrayList<Map<String, Object>>();
        for (N1qlQueryRow row : result) {
            content.add(row.value().toMap());
        }
        return content;
    }

}
