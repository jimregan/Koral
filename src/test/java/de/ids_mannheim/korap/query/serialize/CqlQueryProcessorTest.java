package de.ids_mannheim.korap.query.serialize;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.z3950.zing.cql.CQLParseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.ids_mannheim.korap.query.serialize.CqlQueryProcessor;
import de.ids_mannheim.korap.query.serialize.Cosmas2QueryProcessor;


public class CqlQueryProcessorTest {

    String query;
    String version = "1.2";
    ObjectMapper mapper = new ObjectMapper();


    @Test
    public void testExceptions () throws CQLParseException, IOException {
        query = "(Kuh) prox (Germ) ";
        try {
            CqlQueryProcessor cqlTree = new CqlQueryProcessor(query, version);
        }
        catch (Exception e) {
            int errorCode = Integer.parseInt(e.getMessage().split(":")[0]
                    .replace("SRU diagnostic ", ""));
            assertEquals(48, errorCode);
        }

        query = "(Kuh) or/rel.combine=sum (Germ) ";
        try {
            CqlQueryProcessor cqlTree = new CqlQueryProcessor(query, version);
        }
        catch (Exception e) {
            int errorCode = Integer.parseInt(e.getMessage().split(":")[0]
                    .replace("SRU diagnostic ", ""));
            assertEquals(20, errorCode);
        }

        query = "dc.title any Germ ";
        try {
            CqlQueryProcessor cqlTree = new CqlQueryProcessor(query, version);
        }
        catch (Exception e) {
            int errorCode = Integer.parseInt(e.getMessage().split(":")[0]
                    .replace("SRU diagnostic ", ""));
            assertEquals(16, errorCode);
        }

        query = "cql.serverChoice any Germ ";
        try {
            CqlQueryProcessor cqlTree = new CqlQueryProcessor(query, version);
        }
        catch (Exception e) {
            int errorCode = Integer.parseInt(e.getMessage().split(":")[0]
                    .replace("SRU diagnostic ", ""));
            assertEquals(19, errorCode);
        }

        query = "";
        try {
            CqlQueryProcessor cqlTree = new CqlQueryProcessor(query, version);
        }
        catch (Exception e) {
            int errorCode = Integer.parseInt(e.getMessage().split(":")[0]
                    .replace("SRU diagnostic ", ""));
            assertEquals(27, errorCode);
        }
    }


    @Test
    public void testAndQuery () throws CQLParseException, IOException,
            Exception {
        query = "(Sonne) and (scheint)";
        String jsonLd = "{@type : koral:group, operation : operation:sequence, inOrder : false,"
                + "distances:[ "
                + "{@type : koral:distance, key : s, min : 0, max : 0 } ],"
                + "operands : ["
                + "{@type : koral:token, wrap : {@type : koral:term,key : Sonne, layer : orth, match : match:eq}},"
                + "{@type : koral:token,wrap : {@type : koral:term,key : scheint,layer : orth,match : match:eq}"
                + "}]}";

        CqlQueryProcessor cqlTree = new CqlQueryProcessor(query, version);
        String serializedQuery = mapper.writeValueAsString(cqlTree
                .getRequestMap().get("query"));
        assertEquals(jsonLd.replace(" ", ""), serializedQuery.replace("\"", ""));
        //			/System.out.println(serializedQuery);
        //CosmasTree ct = new CosmasTree("Sonne und scheint");
        //serializedQuery = mapper.writeValueAsString(ct.getRequestMap().get("query"));
        //assertEquals(jsonLd.replace(" ", ""), serializedQuery.replace("\"", ""));
    }


    @Test
    public void testBooleanQuery () throws CQLParseException, IOException,
            Exception {
        query = "((Sonne) or (Mond)) and (scheint)";
        String jsonLd = "{@type:koral:group, operation:operation:sequence, inOrder : false, distances:["
                + "{@type:koral:distance, key:s, min:0, max:0}"
                + "], operands:["
                + "{@type:koral:group, operation:operation:or, operands:["
                + "{@type:koral:token, wrap:{@type:koral:term, key:Sonne, layer:orth, match:match:eq}},"
                + "{@type:koral:token, wrap:{@type:koral:term, key:Mond, layer:orth, match:match:eq}}"
                + "]},"
                + "{@type:koral:token, wrap:{@type:koral:term, key:scheint, layer:orth, match:match:eq}}"
                + "]}";
        CqlQueryProcessor cqlTree = new CqlQueryProcessor(query, version);
        String serializedQuery = mapper.writeValueAsString(cqlTree
                .getRequestMap().get("query"));
        assertEquals(jsonLd.replace(" ", ""), serializedQuery.replace("\"", ""));


        query = "(scheint) and ((Sonne) or (Mond))";
        jsonLd = "{@type:koral:group, operation:operation:sequence, inOrder : false, distances:["
                + "{@type:koral:distance, key:s, min:0, max:0}"
                + "], operands:["
                + "{@type:koral:token, wrap:{@type:koral:term, key:scheint, layer:orth, match:match:eq}},"
                + "{@type:koral:group, operation:operation:or, operands:["
                + "{@type:koral:token, wrap:{@type:koral:term, key:Sonne, layer:orth, match:match:eq}},"
                + "{@type:koral:token, wrap:{@type:koral:term, key:Mond, layer:orth, match:match:eq}}"
                + "]}" + "]}";
        cqlTree = new CqlQueryProcessor(query, version);
        serializedQuery = mapper.writeValueAsString(cqlTree.getRequestMap()
                .get("query"));
        assertEquals(jsonLd.replace(" ", ""), serializedQuery.replace("\"", ""));

    }


