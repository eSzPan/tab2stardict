package stardict;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;

/******************************************************************************************
 *
 * tab2stardict - converting file from tab to stardict format version 2.4.2  in Java
 *
 * Author: Szczepan Panek
 * e-mail: sz.panek.sanok@gmail.com
 * ver: 1.02
 * last update: 11.04.2023
 *
 ******************************************************************************************/

public class tab2stardict {
    public static void convert(File from) {

        try {
            String dictName = JOptionPane.showInputDialog("enter the name of the dictionary");
            String dir = "" + from;
            File dict = new File(dir + ".dict");
            File idx = new File(dir + ".idx");
            File ifo = new File(dir + ".ifo");

            BufferedReader br = new BufferedReader(new FileReader(from));
            FileWriter dictWriter = new FileWriter(dict);
            FileWriter ifoWriter = new FileWriter(ifo);
            FileOutputStream idxWriter = new FileOutputStream(idx);

            ArrayList<String> lines = new ArrayList<>();
            ArrayList<String> words = new ArrayList<>();
            ArrayList<Integer> offsets = new ArrayList<>();
            ArrayList<Integer> sizes = new ArrayList<>();


            String line;
            while ((line = br.readLine()) != null && line.length() >3) {
                lines.add(line);
            }
            Collections.sort(lines);

            int offset = 0;
            for (int i = 0; i < lines.size(); i++) {
                String[] parts = lines.get(i).split("\\t");
                words.add(parts[0]);
                offsets.add(offset);
                dictWriter.write(parts[1]);
                offset += (parts[1]. getBytes(). length );
                sizes.add((int) parts[1]. getBytes(). length);
            }

            br.close();
            dictWriter.close();

            for (int i = 0; i < words.size(); i++) {
                byte[] offsetsBytes = ByteBuffer.allocate(4).putInt(offsets.get(i)).array();
                byte[] sizesBytes = ByteBuffer.allocate(4).putInt(sizes.get(i)).array();

                idxWriter.write(words.get(i).getBytes("UTF8"));
                idxWriter.write(0x0);
                idxWriter.write(offsetsBytes, 0, 4);
                idxWriter.write(sizesBytes, 0, 4);
            }

            ifoWriter.write("StarDict's dict ifo file\n");
            ifoWriter.write("version=2.4.2\n");
            ifoWriter.write("wordcount=" + words.size() + "\n");
            ifoWriter.write("idxfilesize=" + (int) idxWriter.getChannel().size() + "\n");
            ifoWriter.write("bookname= "+ dictName + " by eSz\n");
            ifoWriter.write("sametypesequence=m\n");

            ifoWriter.close();
            idxWriter.close();

            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "done, conversion complete!\n files *.dict *.idx *.info\n have been created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 1) {
            File in = new File(args[0]);

            convert(in);
            System.exit(0);
        }

        if (args.length == 0) {
            JOptionPane.showMessageDialog(null, "converting from tab to stardict format.\n" +
                    "author: Szczepan Panek\n2023 r.");

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("choose a file");

            int result = chooser.showOpenDialog(null);
            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File in = chooser.getSelectedFile();

            if (result != JFileChooser.APPROVE_OPTION) {
                return;
            }
            convert(in);

        }
    }
}
