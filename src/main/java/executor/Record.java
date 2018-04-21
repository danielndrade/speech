package executor;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class Record {

    // record duration, in milliseconds
    static final long RECORD_TIME = 15000;  // 1 minute

    // path of the wav file
    File wavFile = new File("C:\\Cloud\\daniel6.wav");

    // format of audio file
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    // the line from which audio data is captured
    TargetDataLine line;

    /**
     * Defines an audio format
     */
    AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
        return format;
    }

    /**
     * Captures the sound and record into a WAV file
     */
    void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing

            System.out.println("Start capturing...");

            AudioInputStream ais = new AudioInputStream(line);

            System.out.println("Start recording...");

            // start recording
            AudioSystem.write(ais, fileType, wavFile);

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Closes the target data line to finish capturing and recording
     */
    void finish() {
        line.stop();
        line.close();
        System.out.println("Finished");
    }

    public byte[] extractAudioFromFile (AudioInputStream audioInputStream) {
        final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[4096];
            int counter;
            while ((counter = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
                if (counter > 0) {
                    byteOut.write(buffer, 0, counter);
                }
            }
            audioInputStream.close();
            byteOut.close();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }// end catch

        return ((ByteArrayOutputStream) byteOut).toByteArray();
    }

    /**
     * Entry to run the program
     */
    public static void main(String[] args) {
        final Record recorder = new Record();

        // creates a new thread that waits for a specified
        // of time before stopping
        Thread stopper = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(RECORD_TIME);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                recorder.finish();
            }
        });

        stopper.start();
        // start recording
        recorder.start();
    }

}