    @Test
    public void testOrQuery () throws CQLParseException, IOException, Exception {
        query = "(Sonne) or (Mond)";
        String jsonLd = "{@type:koral:group, operation:operation:or, operands:["
                + "{@type:koral:token, wrap:{@type:koral:term, key:Sonne, layer:orth, match:match:eq}},"
                + "{@type:koral:token, wrap:{@type:koral:term, key:Mond, layer:orth, match:match:eq}}"
                + "]}";

        CqlQueryProcessor cqlTree = new CqlQueryProcessor(query, version);
        String serializedQuery = mapper.writeValueAsString(cqlTree
                .getRequestMap().get("query"));
        assertEquals(jsonLd.replace(" ", ""), serializedQuery.replace("\"", ""));

        query = "(\"Sonne scheint\") or (Mond)";
        jsonLd = "{@type:koral:group, operation:operation:or, operands:["
                + "{@type:koral:group, operation:operation:sequence, operands:["
                + "{@type:koral:token, wrap:{@type:koral:term, key:Sonne, layer:orth, match:match:eq}},"
                + "{@type:koral:token, wrap:{@type:koral:term, key:scheint, layer:orth, match:match:eq}}"
                + "]},"
                + "{@type:koral:token, wrap:{@type:koral:term, key:Mond, layer:orth, match:match:eq}}"
                + "]}";

        cqlTree = new CqlQueryProcessor(query, version);
        serializedQuery = mapper.writeValueAsString(cqlTree.getRequestMap()
                .get("query"));
        assertEquals(jsonLd.replace(" ", ""), serializedQuery.replace("\"", ""));

        query = "(\"Sonne scheint\") or (\"Mond scheint\")";
        jsonLd = "{@type:koral:group, operation:operation:or, operands:["
                + "{@type:koral:group, operation:operation:sequence, operands:["
                + "{@type:koral:token, wrap:{@type:koral:term, key:Sonne, layer:orth, match:match:eq}},"
                + "{@type:koral:token, wrap:{@type:koral:term, key:scheint, layer:orth, match:match:eq}}"
                + "]},"
                + "{@type:koral:group, operation:operation:sequence, operands:["
                + "{@type:koral:token, wrap:{@type:koral:term, key:Mond, layer:orth, match:match:eq}},"
                + "{@type:koral:token, wrap:{@type:koral:term, key:scheint, layer:orth, match:match:eq}}"
                + "]}" + "]}";
        cqlTree = new CqlQueryProcessor(query, version);
        serializedQuery = mapper.writeValueAsString(cqlTree.getRequestMap()
                .get("query"));
        assertEquals(jsonLd.replace(" ", ""), serializedQuery.replace("\"", ""));
    }


    @Test
    public void testTermQuery () throws CQLParseException, IOException,
            Exception {
        query = "Sonne";
        String jsonLd = "{@type:koral:token, wrap:{@type:koral:term, key:Sonne, layer:orth, match:match:eq}}";
        CqlQueryProcessor cqlTree = new CqlQueryProcessor(query, version);
        String serializedQuery = mapper.writeValueAsString(cqlTree
                .getRequestMap().get("query"));
        assertEquals(jsonLd.replace(" ", ""), serializedQuery.replace("\"", ""));
    }


    @Test
    public void testPhraseQuery () throws CQLParseException, IOException,
            Exception {
        query = "\"der Mann\"";
        String jsonLd = "{@type:koral:group, operation:operation:sequence, operands:["
                + "{@type:koral:token, wrap:{@type:koral:term, key:der, layer:orth, match:match:eq}},"
                + "{@type:koral:token, wrap:{@type:koral:term, key:Mann, layer:orth, match:match:eq}}"
                + "]}";

        CqlQueryProcessor cqlTree = new CqlQueryProcessor(query, version);
        String serializedQuery = mapper.writeValueAsString(cqlTree
                .getRequestMap().get("query"));
        assertEquals(jsonLd.replace(" ", ""), serializedQuery.replace("\"", ""));


        query = "der Mann schläft";
        jsonLd = "{@type:koral:group, operation:operation:sequence, operands:["
                + "{@type:koral:token, wrap:{@type:koral:term, key:der, layer:orth, match:match:eq}},"
                + "{@type:koral:token, wrap:{@type:koral:term, key:Mann, layer:orth, match:match:eq}},"
                + "{@type:koral:token, wrap:{@type:koral:term, key:schläft, layer:orth, match:match:eq}}"
                + "]}";

        cqlTree = new CqlQueryProcessor(query, version);
        serializedQuery = mapper.writeValueAsString(cqlTree.getRequestMap()
                .get("query"));
        assertEquals(jsonLd.replace(" ", ""), serializedQuery.replace("\"", ""));
    }
}
