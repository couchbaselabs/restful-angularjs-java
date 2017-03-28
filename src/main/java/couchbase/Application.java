package couchbase;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.json.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by nraboy on 1/6/16.
 */

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class Application implements Filter {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}

    @Value("${couchbase_host}")
    private String hostname;

    @Value("${couchbase_bucket}")
    private String bucket;

    @Value("${couchbase_bucket_password}")
    private String password;

    public @Bean
    Cluster cluster() {
        return CouchbaseCluster.create(hostname);
    }

    public @Bean
    Bucket bucket() {
        return cluster().openBucket(bucket, password);
    }

    @RequestMapping(value="/getAll", method= RequestMethod.GET)
    public Object getAll() {
        return Database.getAll(bucket());
    }

    @RequestMapping(value="/get", method= RequestMethod.GET)
    public Object getByDocumentId(@RequestParam String document_id) {
        if(document_id.equals("")) {
            return new ResponseEntity<String>(JsonObject.create().put("message", "A document id is required").toString(), HttpStatus.BAD_REQUEST);
        }
        return Database.getByDocumentId(bucket(), document_id);
    }

    @RequestMapping(value="/delete", method=RequestMethod.POST)
    public Object delete(@RequestBody String json) {
        JsonObject jsonData = JsonObject.fromJson(json);
        if(jsonData.getString("document_id") == null || jsonData.getString("document_id").equals("")) {
            return new ResponseEntity<String>(JsonObject.create().put("message", "A document id is required").toString(), HttpStatus.BAD_REQUEST);
        }
        return Database.delete(bucket(), jsonData.getString("document_id"));
    }

    @RequestMapping(value="/save", method=RequestMethod.POST)
    public Object save(@RequestBody String json) {
        JsonObject jsonData = JsonObject.fromJson(json);
        if(jsonData.getString("firstname") == null || jsonData.getString("firstname").equals("")) {
            return new ResponseEntity<String>(JsonObject.create().put("message", "A firstname is required").toString(), HttpStatus.BAD_REQUEST);
        } else if(jsonData.getString("lastname") == null || jsonData.getString("lastname").equals("")) {
            return new ResponseEntity<String>(JsonObject.create().put("message", "A lastname is required").toString(), HttpStatus.BAD_REQUEST);
        } else if(jsonData.getString("email") == null || jsonData.getString("email").equals("")) {
            return new ResponseEntity<String>(JsonObject.create().put("message", "An email is required").toString(), HttpStatus.BAD_REQUEST);
        }
        return Database.save(bucket(), jsonData);
    }

}
