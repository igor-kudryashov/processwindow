# processwindow

Java Console inplementation for run Java Agents in Lotus Notes Client environment

## Example 

```java
import java.util.Locale;

import com.github.ik.processwindow.ProcessWindow;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;

import lotus.domino.*;

public class JavaAgent extends AgentBase {

    public void NotesMain() {
        Session session = null;
        try {
            session = getSession();
            // to create content data I use the "Java Faker" project https://github.com/DiUS/java-faker
            FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en-GB"), new RandomService());
            ProcessWindow pw = new ProcessWindow();
            pw.setTitle("Processing console");
            
            // It is possible to use Indeterminate mode for ProgressBar
            // pw.setIndeterminate(true);
            
            // The maximum value for the ProgressBar. 
            // DocumentCollection.getCount() can be used here.
            pw.setProgressBarMax(100);
            
            pw.setProgressVisible(true);
            // The console cannot be closed. 
            // The user must first abort the process by clicking the "Cancel" button.
            pw.setMode(ProcessWindow.MODE_CANCELABLE);
            
            pw.show();
            pw.println("Start process");
            for (int i = 0; i <= 100; i++) {
                // Generate data
                Faker faker = new Faker();
                // Printing generated data to the console 
                pw.println(i + ". " + faker.name().fullName() + " (" + faker.internet().emailAddress() + ")");
                // Let's wait a little bit, so it doesn't happen too fast.
                sleep(100);
                // Increasing the ProgressBar                
                pw.setProgress(i);
                if (pw.isCanceled()) {
                    // If the user clicks cancel, we abort the process.
                    pw.setMode(ProcessWindow.MODE_CLOSEABLE);
                    break;
                }

            }            
            if (pw.isCanceled()) {
                // The user clicked the "Cancel" button
                pw.println("Canceled");                
            } else {
                // The process has ended
                pw.println("Done");
            }

            // The console can be closed
            pw.setMode(ProcessWindow.MODE_CLOSEABLE);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (session != null)
                try {
                    session.recycle();
                } catch (NotesException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
    }
}
```
