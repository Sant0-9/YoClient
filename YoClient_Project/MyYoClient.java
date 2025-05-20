import java.io.IOException;
import java.util.Random;

public class MyYoClient extends YoClient {

    // Track counts
    private int sentYoCount = 0;
    private int sentHowdyCount = 0;
    private int recvYoCount = 0;
    private int recvHowdyCount = 0;

    // For tracking cool factor message display
    private int totalReceivedFromOthers = 0;

    // For tracking consecutive Yo’s sent
    private int consecutiveYoSent = 0;

    // Reminder messages for sending Howdy
    private final String[] howdyReminders = {
            "Yo, the 'Send Howdy' button is a thing.",
            "The 'Send Yo' button is getting a little tired, don't you think?",
            "Try sending a 'Howdy' to everyone."
    };

    private final Random random = new Random();

    // Entry point of the application. Initializes UI and connects to the server.
    public static void main(String[] args) {
        MyYoClient client = new MyYoClient();
        try {
            client.initUI();
            if (client.connect()) {
                client.listen();
            }
        } catch (IOException e) {
            client.updateGUIConsole("-- Error launching app: " + e.getMessage());
        }
    }

    // Called whenever a message is received from the server.
    // Tracks sent/received Yo and Howdy messages, triggers reminders and cool
    // factor updates.
    @Override
    public void updateStats(char codeCh) {
        switch (codeCh) {
            case SEND_YO:
                sentYoCount++;
                consecutiveYoSent++;
                if (consecutiveYoSent % 5 == 0) {
                    // After every 5 Yo's sent, show a random Howdy reminder
                    int index = random.nextInt(howdyReminders.length);
                    updateGUIConsole("[Reminder] " + howdyReminders[index]);
                }
                break;

            case SEND_HOWDY:
                sentHowdyCount++;
                consecutiveYoSent = 0; // reset consecutive Yo count
                break;

            case RECV_YO:
                recvYoCount++;
                totalReceivedFromOthers++;
                checkCoolFactor();
                break;

            case RECV_HOWDY:
                recvHowdyCount++;
                totalReceivedFromOthers++;
                checkCoolFactor();
                break;

            default:
                updateGUIConsole("-- Unknown code received in updateStats: " + codeCh);
                break;
        }
    }

    // Calculates and displays the cool factor after every 10 messages received from
    // others.
    private void checkCoolFactor() {
        if (totalReceivedFromOthers % 10 == 0) {
            // After every 10 received messages, calculate and show cool factor
            int total = recvHowdyCount + recvYoCount;
            double coolFactor = total == 0 ? 0.0 : (double) recvHowdyCount / total;
            updateGUIConsole(String.format("[Cool Factor Update] Current cool factor: %.2f", coolFactor));
        }
    }

    // Called on disconnect. Displays the final tallied statistics.
    @Override
    public void finalStats() {
        updateGUIConsole("===== Final Stats =====");
        updateGUIConsole("Yo's sent: " + sentYoCount);
        updateGUIConsole("Howdy's sent: " + sentHowdyCount);
        updateGUIConsole("Yo's received: " + recvYoCount);
        updateGUIConsole("Howdy's received: " + recvHowdyCount);
        int total = recvHowdyCount + recvYoCount;
        double finalCoolFactor = total == 0 ? 0.0 : (double) recvHowdyCount / total;
        updateGUIConsole(String.format("Final cool factor: %.2f", finalCoolFactor));
    }
}
