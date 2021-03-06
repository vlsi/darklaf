/*
 * MIT License
 *
 * Copyright (c) 2020 Jannis Weis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ui.text;

import com.github.weisj.darklaf.LafManager;

import javax.swing.*;

public class TextDemo {

    public static void main(final String[] args) {
        /*Todo Rework Demo:
         * Split up into: JTextField, JFormattedTextField, JTextArea, JEditorPane, JTextPane, JPasswordField.
         */
        SwingUtilities.invokeLater(() -> {
            LafManager.install();
            JFrame f = new JFrame();
            f.setTitle("Text Test");
            JPanel p = new JPanel();
            p.add(new JTextField("Test"));
            p.add(new JPasswordField("Test"));
            p.add(new JFormattedTextField("Test"));
            p.add(new JTextArea("Test\n" + "Test\n" + "Test\n" + "Test..............\n"));
            p.add(new JTextPane() {{setText("Test\n" + "Test\n" + "Test\n" + "Test..............\n");}});
            p.add(new JEditorPane() {{setText("Test\n" + "Test\n" + "Test\n" + "Test..............\n");}});
            f.setContentPane(p);
            f.setSize(400, 400);
            f.pack();
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
