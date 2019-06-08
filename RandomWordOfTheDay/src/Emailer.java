public interface Emailer {
    final String messageTemplate = "<h1>Your Daily Word</h1><h3>Word: %1$s</h3><h4>Definition: %2$s</h4><span>For more information visit <a href=https://www.dictionary.com/e/word-of-the-day>Dictionary.com Word of the Day</a></span>";
    public boolean sendEmail();
    default String getContent() {
        return "No content provided";
    }
    public String getWordContent(String content);
}
