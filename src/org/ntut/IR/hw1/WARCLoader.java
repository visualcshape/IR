package org.ntut.IR.hw1;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.sis.internal.jdk7.StandardCharsets;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import javax.print.Doc;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Created by Vodalok on 2016/3/26.
 */

public class WARCLoader {
    private static final String WARC_REGEX_START_STRING = "WARC/([\\d].[\\d][\\d]*)";
    private static final int BUFFER_SIZE = 65535;

    public enum WARCType{
        INFO,
        RESPONSE,
        OTHER
    }

    public WARCLoader(String warcFileName){
        this.warcFileName = warcFileName;
        this.documents = new ArrayList<>();
        isNewWARCContent = true;
        init();
    }

    public ArrayList<Document> getDocuments(){
        return this.documents;
    }

    private void init(){
        loadFile();
    }

    private void loadFile(){
        boolean isSkip = false;

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(this.warcFileName))){
            documents = new ArrayList<>();

            String line = "";
            while((line = bufferedReader.readLine()) != null){
                if(line.matches(WARC_REGEX_START_STRING)||isNewWARCContent){
                    isNewWARCContent = false;
                    WARCType warcType = this.getWARCType(bufferedReader.readLine());
                    //Skip if warc type is INFO
                    isSkip = !(warcType==WARCType.RESPONSE);
                }
                if(!isSkip){
                    //Forward to HTML Section
                    while(!(line = bufferedReader.readLine()).startsWith("HTTP/"));

                    int statusCode = getStatusCode(line);
                    if(statusCode!=200) {
                        isSkip = true;
                        continue;
                    }
                    //Forward to <html>
                    while(true){
                        String last = line;
                        line = bufferedReader.readLine();
                        if(line == null)
                            break;
                        if(line.startsWith("<html")){
                            break;
                        }
                    }

                    //Extract HTML Content
                    Document document = this.buildDocument(bufferedReader, line);
                    System.out.println("Document built Count:"+documents.size());
                    documents.add(document);
                }
            }
        }catch (IOException exception){
            System.out.println("[ERROR-LOAD] An IOException occurred: "+exception.getMessage());
        }
    }

    private WARCType getWARCType(String line){
        final String RESPONSE_REGEX = "[ ]*response";
        StringTokenizer tokenizer = new StringTokenizer(line,":");
        String token = tokenizer.nextToken();
        token = tokenizer.nextToken();
        if(token.matches(RESPONSE_REGEX))
            return WARCType.RESPONSE;
        return  WARCType.OTHER;

    }

    private int getStatusCode(String line){
        StringTokenizer tokenizer = new StringTokenizer(line,"/");
        if(!tokenizer.nextToken().startsWith("HTTP"))
            return -1;
        String token = tokenizer.nextToken(" ");
        token = tokenizer.nextToken();
        return Integer.valueOf(token);
    }

    private InputStream extractHTMLContent(BufferedReader reader, String firstLine){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            stringBuilder.append(firstLine);
            while (true) {
                String line = reader.readLine();
                if(line==null)
                    break;
                if(line.matches(WARC_REGEX_START_STRING)){
                    isNewWARCContent = true;
                    break;
                }
                stringBuilder.append(line);
            }
        }catch(IOException exception){
            System.out.println("[ERROR] An IOException occurred: "+exception.getMessage());
            System.out.println("Possibly be corrupted HTML content.");
        }
        return new ByteArrayInputStream(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
    }

    private Document buildDocument(BufferedReader reader, String firstLine){
        final String FIELD_NAME_CONTENT = "content";

        Document documentToBeBuilt = new Document();
        InputStream inputStream = extractHTMLContent(reader, firstLine);
        String body = this.parseHTML(inputStream, documentToBeBuilt);

        documentToBeBuilt.add(new TextField(FIELD_NAME_CONTENT, body, Field.Store.NO));

        return documentToBeBuilt;
    }

    private String parseHTML(InputStream inputStream, Document document){
        final String PROPERTY_TITLE = "title";

        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        BodyContentHandler handler = new BodyContentHandler();
        HtmlParser parser = new HtmlParser();

        try {
            parser.parse(inputStream, handler, metadata, context);
        }catch (TikaException|IOException|SAXException exception){
            System.out.println("[ERROR-TIKA] An Exception occurred: "+exception.getMessage());
        }

        System.out.print("Metadata:");
        System.out.println(Arrays.toString(metadata.names()));

        if(metadata.get(PROPERTY_TITLE)!=null)
            document.add(new StringField(PROPERTY_TITLE,metadata.get(PROPERTY_TITLE), Field.Store.NO));
        return handler.toString();
    }

    private String warcFileName;
    private ArrayList<Document> documents;
    private boolean isNewWARCContent;
}