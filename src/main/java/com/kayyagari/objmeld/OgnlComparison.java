package com.kayyagari.objmeld;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.swingworker.SwingWorker;
import org.jmeld.ui.AbstractContentPanel;
import org.jmeld.util.node.JMDiffNode;

/**
 * @author Kiran Ayyagari (kayyagari@apache.org)
 */
public class OgnlComparison extends SwingWorker<String, Object> {
    private JPanel mainPanel;
    private JMDiffNode diffNode;
    private String leftFile;
    private String rightFile;
    private OgnlBufferDiffPanel panel;
    private AbstractContentPanel contentPanel;
    private String contentId;

    private boolean openInBackground;
    private boolean showLevenstein;
    private boolean showTree;

    public OgnlComparison(JPanel mainPanel, String leftFile, String rightFile) {
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
            diffNode = new JMDiffNode("abcd", true);
            diffNode.setBufferNodeLeft(new OgnlNode("left", leftFile));
            diffNode.setBufferNodeRight(new OgnlNode("right", rightFile));

            contentId = "BufferDiffPanel:" + diffNode.getId();
            //contentPanel = JMeldPanel.getAlreadyOpen(mainPanel.getTabbedPane(), contentId);
            if (contentPanel == null) {
              SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                  diffNode.diff();
                }
              });
            }
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
                JOptionPane.showMessageDialog(mainPanel, result, "Error opening file", JOptionPane.ERROR_MESSAGE);
            } else {
                if (contentPanel != null) {
                    // Already opened!
                    //mainPanel.getTabbedPane().setSelectedComponent(contentPanel);
                } else {
                    panel = new OgnlBufferDiffPanel(mainPanel);
                    panel.setId(contentId);
                    panel.setDiffNode(diffNode);
                    mainPanel.add(panel);
//                    mainPanel.getTabbedPane().addTab(panel.getTitle(), ImageUtil.getSmallImageIcon("stock_new"), panel);
//                    if (!openInBackground) {
//                        mainPanel.getTabbedPane().setSelectedComponent(panel);
//                    }

                    SwingUtilities.invokeLater(doGoToFirst());
                }
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
        JPanel mainPanel = new JPanel(new BorderLayout());
        JFrame frame = new JFrame();
        OgnlComparison ognlComparison = new OgnlComparison(mainPanel, "hello\nthis is left", "hello7 this \n right");
        ognlComparison.setOpenInBackground(true);
        ognlComparison.execute();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel.setSize(100, 100);
        frame.getContentPane().add(mainPanel);
        //frame.pack();
        frame.setVisible(true);
    }
}
