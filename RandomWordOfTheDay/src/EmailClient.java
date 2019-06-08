import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Authenticator;

public class EmailClient implements Emailer, Runnable {
    private String sender;
    private String recipient;
    private String content;
    private String subject;
    private String host;
    private Properties properties;
    private Session session;

    public EmailClient(String sender, String recipient, String content, String subject, String user, String password){
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.subject = subject;
        host = "smtp.gmail.com";
        properties = System.getProperties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", "587");
        session = Session.getDefaultInstance(
                properties,
                new Authenticator(){
                    protected PasswordAuthentication getPasswordAuthentication()
                    {
                        return new PasswordAuthentication(user, password);
                    }
                });
    }

    @Override
    public void run() {
        if(sendEmail()){
            System.out.println("Message sent to " + recipient + " successfully");
        }else{
            System.out.println("Failed to send message to " + recipient);
        }
    }

    @Override
    public String getContent() {
        try{
            StringBuilder result = new StringBuilder();

            URL wordOfTheDayURL = new URL("https://www.dictionary.com/e/word-of-the-day/");
            HttpURLConnection connection = (HttpURLConnection) wordOfTheDayURL.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String line;
            while((line = reader.readLine()) != null){
                result.append(line);
            }
            return result.toString();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getWordContent(String content) {
        Pattern recentWordPattern = Pattern.compile("(wotd-item__definition[\\W\\w]+?See)");
        Pattern wordPattern = Pattern.compile("h1>[\\w\\W]+?h1");
        Pattern defPattern = Pattern.compile("wotd-item__definition__text[\\w\\W]+?div");
        Matcher matcher = recentWordPattern.matcher(content);
        String container = "";
        String word = "";
        String definition = "";
        if(matcher.find()){
            container = content.substring(matcher.start(), matcher.end());
            matcher = wordPattern.matcher(container);
            if(matcher.find()){
                word = container.substring(matcher.start(), matcher.end());
                word = word.split("[<>]+")[1];
                word = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
            }
            matcher = defPattern.matcher(container);
            if(matcher.find()){
                definition = container.substring(matcher.start(), matcher.end());
                definition = definition.split("[><]+")[1].trim();
            }
        }
        return word + ";" + definition;
    }

    @Override
    public boolean sendEmail() {
        //https://www.dictionary.com/e/word-of-the-day/
        //look up the word on wikipedia and get extra information
        try {
            System.out.println("Setting up message");
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            String content = getContent();
            String[] wordDef = getWordContent(content).split(";");
            message.setContent(String.format(messageTemplate, wordDef[0], wordDef[1]), "text/html");
            System.out.println("Sending message to " + recipient);
            Transport.send(message);
            return true;
        }catch(MessagingException ex){
            ex.printStackTrace();
            return false;
        }
    }

}
