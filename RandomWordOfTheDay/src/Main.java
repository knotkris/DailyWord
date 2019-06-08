public class Main {

    public static void main(String[] args) {
        //emailing client to send emails to user
        Thread emailThread = new Thread(
                new EmailClient(
                "k.sneed777@gmail.com",
                "k.sneed717@gmail.com",
                "This is a test!!!",
                "TEST",
                "DailyContent012@gmail.com",
                "NeedAPassword4SendingEmails7")//NeedAPassword4SendingEmails7
                );
        emailThread.start();
    }
}
