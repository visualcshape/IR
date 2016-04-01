package org.ntut.IR.hw1;

import org.apache.lucene.document.*;
import org.apache.sis.internal.jdk7.StandardCharsets;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import javax.sound.sampled.Line;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Vodalok on 2016/3/26.
 */

public class WARCLoader {
    private static final String WARC_REGEX_START_STRING = "WARC/([\\d].[\\d][\\d]*)";
    private final int HTTP_OK_CODE = 200;
    private ProgressHelper progressHelper;
    private long lineCount;

    public enum WARCType{
        INFO,
        RESPONSE,
        OTHER
    }

    public WARCLoader(String warcFileName){
        this.warcFileName = warcFileName;
        this.documents = new ArrayList<>();
        isNewWARCContent = true;
        this.isShowProgress = true;
        this.progressHelper = new ProgressHelper("Loading to Document Collection...", "Documents Loaded.");
        try {
            this.lineCount = org.ntut.IR.hw1.Utility.countFileLine(warcFileName);
        }catch (IOException exception){
            System.out.println("[ERROR-LOAD] An IOException occurred: "+exception.getMessage());
        }
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

        try(LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(this.warcFileName))){
            documents = new ArrayList<>();

            String line = "";
            while((line = lineNumberReader.readLine()) != null){
                if(line.matches(WARC_REGEX_START_STRING)||isNewWARCContent){
                    isNewWARCContent = false;
                    WARCType warcType = this.getWARCType(lineNumberReader.readLine());
                    //Skip if warc type is INFO
                    isSkip = !(warcType==WARCType.RESPONSE);
                }
                if(!isSkip){
                    //Forward to HTML Section
                    while(!(line = lineNumberReader.readLine()).startsWith("HTTP/"));

                    int statusCode = getStatusCode(line);
                    if(statusCode!=HTTP_OK_CODE) {
                        isSkip = true;
                        continue;
                    }
                    //Forward to <html>
                    while(true){
                        String last = line;
                        line = lineNumberReader.readLine();
                        if(line == null)
                            break;
                        if(line.startsWith("<html")){
                            break;
                        }
                    }

                    showProgress(lineNumberReader.getLineNumber());

                    //Extract HTML Content
                    Document document = this.buildDocument(lineNumberReader, line);
                    documents.add(document);

                }
            }
            showProgress(lineNumberReader.getLineNumber());
            lineNumberReader.close();
        }catch (IOException exception){
            System.out.println("[ERROR-LOAD] An IOException occurred: "+exception.getMessage());
        }
    }

    private void showProgress(int curLine) {
        if(isShowProgress){
            progressHelper.printProgress(curLine, lineCount);
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

    private InputStream extractHTMLContent(LineNumberReader reader, String firstLine){
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

    private Document buildDocument(LineNumberReader reader, String firstLine){
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

        if(metadata.get(PROPERTY_TITLE)!=null)
            document.add(new StringField(PROPERTY_TITLE,metadata.get(PROPERTY_TITLE), Field.Store.NO));
        return handler.toString();
    }

    public void setShowProgress(boolean showProgress){
        this.isShowProgress = showProgress;
    }

    private String warcFileName;
    private ArrayList<Document> documents;
    private boolean isNewWARCContent;
    private boolean isShowProgress;
}
