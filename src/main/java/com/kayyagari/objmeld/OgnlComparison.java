package com.kayyagari.objmeld;

import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.jdesktop.swingworker.SwingWorker;
import org.jmeld.util.node.JMDiffNode;

import net.miginfocom.layout.AC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;

/**
 * @author Kiran Ayyagari (kayyagari@apache.org)
 */
public class OgnlComparison extends SwingWorker<String, Object> {
    private JPanel mainPanel;
    private JMDiffNode diffNode;
    private OgnlContent leftFile;
    private OgnlContent rightFile;
    private OgnlBufferDiffPanel panel;
    private String contentId;

    private boolean openInBackground;
    private boolean showLevenstein;
    private boolean showTree;

    public OgnlComparison(JPanel mainPanel, OgnlContent leftFile, OgnlContent rightFile) {
        this.mainPanel = mainPanel;
        this.leftFile = leftFile;
        this.rightFile = rightFile;
    }

    public boolean isShowTree() {
        return showTree;
    }

    public void setShowTree(boolean showTree) {
        this.showTree = showTree;
    }

    public boolean isShowLevenstein() {
        return showLevenstein;
    }

    public void setShowLevenstein(boolean showLevenstein) {
        this.showLevenstein = showLevenstein;
    }

    public boolean isOpenInBackground() {
        return openInBackground;
    }

    public void setOpenInBackground(boolean openInBackground) {
        this.openInBackground = openInBackground;
    }

    @Override
    public String doInBackground() {
        try {
            diffNode = new JMDiffNode(leftFile.getName(), true);
            diffNode.setBufferNodeLeft(new OgnlNode("left", leftFile));
            diffNode.setBufferNodeRight(new OgnlNode("right", rightFile));

            contentId = "BufferDiffPanel:" + diffNode.getId();
            // perform diff synchronously to prevent showing of empty documents
            diffNode.diff();
            /*
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    diffNode.diff();
                }
            });*/
        } catch (Exception ex) {
            ex.printStackTrace();

            return ex.getMessage();
        }

        return null;
    }

    @Override
    protected void done() {
        try {
            String result;

            result = get();

            if (result != null) {
                JOptionPane.showMessageDialog(mainPanel, result, "Error opening content", JOptionPane.ERROR_MESSAGE);
            } else {
                LC lc = new LC().flowX().fill().wrapAfter(1).hideMode(3).insets("0 0 0 0");
                AC ac = new AC().fill().grow(100f);
                MigLayout layout = new MigLayout(lc, ac);
                panel = new OgnlBufferDiffPanel(mainPanel);
                panel.setLayout(layout);
                panel.setId(contentId);
                panel.setDiffNode(diffNode);

                Border empty = BorderFactory.createEmptyBorder(0, 0, 0, 0);
                TitledBorder border = BorderFactory.createTitledBorder(empty, panel.getTitle(), TitledBorder.LEFT, TitledBorder.TOP);
                panel.setBorder(border);
                for(Map.Entry<String, OgnlContent> e : leftFile.children().entrySet()) {
                    OgnlContent left = e.getValue();
                    OgnlContent right = rightFile.children().get(e.getKey());
                    OgnlComparison ognlComparison = new OgnlComparison(panel, left, right);
                    ognlComparison.setOpenInBackground(false);
                    ognlComparison.execute();
                }
                mainPanel.add(panel);
                //SwingUtilities.invokeLater(doGoToFirst());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Runnable doGoToFirst() {
        return new Runnable() {
            public void run() {
                panel.doGoToFirst();
                panel.repaint();
            }
        };
    }
    
    public static void main(String[] args) {
        LC lc = new LC().flowX().wrapAfter(1).hideMode(3).insets("0 0 0 0");
        AC ac = new AC().grow(100f).fill();
        MigLayout layout = new MigLayout(lc, ac);

        JPanel mainPanel = new JPanel(layout);

        JFrame frame = new JFrame();
        frame.setBounds(10, 10, 1400, 1400);
        frame.setContentPane(new JScrollPane(mainPanel));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        for(int i=0; i < 7; i++) {
            StringContent left = new StringContent("field " + i, "this is \nleft field" + i);
            left.addChild(new StringContent("sub-field" +i, "child of left" + i));
            
            StringContent right = new StringContent("field " + i, "this is \nright field" + i);
            right.addChild(new StringContent("sub-field" +i, "child of right " + i));

            OgnlComparison ognlComparison = new OgnlComparison(mainPanel, left, right);
            ognlComparison.setOpenInBackground(true);
            ognlComparison.execute();
        }

        frame.setVisible(true);
    }
}
